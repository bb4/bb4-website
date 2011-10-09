package com.becker.puzzle.sudoku.model;


/**
 * An array of cells in a row column or bigCell in the puzzle.
 *
 * @author Barry Becker
 */
public interface CellSet {

    Cell getCell(int i);

    Candidates getCandidates();

    void removeCandidate(int unique);

    void addCandidate(int value);

    int numCells();

    /**
     * Assume all of them, then remove the values that are represented.
     */
    void updateCandidates(ValuesList values);

}
