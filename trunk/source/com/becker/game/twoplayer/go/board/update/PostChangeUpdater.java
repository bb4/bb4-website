package com.becker.game.twoplayer.go.board.update;

import com.becker.game.twoplayer.go.GoMove;
import com.becker.game.twoplayer.go.GoProfiler;
import com.becker.game.twoplayer.go.board.BoardValidator;
import com.becker.game.twoplayer.go.board.GoBoard;
import com.becker.game.twoplayer.go.board.analysis.neighbor.NeighborAnalyzer;
import com.becker.game.twoplayer.go.board.elements.*;

/**
 * Base class for classes responsible for updating a go board after making or undoing a move.
 *
 * @author Barry Becker
 */
public abstract class PostChangeUpdater {

    GoBoard board_;
    Captures captures_;
    NeighborAnalyzer nbrAnalyzer_;
    BoardValidator validator_;
    GoProfiler profiler_;

    /**
     * Update the board information data after a change has been made (like an add or a remove of a stone)
     * @param board board that changed.
     * @param captures captures added or removed during the change
     */
    PostChangeUpdater(GoBoard board, Captures captures) {
        board_ = board;
        captures_ = captures;
        profiler_ = GoProfiler.getInstance();
        nbrAnalyzer_ = new NeighborAnalyzer(board);
        validator_ = new BoardValidator(board);
    }

    /**
     * Update the strings and groups on the board after a move change (move or remove).
     * @param move the move that was just made or undone.
     */
    public abstract void update( GoMove move );

    GoBoard getBoard() {
        return board_;
    }

    GoGroupSet getAllGroups() {
        return board_.getGroups();
    }

    /**
     * The structure of the groups can change after a move.
     * First remove all the current groups then rediscover them.
     */
    protected void recreateGroupsAfterChange() {

        GoGroupSet groups = new GoGroupSet();

        for ( int i = 1; i <= getBoard().getNumRows(); i++ )  {
           for ( int j = 1; j <= getBoard().getNumCols(); j++ ) {
               GoBoardPosition seed = (GoBoardPosition)getBoard().getPosition(i, j);
               if (seed.isOccupied() && !seed.isVisited()) {
                   GoBoardPositionList newGroup = nbrAnalyzer_.findGroupFromInitialPosition(seed, false);
                   GoGroup g = new GoGroup(newGroup);
                   groups.add(g);
               }
           }
        }
        board_.setGroups(groups);
        board_.unvisitAll();
    }

    /**
     * remove groups that have no stones in them.
     */
    void cleanupGroups() {
        GoGroupSet newGroups = new GoGroupSet();

        for (GoGroup group: getAllGroups()) {

            if ( group.getNumStones() > 0 )  {
                newGroups.add(group);
            }
        }
        board_.setGroups(newGroups);
    }
}