package com.becker.puzzle.sudoku.model.update;


/**
 *  A strategy for updating the sudoku board while solving it..
 *
 *  @author Barry Becker
 */
public interface IUpdater {

    /**
     * update candidate lists for all cells then set the unique values that are determined.
     * Next check for loan rangers.
     */
    void updateAndSet();
}
