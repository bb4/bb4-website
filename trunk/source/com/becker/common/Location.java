package com.becker.common;

import java.awt.geom.Point2D;
import java.io.Serializable;

/**
 * Represents a location location of something in integer coordinates.
 *
 * @author Barry Becker
 */
public final class Location implements Serializable
{
    private static final long serialVersionUID = 1;
    private int row_ = 0;
    private int col_ = 0;

    /**
     * Constructs a new point at (0, 0).
     * Default empty constructor
     */
    public Location() {
    }

    /**
     * Constructs a new Location at the given coordinates.
     *
     * @param r  the row  coordinate.
     * @param c  the column coordinate.
     */
    public Location( int r, int c )
    {
        row_ = r;
        col_ = c;
    }


    public int getRow() {
        return row_;
    }

    public void setRow(int row) {
        this.row_ = row;
    }

    public int getCol() {
        return col_;
    }

    public void setCol(int col) {
        this.col_ = col;
    }

    /**
     * Checks to see if the given location has the same coordinates as this
     * one.
     *
     * @param location  The location whose coordinates are to be compared.
     * @return true  The location's coordinates exactly equal this location's.
     */
    public boolean equals( Object location )
    {
        Location loc = (Location) location;
        return (loc.getRow() == row_) && (loc.getCol() == col_);
    }
    
    /**
     * If override euals, should also override hashCode
     */
    public int hashCode() {
        return (100 * row_ + col_);
    }

    /**
     * @param loc another location to measure distance from.
     * @return the euclidean distance from this location to another.
     */
    public double getDistanceFrom(Location loc)
    {
        float xDif = Math.abs(col_ - loc.getCol());
        float yDif = Math.abs(row_ - loc.getRow());
        return Math.sqrt( xDif*xDif + yDif*yDif);
    }

    /**
     * @param loc another arbitrary floating point location to measure distance from.
     * @return the euclidean distance from this location to another.
     */
    public double getDistanceFrom(Point2D loc)
    {
        double xDif = Math.abs(col_ - loc.getX());
        double yDif = Math.abs(row_ - loc.getY());
        return Math.sqrt( xDif*xDif + yDif*yDif);
    }

    /**
     * @return the string form
     */
    public String toString()
    {
        return "row=" + row_ + ", colum=" + col_;
    }

}
