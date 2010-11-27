package com.becker.game.twoplayer.go.board.update;

import com.becker.game.common.board.CaptureList;
import com.becker.game.twoplayer.go.GoMove;
import com.becker.game.twoplayer.go.board.BoardValidator;
import com.becker.game.twoplayer.go.board.GoBoard;
import com.becker.game.twoplayer.go.board.analysis.neighbor.NeighborAnalyzer;
import com.becker.game.twoplayer.go.board.elements.*;

import java.util.List;

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


    /**
     * Update the board information data after a change has been made (like an add or a remove of a stone)
     * @param board board that changed.
     * @param captures captures added or removed during the change
     */
    PostChangeUpdater(GoBoard board, Captures captures) {
        board_ = board;
        captures_ = captures;
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
     * update the liberties of the surrounding strings
     * @param captureList the liberties of the stones in this list will be adjusted.
     */
    void adjustStringLiberties(CaptureList captureList) {
        for (Object capture : captureList) {
            GoBoardPosition captured = (GoBoardPosition) capture;
            GoBoardPosition newLiberty = (GoBoardPosition) board_.getPosition(captured.getRow(), captured.getCol());
            adjustLiberties(newLiberty);
        }
    }

    /**
     * Adjust the liberties on the strings (both black and white) that we touch.
     * @param liberty  - either occupied or not depending on if we are placing the stone or removing it.
     */
    void adjustLiberties(GoBoardPosition liberty) {

         NeighborAnalyzer na = new NeighborAnalyzer(board_);
         GoStringSet stringNbrs = na.findStringNeighbors( liberty );
         for (GoString sn : stringNbrs) {
             sn.changedLiberty(liberty);
         }
    }

    /**
     * remove groups that have no stones in them.
     */
    void cleanupGroups()
    {
        GoGroupSet newGroups = new GoGroupSet();

        for (GoGroup group: getAllGroups()) {

            if ( group.getNumStones() > 0 )  {
                newGroups.add(group);
            }
        }
        board_.setGroups(newGroups);
    }

    /**
     * Remove all the groups in groups_ corresponding to the specified list of stones.
     * @param stones the stones to remove.
     */
    void removeGroupsForListOfStones(List stones) {
        GoGroupSet groupsCopy = new GoGroupSet(getAllGroups());
        for (Object stone : stones) {
            GoBoardPosition nbrStone = (GoBoardPosition) stone;
            // In the case where the removed stone was causing an atari in a string in an enemy group,
            // there is a group that does not contain a nbr stone that also needs to be removed here.
            groupsCopy.remove(nbrStone.getGroup());
        }
        board_.setGroups(groupsCopy);
    }
}