package com.becker.puzzle.sudoku.model.update;

import com.becker.puzzle.sudoku.model.Board;

import java.util.ArrayList;
import java.util.List;

/**
 *  The Board describes the physical layout of the puzzle.
 *
 *  @author Barry Becker
 */
public abstract class AbstractUpdater {

    /** the sudoku board   */
    protected Board board;

    /**
     * Constructor
     */
    public AbstractUpdater(Board b) {
        board = b;
    }

    /**
     * update candidate lists for all cells then set the unique values that are determined.
     * Next check for loan rangers.
     */
    public abstract void updateAndSet();

}
