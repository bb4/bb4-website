package com.becker.puzzle.sudoku;

import java.util.*;

/**
 *  the Board describes the physical layout of the game.
 *  It is an abstract class that provides a common implementation for many of the
 *  methods in the BoardInterface.
 *  Assumes an M*N grid.
 *  Legal positions are [1, numRows_][1, numCols_]
 *
 *  Games like pente, go, chess, checkers, go-moku,
 *  shoji, othello, connect4, squares, Stratego, Blockade
 *  Other games like Risk, Galactic Empire, or Monopoly and might be supportable in the future.
 *  They are harder because they do not have perfect information (i.e. they use dice)
 *  and have multiple players.
 *
 *  @author Barry Becker
 */
public class Board
{

    // the internal data structures representing the game board
    protected BigCell bigCells_[][] = null;

    // The number of Cells in the boad is n^2 * n^2, but there are n * n big cells.
    protected int n_;
    protected int nn_;  // n times n

    static final int MAX_SIZE = 9;

    // lists of row and col candiddates for every row and col.
    protected List[] rowCandidates_;
    protected List[] colCandidates_;

    private Set valueSet_;


    public Board(int size) {
        assert(size > 1 && size < MAX_SIZE);
        n_ = size;
        nn_ = size * size;

        bigCells_ = new BigCell[n_][n_];
        for (int i=0; i<n_; i++) {
           for (int j=0; j<n_; j++) {
               bigCells_[i][j] = new BigCell(n_, this);
           }
        }

        // creaate the row and col candidate lists
        rowCandidates_ = new List[nn_];
        colCandidates_ = new List[nn_];
        for (int i=0; i < nn_; i++) {
            rowCandidates_[i] = new LinkedList();
            colCandidates_[i] = new LinkedList();
        }

        valueSet_ = new HashSet();
        for (int i = 1; i<=nn_; i++) {
            valueSet_.add(i);
        }
    }

    public Board(int[][] initialData) {
        this((int) Math.sqrt(initialData.length));

        assert(initialData.length == nn_ && initialData[0].length == nn_);

        for (int i=0; i<nn_; i++) {
           for (int j=0; j<nn_; j++) {
               getCell(i, j).setOriginalValue(initialData[i][j]);
           }
        }
    }

    /**
     * @return  retrieve the base size of the board (sqrt(edge length).
     */
    public final int getBaseSize()
    {
        return n_;
    }

    /**
     * @return  retrieve the edge size of the board.
     */
    public final int getEdgeLength()
    {
        return nn_;
    }


    /**
     * returns null if there is no game piece at the position specified.
     * @return the piece at the specified location. Returns null if there is no piece there.
     */
    public final BigCell getBigCell( int row, int col )
    {
        assert ( row >= 0 && row < n_ && col >= 0 && col < n_);
        return bigCells_[row][col];
    }

    /**
     * returns null if there is no game piece at the position specified.
     * @return the piece at the specified location. Returns null if there is no piece there.
     */
    public final Cell getCell( int row, int col )
    {

        assert ( row >= 0 && row < nn_ && col >= 0 && col < nn_);
        return bigCells_[row / n_][col / n_].getCell(row % n_, col % n_);
    }

    /**
     * @return true if the board has been successfully solved.
     */
    public boolean solved() {

        if (isFilledIn()) {
            for (int row=0; row < nn_; row++) {
                for (int col=0; col < nn_; col++) {
                    Cell c = this.getCell(row, col);
                    if (c.getCandidates() != null) {
                        return false;
                    }
                }
            }
            return true;
        }
        return false;
    }

    /**
     *
     * @return true if all the cells have been filed in with a value (even if not a valid solution).
     */
    public boolean isFilledIn() {

        for (int row = 0; row < nn_; row++) {
            for (int col = 0; col < nn_; col++) {
                Cell c = this.getCell(row, col);
                if (c.getValue() <= 0) {
                    return false;
                }
            }
        }
        return true;
    }

    public void updateCellCandidates() {
        updateRowCandidates();
        updateColCandidates();
        updateBigCellCandidates();

        // find the cell candidates (intersection of above lists)
        for (int row=0; row < nn_; row++) {
            for (int col=0; col < nn_; col++) {
                getCell(row, col).updateCandidates(getRowCandidates(row), getColCandidates(col));
            }
        }
    }

    private void updateRowCandidates() {
        for (int row = 0; row < nn_; row++) {
            List rowCands = rowCandidates_[row];
            rowCands.clear();
            rowCands.addAll(valueSet_);
            for (int j=0; j < nn_; j++) {
                int v = this.getCell(row, j).getValue();
                if (v > 0 )  {
                    rowCands.remove((Integer) v);
                }
            }
        }
    }

    private void updateColCandidates() {
        for (int col = 0; col < nn_; col++) {
            List colCands = colCandidates_[col];
            colCands.clear();
            colCands.addAll(valueSet_);
            for (int j = 0; j < nn_; j++) {
                int v = this.getCell(j, col).getValue();
                if (v > 0 )  {
                    colCands.remove((Integer) v);
                }
            }
        }
    }


    private void updateBigCellCandidates() {
        for (int i=0; i<n_; i++) {
            for (int j=0; j<n_; j++) {
                this.getBigCell(i, j).updateCandidates();
            }
        }
    }

    public void checkAndSetUniqueValues() {
        for (int row=0; row < nn_; row++) {
            for (int col=0; col < nn_; col++) {
                getCell(row, col).checkAndSetUniqueValues(getRowCandidates(row), getColCandidates(col));
            }
        }
    }

    /**
     *
     * @return the complete set of allowable values (1,... nn);
     */
    protected Set getValueSet() {
        return valueSet_;
    }

    protected List getRowCandidates(int row)  {
        return rowCandidates_[row];
    }

    protected List getColCandidates(int col)  {
        return colCandidates_[col];
    }


}
