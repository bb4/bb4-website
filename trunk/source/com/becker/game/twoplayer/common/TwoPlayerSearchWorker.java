package com.becker.game.twoplayer.common;

import com.becker.common.concurrency.Worker;
import com.becker.common.util.Util;
import com.becker.game.common.Move;

/**
 * Searches for the next computer move in a separate thread.
 *                                                                                                      O
 * @author Barry Becker
 */
public class TwoPlayerSearchWorker {

    private TwoPlayerController controller_;

    /** Worker represents a separate thread for computing the next move. */
    private Worker worker_;

    /** this is true while the computer thinks about its next move. */
    private boolean processing_ = false;


    /**
     * Construct the search worker.
     */
    public TwoPlayerSearchWorker(TwoPlayerController controller) {
        controller_ = controller;
    }

    /**
     * Apply whatever the best move is that we have found so far - even though we are not done.
     */
    public void interrupt() {
        if (isProcessing()) {
            controller_.pause();
            if (worker_!=null) {
                worker_.interrupt();
                processing_ = false;
                // make the move even though we did not finish computing it
                controller_.get2PlayerViewer().computerMoved((Move)worker_.get());
            }
            Util.sleep(100);
        }
    }

    /**
     * Request the next computer move. It will be the best move that the computer can find.
     * Launches a separate thread to do the search for the next move.
     * @param isPlayer1 true if player one to move.
     * @param synchronous if true then the method does not return until the next move has been found.
     * @return true if the game is over
     * @throws AssertionError  if something bad happenned while searching.
     */
     public boolean requestComputerMove(final boolean isPlayer1, boolean synchronous) throws AssertionError {

        worker_ = new Worker() {

            private Move move_ = null;

            @Override
            public Object construct() {
                processing_ = true;

                move_ = controller_.findComputerMove( isPlayer1 );

                return move_;
            }

            @Override
            public void finished() {
                processing_ = false;
                if (controller_.get2PlayerViewer() != null)  {
                    controller_.get2PlayerViewer().computerMoved(move_);
                }
            }
        };

        worker_.start();

        if (synchronous) {
            // this blocks until the value is available..
            TwoPlayerMove m = (TwoPlayerMove)worker_.get();
            return controller_.getSearchable().done( m, true );
        }
        return false;
    }

    /**
     *  @return true if the viewer is currently processing (i.e. searching)
     */
    public boolean isProcessing() {
        return processing_;
    }
}