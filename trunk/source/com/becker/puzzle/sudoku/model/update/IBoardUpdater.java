package com.becker.puzzle.sudoku.model.update;


import com.becker.puzzle.sudoku.model.board.Board;

/**
 *  A strategy for updating the sudoku board while solving it.
 *
 *  @author Barry Becker
 */
public interface IBoardUpdater {

    /**
     * Update candidate lists for all cells then set the unique values that are determined.
     * Applies all updaters.
     */
    void updateAndSet(Board board);
}
