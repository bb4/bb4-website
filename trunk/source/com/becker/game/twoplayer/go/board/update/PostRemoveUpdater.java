package com.becker.game.twoplayer.go.board.update;

import com.becker.game.common.board.CaptureList;
import com.becker.game.common.GameContext;
import com.becker.game.twoplayer.go.GoMove;
import com.becker.game.twoplayer.go.GoProfiler;
import com.becker.game.twoplayer.go.board.GoBoard;
import com.becker.game.twoplayer.go.board.GoCaptureList;
import com.becker.game.twoplayer.go.board.analysis.GoBoardUtil;
import com.becker.game.twoplayer.go.board.analysis.neighbor.NeighborType;
import com.becker.game.twoplayer.go.board.elements.*;

import java.util.*;

/**
 * Responsible for updating a go board after undoing a move.
 *
 * @author Barry Becker
 */
public class PostRemoveUpdater extends PostChangeUpdater {

    PostRemoveUpdater(GoBoard board, Captures captures) {
        super(board, captures);
    }

    /**
     * Update strings and groups after a move was undone.
     * @param move move that was just removed.
     */
    @Override
    public void update(GoMove move) {

        profiler_.startUpdateGroupsAfterRemove();
        GoBoardPosition stone = (GoBoardPosition) (getBoard().getPosition(move.getToLocation()));

        GoString stringThatItBelongedTo = stone.getString();
        // clearing a stone may cause a string to split into smaller strings
        stone.clear(getBoard());
        board_.adjustLiberties(stone);

        updateStringsAfterRemove( stone, stringThatItBelongedTo);
        restoreCaptures((GoCaptureList)move.getCaptures());

        profiler_.startRecreateGroupsAfterRemove();
        recreateGroupsAfterChange();
        profiler_.stopRecreateGroupsAfterRemove();

        captures_.updateCaptures(move, false);
        profiler_.stopUpdateGroupsAfterRemove();
    }

    /**
     * update the strings after a stone has been removed.
     * Some friendly strings may have been split by the removal.
     * @param stone that was removed.
     * @param string that the stone belonged to.
     */
    private void updateStringsAfterRemove( GoBoardPosition stone, GoString string ) {
        GoProfiler profiler = GoProfiler.getInstance();
        profiler.startUpdateStringsAfterRemove();

        // avoid error when calling from treeDlg
        if (string == null) return;

        splitStringsIfNeeded(stone, string);

        if ( GameContext.getDebugMode() > 1 ) {
            getAllGroups().confirmNoEmptyStrings();
            validator_.confirmStonesInValidGroups();
        }
        profiler.stopUpdateStringsAfterRemove();
    }

    /**
     * Make new string(s) if removing the stone has caused a larger string to be split.
     * @param stone that was removed.
     * @param string that the stone belonged to.
     */
    private void splitStringsIfNeeded(GoBoardPosition stone, GoString string) {

        GoGroup group = string.getGroup();
        GoBoardPositionSet nbrs =
            nbrAnalyzer_.getNobiNeighbors( stone, group.isOwnedByPlayer1(), NeighborType.FRIEND );

        if ( nbrs.size() > 1 ) {
            List<GoBoardPositionList> lists = new ArrayList<GoBoardPositionList>(8);
            GoBoardPosition firstNbr = nbrs.getOneMember();
            GoBoardPositionList stones = nbrAnalyzer_.findStringFromInitialPosition( firstNbr, false );
            lists.add( stones );
            for ( GoBoardPosition nbrStone : nbrs ) {
                if ( !nbrStone.isVisited() ) {
                    GoBoardPositionList stones1 = nbrAnalyzer_.findStringFromInitialPosition( nbrStone, false );
                    GoString newString = new GoString( stones1, getBoard() );
                    group.addMember( newString);
                    lists.add( stones1 );
                }
            }
            GoBoardUtil.unvisitPositionsInLists( lists );
        }
    }

    /**
     * restore this moves captures stones on the board
     * @param captures list of captures to remove.
     */
    private void restoreCaptures(GoCaptureList captures) {

        if ( captures == null || captures.isEmpty() ) return;

        captures.restoreOnBoard(board_);
        if (GameContext.getDebugMode() > 1) {
            validator_.confirmStonesInValidGroups();
            getAllGroups().confirmAllStonesInUniqueGroups();
            GameContext.log( 3, "GoBoard: undoInternalMove: " + getBoard() + "  groups after restoring captures:" );
        }
    }
}