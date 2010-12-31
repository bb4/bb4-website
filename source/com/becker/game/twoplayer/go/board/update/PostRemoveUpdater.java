package com.becker.game.twoplayer.go.board.update;

import com.becker.game.common.board.CaptureList;
import com.becker.game.common.GameContext;
import com.becker.game.twoplayer.go.GoMove;
import com.becker.game.twoplayer.go.GoProfiler;
import com.becker.game.twoplayer.go.board.GoBoard;
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

        GoBoardPosition stone = (GoBoardPosition) (getBoard().getPosition(move.getToLocation()));

        GoString stringThatItBelongedTo = stone.getString();
        // clearing a stone may cause a string to split into smaller strings
        stone.clear(getBoard());
        adjustLiberties(stone);

        updateStringsAfterRemove( stone, stringThatItBelongedTo);
        restoreCaptures(move.getCaptures());
        recreateGroupsAfterChange();
        getBoard().unvisitAll();

        captures_.updateCaptures(move, false);
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
        //assert (string.size() > 0) : " String has 0 members! " + string;

        if ( nbrs.size() > 1 ) {
            Iterator nbrIt = nbrs.iterator();
            List<GoBoardPositionList> lists = new ArrayList<GoBoardPositionList>(8);
            GoBoardPosition firstNbr = (GoBoardPosition) nbrIt.next();
            GoBoardPositionList stones = nbrAnalyzer_.findStringFromInitialPosition( firstNbr, false );
            lists.add( stones );
            while ( nbrIt.hasNext() ) {
                GoBoardPosition nbrStone = (GoBoardPosition) nbrIt.next();
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
    private void restoreCaptures(CaptureList captures) {
        if ( captures != null ) {
            restoreCapturesOnBoard(captures);
            updateAfterRestoringCaptures(captures);     // XXX should remove
            if (GameContext.getDebugMode() > 1) {
                validator_.confirmStonesInValidGroups();
                getAllGroups().confirmAllStonesInUniqueGroups();
                GameContext.log( 3, "GoBoard: undoInternalMove: " + getBoard() + "  groups after restoring captures:" );
            }
        }
    }

    /**
     * put the captures back on the board.
     */
    private void restoreCapturesOnBoard( CaptureList captureList ) {
        captureList.restoreOnBoard( getBoard() );

        GameContext.log( 3, "GoMove: restoring these captures: " + captureList );

        List<GoBoardPositionList> strings = getRestoredStringList(captureList);  // XXX should remove
        adjustStringLiberties(captureList);

        // XXX should remove next lines
        GoGroup group = getRestoredGroup(strings);

        assert ( group!=null): "no group was formed when restoring "
                + captureList + " the list of strings was "+strings;
        getAllGroups().add( group );
    }

    /**
     * There may have been more than one string in the captureList
     * @return list of strings that were restored ont he board.
     */
    private List<GoBoardPositionList> getRestoredStringList(CaptureList captureList) {

        GoBoardPositionList restoredList = getRestoredList(captureList);
        List<GoBoardPositionList> strings = new LinkedList<GoBoardPositionList>();

        for (GoBoardPosition s : restoredList) {
            if (!s.isVisited()) {
                GoBoardPositionList string1 = nbrAnalyzer_.findStringFromInitialPosition(s, false);
                strings.add(string1);
            }
        }
        return strings;
    }

    /**
     * @return list of captured stones that were restored on the board.
     */
    private GoBoardPositionList getRestoredList(CaptureList captureList) {
        Iterator it = captureList.iterator();
        GoBoardPositionList restoredList = new GoBoardPositionList();
        while ( it.hasNext() ) {
            GoBoardPosition capStone = (GoBoardPosition) it.next();
            GoBoardPosition stoneOnBoard =
                    (GoBoardPosition) getBoard().getPosition( capStone.getRow(), capStone.getCol() );
            stoneOnBoard.setVisited(false);    // make sure in virgin unvisited state

            // --adjustLiberties(stoneOnBoard, board);
            restoredList.add( stoneOnBoard );
        }
        return restoredList;
    }

    /**
     * @return the group that was restored when the captured stones were replaced on the board.
     */
    private GoGroup getRestoredGroup(List<GoBoardPositionList> strings) {
        // ?? form new group, or check group nbrs to see if we can add to an existing one.
        boolean firstString = true;
        GoGroup group = null;
        for  (GoBoardPositionList stringList : strings) {
            GoString string = new GoString( stringList, getBoard() );
            if ( firstString ) {
                group = new GoGroup( string );
                firstString = false;
            }
            else {
                group.addMember( string);
                //GameContext.log( 2, "GoMove: restoring ----------------" + string );
            }
            string.unvisit();
        }
        return group;
    }

    /**
     * After restoring the captures, the stones surrounding the captures will probably
     * form disparate groups rather than 1 cohesive one.
     */
    private void updateAfterRestoringCaptures( CaptureList captures) {
        if ( GameContext.getDebugMode() > 1 ) {
             validator_.confirmStonesInValidGroups();
        }

        GoBoardPositionList enemyNobiNbrs = getEnemyNeighbors(captures);
        // in some bizarre cases there might actually be no enemy nobi neighbors
        // (such as when one stone killed all the stones on the board?)
        if (enemyNobiNbrs.size() == 0) {
            GameContext.log(0, "The restored captures ("+captures+") have no enemy neighbors (very strange!)" );
            return;
        }

        if ( GameContext.getDebugMode() > 1 ) {
             validator_.confirmStonesInValidGroups();
        }
    }

    /**
     * @return  all the enemy neighbors of the stones in the captured group being restored.
     */
    private GoBoardPositionList getEnemyNeighbors(CaptureList captures) {
        GoBoardPositionList enemyNobiNbrs = new GoBoardPositionList();
        for (Object capture1 : captures) {
            GoBoardPosition capture = (GoBoardPosition) capture1;
            GoBoardPositionSet enns = nbrAnalyzer_.getNobiNeighbors(capture, NeighborType.ENEMY);
            enemyNobiNbrs.addAll(enns);
        }
        return enemyNobiNbrs;
    }
}