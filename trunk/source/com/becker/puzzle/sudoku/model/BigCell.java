package com.becker.puzzle.sudoku.model;

import java.util.ArrayList;
import java.util.List;

/**
 * A block of n*n cells in a sudoku puzzle.
 * @author Barry Becker
 */
public class BigCell {

    /** The internal data structures representing the game board. Row, column order. */
    protected Cell cells_[][] = null;

    /** The number of Cells in the BigCell is n * n.  */
    protected int n_;

    /** The number which have not yet been used in this big cell. */
    private Candidates candidates_;


    public BigCell(int size, ValuesList values) {

        assert(size > 1 && size < Board.MAX_SIZE);
        n_ = size;

        cells_ = new Cell[n_][n_];
        for (int i=0; i<n_; i++) {
           for (int j=0; j<n_; j++) {
               cells_[i][j] = new Cell(0, this, values);
           }
        }
        candidates_ = new Candidates(values);
    }

    /**
     * @return  retrieve the base size of the board - sqrt(edge magnitude).
     */
    public final int getSize() {
        return n_;
    }

    /** a value has been set, so we need to remove it from all the candidate lists. */
    public void remove(int unique) {
        candidates_.safeRemove(unique);
        for (int i=0; i<n_; i++) {
           for (int j=0; j<n_; j++) {
               getCell(i, j).removeCandidateValue(unique);
           }
        }
    }

    /** add to the bigCell candidate list and each cells candidates for cells not yet set in stone. */
    public void add(int value) {
        for (int i=0; i<n_; i++) {
           for (int j=0; j<n_; j++) {
               Cell cell = getCell(i, j);
               if (cell.isAvailable(value)) {
                   cell.addCandidateValue(value);
               }
           }
        }
        candidates_.add(value);
    }

    boolean isAvailable(int value) {
        assert value > 0;
        for (int i=0; i<n_; i++) {
            for (int j=0; j<n_; j++) {
                if (getCell(i, j).getValue() == value) {
                    return false;
                }
            }
        }
        return true;
    }

    /** @return all the candidate lists for all the cells in the bigCell except the one specified. */
    public CandidatesArray getCandidatesArrayExcluding(int row, int col) {

        List<Candidates> cands = new ArrayList<Candidates>();

        for (int i = 0; i < n_; i++) {
           for (int j = 0; j < n_; j++) {
               Candidates c = getCell(i, j).getCandidates();
               if (!(i==row && j==col) && c!=null) {
                   cands.add(c);
               }
           }
        }
        return new CandidatesArray(cands.toArray(new Candidates[cands.size()]));
    }


    public void updateCandidates(ValuesList values) {

        //candidates_ = new Candidates(values);  // try this way
        candidates_.clear();
        // assume all of them, then remove those that are represented.
        candidates_.addAll(values);

        for (int i = 0; i < n_; i++) {
           for (int j = 0; j < n_; j++) {
               int v = cells_[i][j].getValue();
               if (v > 0) {
                  candidates_.remove(v);
               }
           }
        }
    }

    public Candidates getCandidates() {
        return candidates_;
    }

    /**
     * returns null if there is no game piece at the position specified.
     * @return the piece at the specified location. Returns null if there is no piece there.
     */
    public final Cell getCell( int row, int col ) {
        assert ( row >= 0 && row < n_ && col >= 0 && col < n_);
        return cells_[row][col];
    }
}
