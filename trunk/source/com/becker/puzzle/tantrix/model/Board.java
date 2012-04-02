// Copyright by Barry G. Becker, 2012. Licensed under MIT License: http://www.opensource.org/licenses/MIT
package com.becker.puzzle.tantrix.model;

import com.becker.common.geometry.Location;
import com.becker.puzzle.sudoku.model.ValueConverter;
import com.becker.puzzle.sudoku.model.board.*;

/**
 *  The Board describes the physical layout of the puzzle.
 *
 *  @author Barry Becker
 */
public class Board {

    /** The number of Cells in the board is n^2 * n^2, but there are n * n big cells.   */
    protected int n_;
    protected int nn_;  // n times n

    static final int MAX_SIZE = 9;

    private Cell[][] cells_;


    /**
     * Constructor
     */
    public Board(int size) {
        assert(size > 1 && size < MAX_SIZE);
        n_ = size;
        nn_ = size * size;
        reset();
    }

    /**
     * copy constructor
     */
    public Board(Board b) {
        this(b.getBaseSize());
        for (int i=0; i<nn_; i++) {
           for (int j=0; j<nn_; j++) {
               getCell(i, j).setOriginalValue(b.getCell(i, j).getValue());
           }
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
     * return to original state before attempting solution.
     * Non original values become 0.
     */
    public void reset() {
        cells_ = new Cell[nn_][nn_];
        for (int i=0; i<nn_; i++)  {
           for (int j=0; j<nn_; j++) {
               cells_[i][j] = new Cell(0);
           }
        }
    }

    /**
     * @return retrieve the base size of the board (sqrt(edge magnitude)).
     */
    public final int getBaseSize() {
        return n_;
    }

    /**
     * @return  retrieve the edge size of the board.
     */
    public final int getEdgeLength() {
        return nn_;
    }

    public final int getNumCells() {
        return nn_ * nn_;
    }

    /**
     * @param row 0-nn_-1
     * @param col 0-nn_-1
     * @return the cell in the bigCellArray at the specified location.
     */
    public final Cell getCell(int row, int col) {
        return cells_[row][col];
    }

    public final Cell getCell(Location location) {
        return cells_[location.getRow()][location.getCol()];
    }

    /**
     * @param position a number between 0 and nn_^2
     * @return the cell at the specified position.
     */
    public final Cell getCell( int position ) {
        return getCell(position / nn_, position % nn_);
    }


    public String toString() {
        StringBuilder bldr = new StringBuilder("\n");
        for (int row=0; row < nn_; row++) {
            for (int col=0; col < nn_; col++) {
                bldr.append(ValueConverter.getSymbol(getCell(row, col).getValue()));
                bldr.append(" ");
            }
            bldr.append("\n");
        }

        return bldr.toString();
    }
}
