// Copyright by Barry G. Becker, 2012. Licensed under MIT License: http://www.opensource.org/licenses/MIT
package com.becker.puzzle.tantrix.model;

import com.becker.common.geometry.Location;

/**
 *  The TantrixBoard describes the physical layout of the puzzle.
 *
 *  @author Barry Becker
 */
public class TantrixBoard {

    static final int MAX_SIZE = 7;
    /** The number of Cells in the board is n^2 * n^2.   */
    protected int n_ = MAX_SIZE;


    private HexTile[][] tiles;

    /**
     * Constructor
     */
    public TantrixBoard(int size) {
        assert(size > 1 && size < MAX_SIZE);
        n_ = size;
        reset();
    }

    /**
     * copy constructor
     */
    public TantrixBoard(TantrixBoard b) {
        this(b.getEdgeLength());
        for (int i=0; i < n_; i++) {
           for (int j=0; j < n_; j++) {
               setTile(i, j, b.getTile(i, j));
           }
        }
    }

    public TantrixBoard(HexTileList tileList) {

        reset();
        for (int i = 0; i < tileList.size(); i++) {
           tiles[i / n_][i % n_] = tileList.get(i);
        }
    }

    /**
     * return to original state before attempting solution.
     * Non original values become 0.
     */
    public void reset() {
        tiles = new HexTile[n_][n_];
        for (int i=0; i<n_; i++)  {
           for (int j=0; j<n_; j++) {
               tiles[i][j] = null;
           }
        }
    }

    /**
     * @return  retrieve the edge size of the board.
     */
    public final int getEdgeLength() {
        return n_;
    }

    /**
     * @param row 0-nn_-1
     * @param col 0-nn_-1
     * @return the cell in the bigCellArray at the specified location.
     */
    public HexTile getTile(int row, int col) {
        return tiles[row][col];
    }

    public void setTile(int row, int col, HexTile tile) {
        tiles[row][col] = tile;
    }

    public final HexTile getTile(Location location) {
        return tiles[location.getRow()][location.getCol()];
    }


    public String toString() {
        StringBuilder bldr = new StringBuilder("\n");
        for (int row=0; row < n_; row++) {
            for (int col=0; col < n_; col++) {
                bldr.append(getTile(row, col));
                bldr.append(" ");
            }
            bldr.append("\n");
        }

        return bldr.toString();
    }
}
