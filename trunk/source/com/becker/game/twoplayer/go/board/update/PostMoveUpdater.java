package com.becker.game.twoplayer.go.board.update;

import com.becker.game.common.board.CaptureList;
import com.becker.game.common.GameContext;
import com.becker.game.twoplayer.go.GoMove;
import com.becker.game.twoplayer.go.GoProfiler;
import com.becker.game.twoplayer.go.board.GoBoard;
import com.becker.game.twoplayer.go.board.analysis.neighbor.NeighborType;
import com.becker.game.twoplayer.go.board.elements.*;

/**
 * Responsible for updating a go board after making a move.
 *
 * @author Barry Becker
 */
public class PostMoveUpdater extends PostChangeUpdater {

    PostMoveUpdater(GoBoard board, Captures captures) {
        super(board, captures);
    }

    /**
     * Update the board after move has been played.
     * @param move the move that was just made.
     */
    @Override
    public void update(GoMove move) {

        GoBoardPosition stone = (GoBoardPosition) (getBoard().getPosition(move.getToRow(), move.getToCol()));

        adjustLiberties(stone);

        CaptureList captures = determineCaptures(stone);
        move.setCaptures(captures);

        updateStringsAfterMove(stone);
        removeCaptures(captures);
        assert (stone.getString().getNumLiberties(getBoard()) > 0):
            "The placed stone "+stone+" has no liberties "+stone.getGroup() +"\n"+ getBoard().toString();
        updateGroupsAfterMove(stone);
        captures_.updateCaptures(move, true);
    }

    /**
     * Determine a list of enemy stones that are captured when this stone is played on the board.
     * In other words determine all opponent strings (at most 4) whose last liberty is at the new stone location.
     * @return list of captured stones.
     */
    private CaptureList determineCaptures(GoBoardPosition stone)
    {
        GoProfiler.getInstance().start(GoProfiler.FIND_CAPTURES);
        assert ( stone!=null );
        GoBoardPositionSet nbrs = nbrAnalyzer_.getNobiNeighbors( stone, NeighborType.ENEMY );
        CaptureList captureList = null;
        // keep track of the strings captured so we don't capture the same one twice
        GoStringSet capturedStrings = new GoStringSet();

        for (GoBoardPosition enbr : nbrs) {

            assert (enbr.isOccupied()): "enbr=" + enbr;

            GoString str = enbr.getString();
            assert ( str.isOwnedByPlayer1() != stone.getPiece().isOwnedByPlayer1()):
                    "The "+str+" is not an enemy of "+stone;
            assert ( str.size() > 0 ) : "Sting has 0 stones:" + str;

            if ( str.getNumLiberties(getBoard()) == 0 && !capturedStrings.contains(str) ) {
                capturedStrings.add( str );
                // we need to add copies so that when the original stones on the board are
                // changed we don't change the captures
                if ( captureList == null )
                    captureList = new CaptureList();

                addCaptures(captureList, str.getMembers() );
            }
        }
        GoProfiler.getInstance().stop(GoProfiler.FIND_CAPTURES);
        return  captureList;
    }

    /**
     * we need to add copies so that when the original stones on the board are
     * changed we don't change the captures
     * @return true if set is not null and not 0 sized.
     */
    private boolean addCaptures(CaptureList captureList, GoBoardPositionSet set) {
        if ( set == null )  {
            return false;
        }

        for (GoBoardPosition capture : set) {
            // make sure none of the captures are blanks
            assert capture.isOccupied();
            captureList.add(capture.copy());
        }
        return (!set.isEmpty());
    }

    /**
     * Examine the neighbors of this added stone and determine how the strings have changed.
     * For strings: examine the strongly connected neighbors. If more than one string borders, then
     * we merge the strings. If only one borders, then we add this stone to that string. If no strings
     * touch the added stone, then we create a new string containing only this stone.
     * @param stone the stone that was just placed on the board.
     */
    private void updateStringsAfterMove( GoBoardPosition stone )  {
        GoProfiler profiler = GoProfiler.getInstance();
        profiler.startUpdateStringsAfterMove();

       GoBoardPositionSet nbrs = nbrAnalyzer_.getNobiNeighbors( stone, NeighborType.FRIEND );

        if ( nbrs.size() == 0 ) {
            // there are no strongly connected neighbors, create a new string
            new GoString( stone, getBoard());  // stone points to the new string
        }
        else {
            updateNeighborStringsAfterMove(stone, nbrs);
        }
        cleanupGroups();
        profiler.stopUpdateStringsAfterMove();
    }

    /**
     * There is at least one neighbor string, so we will join to it/them.
     * @param stone position where we just placed a stone.
     * @param nbrs
     */
    private void updateNeighborStringsAfterMove(GoBoardPosition stone, GoBoardPositionSet nbrs) {

        GoBoardPosition nbrStone = nbrs.getOneMember();
        GoString str = nbrStone.getString();
        str.addMember( stone, getBoard() );
        getAllGroups().debugPrint( 3, "groups before merging:", true, true);

        if ( nbrs.size() > 1 ) {
            mergeStringsIfNeeded(str, nbrs);
        }
    }

    /**
     * Then we probably need to merge the strings.
     * We will not, for example, if we are completing a clump of four.
     */
    private void mergeStringsIfNeeded(GoString str, GoBoardPositionSet nbrs) {

        for (GoBoardPosition nbrStone: nbrs ) {
            // if its the same string then there is nothing to merge
            GoString nbrString = nbrStone.getString();
            if ( str != nbrString )   {
                str.merge(nbrString, getBoard());
            }
        }
    }

    /**
     * Make the positions on the board represented by the captureList show up empty.
     * Afterwards these empty spaces should not belong to any strings.
     */
    private void removeCaptures(CaptureList captureList) {

        if ( captureList == null ) return;
        for (Object aCaptureList : captureList) {
            GoBoardPosition capStone = (GoBoardPosition) aCaptureList;
            GoBoardPosition stoneOnBoard =
                (GoBoardPosition) getBoard().getPosition(capStone.getRow(), capStone.getCol());
            stoneOnBoard.clear(getBoard());
        }

        adjustStringLiberties(captureList);
    }

    /**
     * First remove all the groups on the board.
     * Then for each stone, find its group and add that new group to the board's group list.
     * Continue until all stone accounted for.
     */
    private void updateGroupsAfterMove(GoBoardPosition pos) {

        GoProfiler profiler = GoProfiler.getInstance();
        profiler.startUpdateGroupsAfterMove();

        if (GameContext.getDebugMode() > 1) {
            getAllGroups().confirmAllStonesInUniqueGroups();
        }

        recreateGroupsAfterChange();

        getBoard().unvisitAll();

        // verify that the string to which we added the stone has at least one liberty
        assert (pos.getString().getNumLiberties(getBoard()) > 0):
                "The placed stone "+pos+" has no liberties "+pos.getGroup();

        if ( GameContext.getDebugMode() > 1 )
            validator_.consistencyCheck(pos);

        // this gets used when calculating the worth of the board
        getBoard().updateTerritory(false);

        if ( GameContext.getDebugMode() > 1 )
            validator_.consistencyCheck(pos);

        profiler.stopUpdateGroupsAfterMove();
    }
}