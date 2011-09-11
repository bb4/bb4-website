package com.becker.puzzle.sudoku;

import com.becker.common.concurrency.ThreadUtil;
import com.becker.puzzle.sudoku.model.Board;
import junit.extensions.RepeatedTest;

import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

/**
 * This does the hard work of actually solving the puzzle.
 * Controller in the model-view-controller pattern.
 *
 * @author Barry Becker
 */
public class SudokuSolver {

    private Board board_;
    private int delay_;


    /**
     * Constructor
     * @param board board to show solution on.
     */
    public SudokuSolver(Board board) {
        delay_ = 0;
        board_ = board;
    }

    public void setBoard(Board b) {
        board_ = b;
    }

    /**
     * Solves the puzzle.
     * This implements the main algorithm for solving the red puzzle.
     *
     * @return true if solved.
     */
    public boolean solvePuzzle() {
        return solvePuzzle(null);
    }

    public void setDelay(int delay)  {
        delay_ = delay;
    }

    /**
     * Solves the puzzle.
     * This implements the main algorithm for solving the Sudoku puzzle.
     *
     * @param puzzlePanel the viewer
     * @return true if solved.
     */
    public boolean solvePuzzle(Container puzzlePanel) {
        boolean solved;

        // not sure what this should be.
        int maxIterations = 2 * board_.getEdgeLength();
        board_.zeroNumIterations();

        do {
            solved = doIteration();
            refreshWithDelay(puzzlePanel, 3);

        } while (!solved && board_.getNumIterations() < maxIterations);

        refresh(puzzlePanel);

        // if we get here and solved is not true, we did not find a solution.
        return solved;
    }

    public boolean doIteration()   {
        // find missing row and column numbers
        board_.updateAndSet();
        board_.incrementNumIterations();
        return board_.solved();
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
