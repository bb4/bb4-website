package com.becker.puzzle.sudoku;

import com.becker.common.concurrency.ThreadUtil;
import com.becker.puzzle.sudoku.model.Board;

import java.awt.*;

/**
 * This does the hard work of actually solving the puzzle.
 * Controller in the model-view-controller pattern.
 *
 * @author Barry Becker
 */
public class SudokuSolver {

    private int delay_;

    /**
     * Constructor
     */
    public SudokuSolver() {
        delay_ = 0;
    }

    /**
     * Solves the puzzle.
     * This implements the main algorithm for solving the red puzzle.
     * @param board board to show solution on.
     * @return true if solved.
     */
    public boolean solvePuzzle(Board board) {
        return solvePuzzle(board, null);
    }

    public void setDelay(int delay)  {
        delay_ = delay;
    }

    /**
     * Solves the puzzle.
     * This implements the main algorithm for solving the Sudoku puzzle.
     *
     * @param board the board to show the solution on.
     * @param puzzlePanel the viewer
     * @return true if solved.
     */
    public boolean solvePuzzle(Board board, Container puzzlePanel) {
        boolean solved;
        int ct = 0;
        // not sure what this should be.
        int maxIterations = 2 * board.getEdgeLength();

        do {
            // find missing row and column numbers
            board.updateAndSet();
            refreshWithDelay(puzzlePanel, 1);

            refreshWithDelay(puzzlePanel, 3);
            board.setNumIterations(++ct);

            solved = board.solved();

        } while (!solved && ct < maxIterations);

        refresh(puzzlePanel);

        // if we get here and solved is not true, we did not find a solution.
        return solved;
    }

    private void refreshWithDelay(Container puzzlePanel, int relativeDelay) {
        refresh(puzzlePanel);
        ThreadUtil.sleep(relativeDelay * delay_);
    }


    private void refresh(Container puzzlePanel) {
        if (puzzlePanel == null || delay_ == 0)
            return;
        puzzlePanel.repaint();
        ThreadUtil.sleep(5);  // give it a chance to repaint.
    }
}
