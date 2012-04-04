// Copyright by Barry G. Becker, 2012. Licensed under MIT License: http://www.opensource.org/licenses/MIT
package com.becker.puzzle.tantrix.model;

import com.becker.common.geometry.Location;

/**
 *  The TantrixBoard describes the physical layout of the puzzle.
 *
 *  @author Barry Becker
 */
public class TantrixBoard {

    static final int MAX_SIZE = 9;

    /** The number of Cells in the board is n^2 * n^2.   */
    protected int n_ = MAX_SIZE;

    private TilePlacement[][] tiles;

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
               setTilePlacement(b.getTilePlacement(i, j));
           }
        }
    }

    public TantrixBoard(TantrixBoard board, TilePlacement placement) {
        this(board);
        this.setTilePlacement(placement);
    }

    public TantrixBoard(HexTileList tileList) {

        n_ = (int)Math.ceil(Math.sqrt(tileList.size())) + 1;
        reset();
        for (byte i = 0; i < tileList.size(); i++) {
            byte row = (byte) (i / n_);
            byte col = (byte) (i % n_);
           tiles[row][col] = new TilePlacement(tileList.get(i), new Location(row, col), Rotation.ANGLE_0);
        }
    }

    public boolean isSolved() {
        return false;
    }

    public TantrixBoard placeTile(TilePlacement placement) {
        return new TantrixBoard(this, placement);
    }

    /**
     * return to original state before attempting solution.
     * Non original values become 0.
     */
    public void reset() {
        tiles = new TilePlacement[n_][n_];
        for (int i=0; i<n_; i++)  {
           for (int j=0; j<n_; j++) {
               tiles[i][j] = new TilePlacement(new Location(i, j));
           }
        }
    }

    /**
     * @return retrieve the edge size of the board.
     */
    public final int getEdgeLength() {
        return n_;
    }

    /**
     * @return the cell in the bigCellArray at the specified location.
     */
    public TilePlacement getTilePlacement(int row, int col) {
        return tiles[row][col];
    }

    public void setTilePlacement(TilePlacement tile) {
        Location loc = tile.getLocation();
        tiles[loc.getRow()][loc.getCol()] = tile;
    }

    public final HexTile getTile(Location location) {
        return tiles[location.getRow()][location.getCol()].getTile();
    }

    public final TilePlacement getTilePlacement(Location location) {
        return tiles[location.getRow()][location.getCol()];
    }


    public String toString() {
        StringBuilder bldr = new StringBuilder("\n");
        for (int row=0; row < n_; row++) {
            for (int col=0; col < n_; col++) {
                bldr.append(getTilePlacement(row, col));
                bldr.append(" ");
            }
            bldr.append("\n");
        }

        return bldr.toString();
    }
}
