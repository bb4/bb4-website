package com.becker.game.twoplayer.go.board.update;

import com.becker.game.common.BoardPosition;
import com.becker.game.common.CaptureList;
import com.becker.game.common.GameContext;
import com.becker.game.twoplayer.go.GoMove;
import com.becker.game.twoplayer.go.GoProfiler;
import com.becker.game.twoplayer.go.board.*;
import com.becker.game.twoplayer.go.board.update.Captures;

import java.util.*;

/**
 * Responsible for updating a go board after making or undoing a move.
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

        GoBoardPosition stone = (GoBoardPosition) (board_.getPosition(move.getToRow(), move.getToCol()));

        adjustLiberties(stone);

        CaptureList captures = determineCaptures(stone);
        move.setCaptures(captures);

        updateStringsAfterMove(stone);
        removeCaptures(move.getToRow(), move.getToCol(), captures);
        assert (stone.getString().getNumLiberties(board_) > 0):
            "The placed stone "+stone+" has no liberties "+stone.getGroup() +"\n"+ board_.toString();
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
        board_.getProfiler().start(GoProfiler.FIND_CAPTURES);
        assert ( stone!=null );
        Set nbrs = board_.getNobiNeighbors( stone, NeighborType.ENEMY );
        CaptureList captureList = null;
        Iterator it = nbrs.iterator();
        // keep track of the strings captured so we don't capture the same one twice
        Set<GoString> capturedStrings = new HashSet<GoString>();

        while ( it.hasNext() ) {
            GoBoardPosition enbr = (GoBoardPosition) it.next();
            assert (enbr.isOccupied()): "enbr=" + enbr;

            GoString str = enbr.getString();
            assert ( str.isOwnedByPlayer1() != stone.getPiece().isOwnedByPlayer1()): "The "+str+" is not an enemy of "+stone;
            if ( str.getNumLiberties(board_) == 0 && str.size() > 0 && !capturedStrings.contains(str) ) {
                capturedStrings.add( str );
                // we need to add copies so that when the original stones on the board are
                // changed we don't change the captures
                if ( captureList == null )
                    captureList = new CaptureList();
                captureList.addAllCopied( str.getMembers() );
            }
        }
        board_.getProfiler().stop(GoProfiler.FIND_CAPTURES);
        return  captureList;
    }

    /**
     * Examine the neighbors of this added stone and determine how the strings have changed.
     * For strings: examine the strongly connected neighbors. If more than one string borders, then
     * we merge the strings. If only one borders, then we add this stone to that string. If no strings
     * touch the added stone, then we create a new string containing only this stone.
     * @param stone the stone that was just placed on the board.
     */
    private void updateStringsAfterMove( GoBoardPosition stone )
    {
        GoProfiler profiler = (GoProfiler)board_.getProfiler();
        profiler.startUpdateStringsAfterMove();

        Set<GoBoardPosition> nbrs = board_.getNobiNeighbors( stone, NeighborType.FRIEND );

        GoString str;
        if ( nbrs.size() == 0 ) {
            // there are no strongly connected nbrs, create a new string
            new GoString( stone, board_);  // stone points to the new string
        }
        else {
            // there is at least one nbr, so we join to it/them
            Iterator nbrIt = nbrs.iterator();
            GoBoardPosition nbrStone = (GoBoardPosition) nbrIt.next();
            str = nbrStone.getString();
            str.addMember( stone, board_ );
            BoardDebugUtil.debugPrintGroups( 3, "groups before merging:", true, true, board_.getGroups());

            if ( nbrs.size() > 1 ) {
                // then we probably need to merge the strings.
                // We would not, for example, if we are completing a clump of four
                while ( nbrIt.hasNext() ) {
                    // if its the same string then there is nothing to merge
                    nbrStone = (GoBoardPosition) nbrIt.next();
                    GoString nbrString = nbrStone.getString();
                    if ( str != nbrString )   {
                        str.merge( nbrString, board_ );
                    }
                }
            }
            // now that we have merged the stone into a new string, we need to verify that that string is not in atari.
            // if it is, then we need to split that ataried string off from its group and form a new group.
            if (stone.isInAtari(board_)) {
                GoGroup oldGroup = str.getGroup();
                GameContext.log(3, "Before splitting off ataried string (due to " + stone + ") containing (" +
                                   str + ") we have: " + oldGroup);

                oldGroup.remove(str);
                GoGroup newGroup = new GoGroup(str);
                //GameContext.log(3, "after splitting we have: "+newGroup);
                assert (!newGroup.getMembers().isEmpty()) : "The group we are trying to add is empty";
                board_.getGroups().add(newGroup);
            }
        }
        cleanupGroups();
        profiler.stopUpdateStringsAfterMove();
    }

    /**
     * Remove all thecaptures on the board.
     */
    private void removeCaptures(int toRow, int toCol, CaptureList captures) {
        if ( captures != null ) {
            removeCapturesOnBoard( captures );
            updateAfterRemovingCaptures( toRow, toCol );
        }
    }


    /**
     * Make the positions on the board represented by the captureList show up empty.
     * Afterwards these empty spaces should not belong to any strings.
     */
    private void removeCapturesOnBoard(CaptureList captureList)
    {
        // remove the captured strings from the owning group (there could be up to 4)
        GoString capString = ((GoBoardPosition) captureList.get( 0 )).getString();
        GoGroup group = capString.getGroup();
        Set<GoString> capStrings = new HashSet<GoString>();

        // we can't just call captureList.removeOnBoard because we need to do additional updates for go.
        for (Object aCaptureList : captureList) {
            GoBoardPosition capStone = (GoBoardPosition) aCaptureList;
            capString = capStone.getString();
            // remove the captured strings from the group
            if (!capStrings.contains(capString)) {
                capStrings.add(capString);

                group.remove(capString);

            }
            GoBoardPosition stoneOnBoard = (GoBoardPosition) board_.getPosition(capStone.getRow(), capStone.getCol());
            stoneOnBoard.clear(board_);
            // ?? restore disconnected groups?
        }

        // if there are no more stones in the group, remove it.
        if ( group.getNumStones() == 0 ) {
            board_.getGroups().remove( group );
        }

        adjustStringLiberties(captureList);
    }

    /**
     * Remove all the groups on the board.
     * Then for each stone, find its group and add that new group to the board's group list.
     * Continue until all stone accounted for.
     */
    private void updateGroupsAfterMove(GoBoardPosition pos)
    {
        GoProfiler profiler = (GoProfiler)board_.getProfiler();
        profiler.startUpdateGroupsAfterMove();

        if (GameContext.getDebugMode() > 1) {
            BoardValidationUtil.confirmAllStonesInUniqueGroups(board_.getGroups());
        }

        // remove all the current groups (we will then add them back)
        board_.getGroups().clear();

        for ( int i = 1; i <= board_.getNumRows(); i++ )  {
           for ( int j = 1; j <= board_.getNumCols(); j++ ) {
               GoBoardPosition seed = (GoBoardPosition)board_.getPosition(i, j);
               if (seed.isOccupied() && !seed.isVisited()) {
                   List newGroup = board_.findGroupFromInitialPosition(seed, false);
                   GoGroup g = new GoGroup(newGroup);
                   board_.getGroups().add(g);
               }
           }
        }
        unvisitAll();

        // verify that the string to which we added the stone has at least one liberty
        assert (pos.getString().getNumLiberties(board_) > 0): "The placed stone "+pos+" has no liberties "+pos.getGroup();

        // this gets used when calculating the worth of the board
        board_.updateTerritory(false);

        if ( GameContext.getDebugMode() > 1 ) {
            BoardValidationUtil.confirmNoEmptyStrings(board_.getGroups());
            BoardValidationUtil.confirmStonesInValidGroups(board_);
            BoardValidationUtil.confirmAllStonesInUniqueGroups(board_.getGroups());
            try {
                BoardValidationUtil.confirmAllStonesInGroupsClaimed(board_.getGroups(), board_);
            } catch (AssertionError e) {
                GameContext.log(1, "The move was :"+pos);
                throw e;
            }
        }

        profiler.stopUpdateGroupsAfterMove();
    }


    /**
     * Make sure that all the positions on the board are reset to the unvisited state.
     */
    protected void unvisitAll()
    {
        for ( int i = 1; i <= board_.getNumRows(); i++ ) {
            for ( int j = 1; j <= board_.getNumCols(); j++ ) {
                GoBoardPosition pos = (GoBoardPosition) board_.getPosition( i, j );
                pos.setVisited(false);
            }
        }
    }

    /**
     * After removing the captures, the stones surrounding the captures will form 1 (or sometimes 2)
     * cohesive group(s) rather than disparate ones.
     * There can be two if, for example, the capturing stone joins a string that is
     * still in atari after the captured stones have been removed.
     */
    private void updateAfterRemovingCaptures(int toRow, int toCol)
    {
        GoBoardPosition finalStone = (GoBoardPosition) board_.getPosition(toRow, toCol);

        GoBoardPosition seedStone;
        // Its a bit of a special case if the finalStone's string is in atari after the capture.
        // The string that the finalStone belongs to is still cut from the larger joined group,
        // but we need to determine the big group from some other stone not from finalStone's string.
        if (finalStone.isInAtari(board_)) {
            seedStone = findAlternativeSeed(finalStone);
        }
        else {
            seedStone = finalStone;
        }
        assert seedStone.isOccupied();
        List bigGroup = board_.findGroupFromInitialPosition( seedStone );
        assert ( bigGroup.size() > 0 );

        removeGroupsForListOfStones(bigGroup);

        GoGroup newBigGroup = new GoGroup( bigGroup );
        board_.getGroups().add( newBigGroup );
    }

    /**
     * We need to identify this pattern:
     *   #0
     *   0'
     *   #0
     * (or      #O
     *           O'
     *       if on the edge or corner)
     * Where the middle 0 is the stone passed in.
     * There are 4 cases to check
     * @param stone
     * @return one of the other 2 0's in the picture.
     */
    private GoBoardPosition findAlternativeSeed(GoBoardPosition stone)
    {
        // List nbrs = this.getNobiNeighbors(stone, NeighborType.ANY)
        // After we find where the blank is we can pretty much just assert the other positions.
        int r = stone.getRow();
        int c = stone.getCol();

        GoBoardPosition alternative;
        alternative = getConfirmedAlternative(stone, r, c, 0, -1);
        if (alternative != null)
           return alternative;
        alternative = getConfirmedAlternative(stone, r, c, 0, 1);
        if (alternative != null)
           return alternative;
        alternative = getConfirmedAlternative(stone, r, c, -1, 0);
        if (alternative != null)
           return alternative;
        alternative = getConfirmedAlternative(stone, r, c, 1, 0);
        if (alternative != null)
           return alternative;
        assert false : "There was no alternative seed for "+stone +" board:\n"+this;
        return stone;
    }


    /**
     * @param stone to find alternative for.
     * @return null if no alternative found
     */
    private GoBoardPosition getConfirmedAlternative(BoardPosition stone,
                                                    int r, int c, int rowOffset, int colOffset)
    {
        BoardPosition blankPos = board_.getPosition(r + rowOffset, c + colOffset);
        if (blankPos != null && blankPos.isUnoccupied()) {
            BoardPosition enemy1;
            BoardPosition enemy2;
            if (rowOffset == 0) {
                enemy1 = board_.getPosition(r + 1, c + colOffset);
                enemy2 = board_.getPosition(r - 1, c + colOffset);
            }
            else {
                assert (colOffset == 0);
                enemy1 = board_.getPosition(r + rowOffset, c + 1);
                enemy2 = board_.getPosition(r + rowOffset, c - 1);
            }
            if (enemy1 != null && enemy2 != null) {
                assert( enemy1.getPiece().isOwnedByPlayer1() == enemy2.getPiece().isOwnedByPlayer1()
                    && enemy1.getPiece().isOwnedByPlayer1() == stone.getPiece().isOwnedByPlayer1()) :
                    "unexpected ownership (e1="+enemy1.getPiece().isOwnedByPlayer1()+",e2="+enemy2.getPiece().isOwnedByPlayer1()
                        +") for "+enemy1+" and "+enemy2+" based on "+stone+". Blank="+blankPos;
            }
            if (enemy1 != null && enemy1.isOccupied())
                return (GoBoardPosition)enemy1;
            else
                return (GoBoardPosition)enemy2;
        }
        else
            return null;
    }
}