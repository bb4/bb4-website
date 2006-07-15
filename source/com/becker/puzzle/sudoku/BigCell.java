package com.becker.puzzle.sudoku;

import java.util.*;

/**
 * A block of n*n cells in a sudoku puzzle.
 * @author Barry Becker Date: Jul 3, 2006
 */
public class BigCell {

    // the internal data structures representing the game board
    protected Cell cells_[][] = null;

    // The number of Cells in the BigCell is n * n.
    protected int n_;

    // The number which have not yet been used in this big cell.
    private List bigCellCandidates_;

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
        bigCellCandidates_ = new LinkedList();
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
        bigCellCandidates_.clear();
        // assume all of them, then remove those that are represented.
        bigCellCandidates_.addAll(board_.getValuesList());
        for (int i = 0; i < n_; i++) {
           for (int j = 0; j < n_; j++) {
               int v = cells_[i][j].getValue();
               if (v > 0) {
                  bigCellCandidates_.remove((Integer) v);
               }
           }
        }
    }

    public List getCandidates() {
        return bigCellCandidates_;
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
     * @param cell
     * @return the unique value for this cell if there is one
     */
    public int getUniqueValueForCell(Cell cell) {
        List cellCandidates = cell.getCandidates();
        if (cellCandidates == null)
            return 0; // cell.getValue();
        if (cellCandidates.size() == 1) {
            // if there is only one candidate, then that is the value for this cell.
            return (Integer) cellCandidates.get(0);
        }
        return 0;   // the value is not unique
    }

    private boolean isUnique(Object candidate, Cell cell) {
       for (int i = 0; i < n_; i++) {
           for (int j = 0; j < n_; j++) {
               Cell c = cells_[i][j];
               List candidates = c.getCandidates();
               if (c != cell && candidates != null && candidates.contains(candidate)) {
                   // then not unique
                   return false;
               }
           }
        }
        return true;
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
