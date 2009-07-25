package com.becker.puzzle.sudoku;

import com.becker.common.util.Util;

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
    public boolean solvePuzzle( Board board) {
        return solvePuzzle(board, null);
    }

    public void setDelay(int delay)  {
        delay_ = delay;
    }

    /**
     * Solves the puzzle.
     * This implements the main algorithm for solving the red puzzle.
     * @param puzzlePanel the viewer
     * @return true if solved.
     */
    protected  boolean solvePuzzle(SudokuPanel puzzlePanel) {
        return solvePuzzle(puzzlePanel.getBoard(), puzzlePanel);
    }

    /**
     * Solves the puzzle.
     * This implements the main algorithm for solving the red puzzle.
     * @param board the board to show the solution on.
     * @param puzzlePanel the viewer
     * @return true if solved.
     */
    protected boolean solvePuzzle( Board board, SudokuPanel puzzlePanel) {
        boolean solved;
        int ct = 0;
        int maxIterations = 2 * board.getEdgeLength();  // @@ not sure what this should be.

        do {
            // find missing row and column numbers
            board.updateCellCandidates();
            refresh(puzzlePanel);
            Util.sleep(delay_);
            board.checkAndSetUniqueValues();

            refresh(puzzlePanel);
            Util.sleep(2*delay_);
            board.setNumIterations(++ct);
            solved = board.solved();

        } while (!solved && ct < maxIterations);

        refresh(puzzlePanel);

        // if we get here and solved is not true, we did not find a puzzlePanel
        return solved;
    }



    private static void refresh(SudokuPanel puzzlePanel) {
        if (puzzlePanel == null)
            return;
        puzzlePanel.repaint();
        Util.sleep(20);  // give it a chance to repaint.
    }
}
