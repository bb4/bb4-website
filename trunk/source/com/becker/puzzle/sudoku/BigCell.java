package com.becker.puzzle.sudoku;

import java.util.HashSet;
import java.util.Set;

/**
 * A block of n*n cells in a sudoku puzzle.
 * @author Barry Becker Date: Jul 3, 2006
 */
public class BigCell {

    /** the internal data structures representing the game board. */
    protected Cell cells_[][] = null;

    /** The number of Cells in the BigCell is n * n.    */
    protected int n_;

    /** The number which have not yet been used in this big cell. */
    private Set<Integer> candidates_;

    private Board board_;


    public BigCell(int size, Board board) {
        board_ = board;

        assert(size > 1 && size < Board.MAX_SIZE);
        n_ = size;

        cells_ = new Cell[n_][n_];
        for (int i=0; i<n_; i++) {
           for (int j=0; j<n_; j++) {
               cells_[i][j] = new Cell(0, this);
           }
        }
        candidates_ = new HashSet<Integer>();
    }

    /**
     * @return  retrieve the base size of the board - sqrt(edge length).
     */
    public final int getSize()
    {
        return n_;
    }

    public Board getBoard() {
        return board_;
    }

    public void updateCandidates() {
        candidates_.clear();
        // assume all of them, then remove those that are represented.
        candidates_.addAll(board_.getValuesList());
        for (int i = 0; i < n_; i++) {
           for (int j = 0; j < n_; j++) {
               int v = cells_[i][j].getValue();
               if (v > 0) {
                  candidates_.remove(v);
               }
           }
        }
    }

    public Set<Integer> getCandidates() {
        return candidates_;
    }

    /**
     * returns null if there is no game piece at the position specified.
     * @return the piece at the specified location. Returns null if there is no piece there.
     */
    public final Cell getCell( int row, int col )
    {
        assert ( row >= 0 && row < n_ && col >= 0 && col < n_);
        return cells_[row][col];
    }

    /**
     * @param cell  cell to check for unique candidate.
     * @return the unique value for this cell if there is one
     */
    public int getUniqueValueForCell(Cell cell) {
        Set<Integer> cellCandidates = cell.getCandidates();
        if (cellCandidates == null)
            return 0; // cell.getValue();
        if (cellCandidates.size() == 1) {
            // if there is only one candidate, then that is the value for this cell.
            return cellCandidates.iterator().next();
        }
        return 0;   // the value is not unique
    }

    /**
     * Explicitly clean things up to avoid memory leaks.
     * The most common way to accidentaly have memory leaks is to leave listeners on objects.
     */
    public void dispose()
    {
        cells_ = null;
    }
}
