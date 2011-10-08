package com.becker.puzzle.sudoku.model;


/**
 *  An array of cells for a row or column in the puzzle.
 *
 *  @author Barry Becker
 */
public interface CellSet {

    Cell getCell(int i);

    Candidates getCandidates();

    void remove(int unique);

    void add(int value);

    int numCells();

    /**
     * Assume all of them, then remove the values that are represented.
     */
    void updateCandidates(ValuesList values);

}
