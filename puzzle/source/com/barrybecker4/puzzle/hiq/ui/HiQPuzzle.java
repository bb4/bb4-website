/** Copyright by Barry G. Becker, 2000-2011. Licensed under MIT License: http://www.opensource.org/licenses/MIT  */
package com.barrybecker4.puzzle.hiq.ui;

import com.barrybecker4.puzzle.common.AlgorithmEnum;
import com.barrybecker4.puzzle.common.PuzzleController;
import com.barrybecker4.puzzle.common.Refreshable;
import com.barrybecker4.puzzle.common.ui.PuzzleApplet;
import com.barrybecker4.puzzle.common.ui.PuzzleViewer;
import com.barrybecker4.puzzle.hiq.Algorithm;
import com.barrybecker4.puzzle.hiq.HiQController;
import com.barrybecker4.puzzle.hiq.model.PegBoard;
import com.barrybecker4.puzzle.hiq.model.PegMove;
import com.barrybecker4.ui.util.GUIUtil;

import javax.swing.JButton;
import javax.swing.JPanel;
import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 * HiQ Puzzle.
 * This program solves a very difficult classic solitaire puzzle
 * where you jump pegs to remove them in a cross shaped peg-board.
 * The fewer pegs you have remaining at the end, the better.
 * A perfect solution is to have only one peg in the center square
 * at the end.
 *  Assuming an average of 7 different options on each move, there are
 *   7 ^ 32 = 10,000,000,000,000,000,000,000,000,000,000
 *  (10 decillion combinations)
 * Actually this calculation is not correct since many paths lead
 * to the same board positions.
 * There are actually only 23.4 million unique board positions
 * see http://www.durangobill.com/Peg33.html for analysis.
 *    A brute force solution will run for years on today's fastest
 * computers.
 * See http://homepage.sunrise.ch/homepage/pglaus/Solitaire/solitaire.htm
 * for a solution that uses a genetic algorithm to find a solution quickly.
 *
 * My initial approach was to apply a kind of tunnel method.
 * I tried to solve the problem from both ends.
 * First, I work backwards for 32-FORWARD_MOVE's and build a
 * hashmap of all the possible board positions - storing a path to the solution
 * at each hashmap location. Then I traverse forward from the initial position
 * for BACK_MOVE's until I reach one of these positions that I know
 * leads to the solution. Then I combined the 2 paths to see the sequence
 * that will lead to the solution.
 *   Finally, I found that it was enough to search entirely from the beginning
 * and just prune when I reach states I've encountered before.
 * When I first ran this successfully, it took about 1 hour to run on an AMD 64bit 3200.
 * After optimization it  ran in about 3 minutes on a Core2Duo (189 seconds).
 * After parallelizing the algorithm using ConcurrentPuzzleSolver it is down to 93 seconds on the CoreDuo.
 */
public final class HiQPuzzle extends PuzzleApplet<PegBoard, PegMove>
                             implements ActionListener, DoneListener {

    private JButton backButton_;
    private JButton forwardButton_;
    private int currentStep_;

    /**
     * Construct the application
     */
    public HiQPuzzle() {}


    @Override
    protected PuzzleViewer<PegBoard, PegMove> createViewer() {
        return new PegBoardViewer(PegBoard.INITIAL_BOARD_POSITION, this);
    }

    @Override
    protected PuzzleController<PegBoard, PegMove> createController(Refreshable<PegBoard, PegMove> viewer_) {
        return new HiQController(viewer_);
    }

    @Override
    protected AlgorithmEnum<PegBoard, PegMove>[] getAlgorithmValues() {
        return Algorithm.values();
    }


    @Override
    protected JPanel createCustomControls() {

        backButton_ = new JButton("Back");
        forwardButton_ = new JButton("Forward");
        backButton_.addActionListener(this);
        forwardButton_.addActionListener(this);
        backButton_.setEnabled(false);
        forwardButton_.setEnabled(false);

        JPanel buttonPanel = new JPanel(new BorderLayout());
        buttonPanel.add(backButton_, BorderLayout.WEST);
        buttonPanel.add(forwardButton_, BorderLayout.EAST);

        return buttonPanel;
    }

    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == backButton_) {
            moveInPath(-1);
            backButton_.setEnabled((currentStep_ > 0));
            forwardButton_.setEnabled(true);
        }
        else if (e.getSource() == forwardButton_) {
            moveInPath(1);
            boolean enable = (currentStep_ < ((PegBoardViewer)viewer_).getPath().size()-1);
            forwardButton_.setEnabled(enable);
            backButton_.setEnabled(true);
        }
    }


    /**
     * switch from the current move in the sequence forwards or backwards stepSize.
     * @param stepSize num steps to move.
     */
    public void moveInPath(int stepSize) {
        if (stepSize == 0) return;
        int inc = stepSize > 0 ? 1 : -1;
        int toStep = currentStep_ + stepSize;
        do {
            ((PegBoardViewer)viewer_).makeMove(currentStep_, (inc < 0));
            currentStep_ += inc;
        } while (currentStep_ != toStep);
        viewer_.repaint();
    }

    public void done() {
        currentStep_ = ((PegBoardViewer)viewer_).getPath().size()-1;
        backButton_.setEnabled(true);
        forwardButton_.setEnabled(false);
    }

    /**
     * Use this to run as an application instead of an applet.
     */
    public static void main(String[] args) {

        PuzzleApplet applet = new HiQPuzzle();

        // this will call applet.init() and start() methods instead of the browser
        GUIUtil.showApplet(applet, "HiQ Puzzle Solver");
    }
}

