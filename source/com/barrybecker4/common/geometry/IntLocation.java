// Copyright by Barry G. Becker, 2012. Licensed under MIT License: http://www.opensource.org/licenses/MIT
package com.barrybecker4.common.geometry;

import java.awt.geom.Point2D;
import java.io.Serializable;

/**
 * Represents a location location of something in integer coordinates.
 *
 * Immutable. Use MutableLocation if you really need to modify it (rare).
 *
 * @author Barry Becker
 */
public class IntLocation implements Serializable {

    private static final long serialVersionUID = 1;
    protected int row_ = 0;
    protected int col_ = 0;

    /**
     * Constructs a new point at (0, 0).
     * Default empty constructor
     */
    public IntLocation() {
    }

    public IntLocation(Location loc) {
        row_ = loc.getRow();
        col_ = loc.getCol();
    }


    /**
     * Constructs a new Location at the given coordinates.
     *
     * @param row  the row  coordinate (0 - 255).
     * @param col  the column coordinate (0 - 255).
     */
    public IntLocation(int row, int col) {
        row_ = row;
        col_ = col;
    }

    public int getRow() {
        return row_;
    }

    public int getCol() {
        return col_;
    }

    public int getX() {
        return col_;
    }

    public int getY() {
        return row_;
    }

    public IntLocation copy() {
        return new IntLocation(row_, col_);
    }

    /**
     * @return an immutable copy of the original incremented by the amount specified.
     */
    public IntLocation incrementOnCopy(int rowChange, int colChange) {
        return new IntLocation(row_ + rowChange, col_+colChange);
    }

    /**
     * @return an immutable copy of the original incremented by the amount specified.
     */
    public IntLocation incrementOnCopy(IntLocation loc) {
        return new IntLocation(row_ + loc.getRow(), col_+ loc.getCol());
    }

    /**
     * @return an immutable copy of the original incremented by the amount specified.
     */
    public IntLocation decrementOnCopy(IntLocation loc) {
        return new IntLocation(row_ - loc.getRow(), col_ - loc.getCol());
    }

    /**
     * Checks to see if the given location has the same coordinates as this
     * one.
     *
     * @param location  The location whose coordinates are to be compared.
     * @return true  The location's coordinates exactly equal this location's.
     */
    @Override
    public boolean equals( Object location ) {

        if (!(location instanceof IntLocation)) return false;
        IntLocation loc = (IntLocation) location;
        return (loc.getRow() == row_) && (loc.getCol() == col_);
    }

    /**
     * If override equals, should also override hashCode
     */
    public int hashCode() {
        return (100 * row_ + col_);
    }

    /**
     * @param loc another location to measure distance from.
     * @return the euclidean distance from this location to another.
     */
    public double getDistanceFrom(IntLocation loc) {
        float xDif = Math.abs(col_ - loc.getCol());
        float yDif = Math.abs(row_ - loc.getRow());
        return Math.sqrt( xDif*xDif + yDif*yDif);
    }

    /**
     * @param loc another arbitrary floating point location to measure distance from.
     * @return the euclidean distance from this location to another.
     */
    public double getDistanceFrom(Point2D loc) {
        double xDif = Math.abs(col_ - loc.getX());
        double yDif = Math.abs(row_ - loc.getY());
        return Math.sqrt( xDif*xDif + yDif*yDif);
    }

    /**
     * @return the string form
     */
    public String toString() {
        return "(row=" + row_ + ", column=" + col_ + ")";
    }
}

