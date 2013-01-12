/** Copyright by Barry G. Becker, 2000-2011. Licensed under MIT License: http://www.opensource.org/licenses/MIT  */
package com.barrybecker4.game.twoplayer.common.ui;

import com.barrybecker4.common.format.FormatUtil;
import com.barrybecker4.game.common.GameContext;
import com.barrybecker4.game.twoplayer.common.TwoPlayerController;
import com.barrybecker4.game.twoplayer.common.search.strategy.SearchStrategy;

import javax.swing.JProgressBar;
import javax.swing.Timer;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;


/**
 * Shows progress while the computer move is being determined.
 *
 * @author Barry Becker
 */
class ComputerMoveProgress {

    private static final int PROGRESS_UPDATE_DELAY = 700;
    private static final int PROGRESS_STEP_DELAY = 100;

    private TwoPlayerController controller_;
    private JProgressBar progressBar_;

    /** Periodically updates the progress bar.  */
    private Timer timer_;

    /** becomes true when stepping through the search.   */
    private boolean stepping_ = false;

    /**
     * Constructor.
     */
    public ComputerMoveProgress(TwoPlayerController controller) {
        controller_ = controller;
    }

    public void setProgressBar(JProgressBar progressBar) {
        progressBar_ = progressBar;
    }

    /**
     * make the computer move and show it on the screen.
     * Since this can take a very long time we will show the user a progress bar
     * to give feedback.
     *   The computer needs to search through vast numbers of moves to find the best one.
     * This will happen asynchronously in a separate thread so that the event dispatch
     * thread can return immediately and not lock up the user interface (UI).
     *   Some moves can be complex (like multiple jumps in checkers). For these
     * We animate these types of moves so the human player does not get disoriented.
     *
     * @param isPlayer1 if the computer player now moving is player 1.
     * @return true if done. Always returns false unless auto optimizing
     */
    boolean doComputerMove( boolean isPlayer1 ) {

        if (progressBar_ != null) {
            // initialize the progress bar if there is one.
            progressBar_.setValue(0);
            progressBar_.setVisible(true);

            // start a thread to update the progress bar at fixed time intervals
            // The timer gets killed when the worker thread is done searching.
            timer_ = new Timer(PROGRESS_UPDATE_DELAY, new TimerListener());

            timer_.start();
        }

        // this will spawn the worker thread and return immediately (unless autoOptimize on)
        return controller_.requestComputerMove(isPlayer1);
    }


    /**
     * Currently this does not actually step forward just one search step, but instead
     * stops after PROGRESS_STEP_DELAY more milliseconds.
     */
    public final void step() {
        if (timer_ != null) {
            timer_.setDelay(PROGRESS_STEP_DELAY);
            timer_.restart();
            stepping_ = true;
            controller_.getSearchStrategy().continueProcessing();
        }
        else {
            GameContext.log(0,  "step error : timer is null" );
        }
    }

    /**
     * resume computation
     */
    public final void continueProcessing()  {
        /*
        if (controller_.getSearchStrategy()!=null) {
            timer_.setDelay(PROGRESS_UPDATE_DELAY);
            controller_.getSearchStrategy().continueProcessing();
        } */
    }

    public void cleanup() {
        timer_.stop();
        if (progressBar_ != null) {
            progressBar_.setValue(0);
            progressBar_.setString("");
        }
    }

    /**
     * The actionPerformed method in this class
     * is called each time the Timer "goes off".
     */
    private class TimerListener implements ActionListener {
        public void actionPerformed(ActionEvent evt) {
            SearchStrategy strategy = controller_.getSearchStrategy();
            if (strategy == null) return;
            int percentDone = strategy.getPercentDone();
            progressBar_.setValue( percentDone );
            String numMoves = FormatUtil.formatNumber(strategy.getNumMovesConsidered());
            String note = GameContext.getLabel("MOVES_CONSIDERED") + ' '
                   + numMoves + "  ("+ percentDone +"%)";

            progressBar_.setToolTipText(note);
            progressBar_.setString(note);

            if (stepping_) {
                stepping_ = false;
                controller_.pause();
            }
        }
    }
}