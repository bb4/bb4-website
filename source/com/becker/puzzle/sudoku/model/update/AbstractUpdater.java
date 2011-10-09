package com.becker.puzzle.sudoku.model.update;

import com.becker.puzzle.sudoku.model.board.Board;

/**
 *  The Board describes the physical layout of the puzzle.
 *
 *  @author Barry Becker
 */
public abstract class AbstractUpdater implements IUpdater {

    /** the sudoku board   */
    protected Board board;

    /**
     * Constructor
     * @param board the board to update
     */
    public AbstractUpdater(Board board) {
        this.board = board;
    }

    /**
     * {@inheritDoc}
     */
    public abstract void updateAndSet();

}
