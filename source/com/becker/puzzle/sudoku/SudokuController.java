package com.becker.puzzle.sudoku;

import com.becker.common.concurrency.Worker;
import com.becker.puzzle.sudoku.ui.SudokuPanel;

import java.awt.*;


/**
 * Controller part of the MVC pattern.
 *
 * @author Barry becker
 */
public final class SudokuController {

    private SudokuPanel puzzlePanel_;

    /**
     * Construct the application and set the look and feel.
     * @@ make the panel passed in implement a generic interface.
     */
    public SudokuController(SudokuPanel panel) {
        puzzlePanel_ = panel;
    }

    public void generatePuzzle(final int delay, final int size) {
        Worker worker = new Worker() {

            @Override
            public Object construct() {
                puzzlePanel_.setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
                puzzlePanel_.setDelay(delay);
                puzzlePanel_.generateNewPuzzle(size);
                return null;
            }

            @Override
            public void finished() {
                puzzlePanel_.repaint();
                puzzlePanel_.setCursor(Cursor.getDefaultCursor());
            }
        };
        worker.start();
    }

    public void solvePuzzle(final int delay) {
        Worker worker = new Worker() {

            @Override
            public Object construct() {
                puzzlePanel_.setDelay(delay);
                puzzlePanel_.startSolving();  // comment for single stepping
                return null;
            }

            @Override
            public void finished() {
                puzzlePanel_.repaint();
            }
        };
        worker.start();
    }
}
