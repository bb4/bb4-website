/** Copyright by Barry G. Becker, 2000-2011. Licensed under MIT License: http://www.opensource.org/licenses/MIT  */
package com.becker.common.geometry;

/**
 * Represents a location location of something in byte coordinates.
 * The range of bytes are only 0 to 255 and cannot be negative.
 * Use this version if you really need it to be mutable
 * This version <i>is</> mutable. 
 *
 * @author Barry Becker
 */
public final class MutableLocation extends Location {

    /**
     * Constructs a new Location at the given coordinates.
     *
     * @param row  the row  coordinate (0 - 255).
     * @param col  the column coordinate (0 - 255).
     */
    public MutableLocation( int row, int col ) {
        super(row, col);
    }

    public MutableLocation(Location loc) {
        super(loc.getRow(), loc.getCol());
    }

    public void setRow(int row) {
        this.row_ =(byte) row;
    }

    public void setCol(int col) {
        this.col_ =(byte) col;
    }

    public void incrementRow(int rowChange) {
        row_ += rowChange;
    }

    public void incrementCol(int colChange) {
        col_ += colChange;
    }

    public void increment(int rowChange, int colChange) {
        incrementRow(rowChange);
        incrementCol(colChange);
    }
}

