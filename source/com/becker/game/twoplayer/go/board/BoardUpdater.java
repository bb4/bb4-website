package com.becker.game.twoplayer.go.board;

import com.becker.game.twoplayer.go.board.analysis.GoBoardUtil;
import com.becker.game.twoplayer.go.board.analysis.NeighborAnalyzer;
import com.becker.game.common.BoardPosition;
import com.becker.game.common.CaptureList;
import com.becker.game.common.GameContext;
import com.becker.game.twoplayer.go.GoMove;
import com.becker.game.twoplayer.go.GoProfiler;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

/**
 * Responsible for updating a go board after making or undoing a move.
 * 
 * @author Barry Becker
 */
public class BoardUpdater {

    PostMoveUpdater postMoveUpdater_;
    PostRemoveUpdater postRemoveUpdater_;
    Captures captures_;

    /**
     * Constructor
     */
    BoardUpdater(GoBoard board) {

        captures_ = new Captures();
        postMoveUpdater_ = new PostMoveUpdater(board, captures_);
        postRemoveUpdater_ = new PostRemoveUpdater(board, captures_);
    }

    public int getNumCaptures(boolean player1StonesCaptured) {
        return captures_.getNumCaptures(player1StonesCaptured);
    }
    
    /**
     * Update the board after move has been played.
     * @param move the move that was just made
     */
    public void updateAfterMove(GoMove move) {

        postMoveUpdater_.update(move);
    }

    /**
     * Update the board after backing out a move.
     * @param move the move that was just undone
     */
    public void updateAfterRemove(GoMove move) {

        postRemoveUpdater_.update(move);
    }
}
