package com.becker.puzzle.sudoku.model;

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


    public BigCell(int size) {

        assert(size > 1 && size < Board.MAX_SIZE);
        n_ = size;

        cells_ = new Cell[n_][n_];
        for (int i=0; i<n_; i++) {
           for (int j=0; j<n_; j++) {
               cells_[i][j] = new Cell(0, this);
           }
        }
        candidates_ = new Candidates();
    }

    /**
     * @return  retrieve the base size of the board - sqrt(edge magnitude).
     */
    public final int getSize() {
        return n_;
    }


    public void updateCandidates(Board board) {

        candidates_.clear();
        // assume all of them, then remove those that are represented.
        candidates_.addAll(board.getValuesList());

        for (int i = 0; i < board.getBaseSize(); i++) {
           for (int j = 0; j < board.getBaseSize(); j++) {
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

    /**
     * Find the intersection of the row, column, and bigGrid candidates and set it as the candidates for the cell.
     * @param cell cell to check for a unique candidate.
     * @return the unique value for this cell if there is one, else return 0.
     */
    public int getUniqueValueForCell(Cell cell, Candidates rowCands, Candidates colCands) {

        if (cell.getCandidates() == null)  {
            cell.getValue();
        }

        Candidates cands = cell.getCandidates();
        cands.findIntersectionCandidates(candidates_, rowCands, colCands);

        if (cands.size() == 1) {
            // if there is only one candidate, then that is the value for this cell.
            return cands.iterator().next();
        }
        return 0;   // the value is not unique
    }

    /**
     * Explicitly clean things up to avoid memory leaks.
     * The most common way to accidentally have memory leaks is to leave listeners on objects.
     */
    public void dispose() {
        cells_ = null;
    }
}
