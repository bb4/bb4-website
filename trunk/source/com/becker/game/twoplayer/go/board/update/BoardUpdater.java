package com.becker.game.twoplayer.go.board.update;

import com.becker.game.twoplayer.go.GoMove;
import com.becker.game.twoplayer.go.board.GoBoard;

/**
 * Responsible for updating a go board after making or undoing a move.
 * 
 * @author Barry Becker
 */
public class BoardUpdater {

    private PostMoveUpdater postMoveUpdater_;
    private PostRemoveUpdater postRemoveUpdater_;
    private Captures captures_;

    /**
     * Constructor
     */
    public BoardUpdater(GoBoard board) {

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
