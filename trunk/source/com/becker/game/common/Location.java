package com.becker.game.common;

import java.awt.geom.Point2D;

/**
 * Represents a location on a game board.
 * The row and col values are directly accessible for efficiency reasons.
 *
 * @author Barry Becker
 */
public final class Location
{
  public int row = 0;
  public int col = 0;

  /**
   * Constructs a new point at (0, 0).
   */
  public Location() { }

  /**
   * Constructs a new Location at the given coordinates.
   *
   * @param r  the row  coordinate.
   * @param c  the column coordinate.
   */
  public Location( int r, int c )
  {
      row = r;
      col = c;
  }

  /**
   * Checks to see if the given location has the same coordinates as this
   * one.
   *
   * @param location  The location whose coordinates are to be compared.
   * @return true  The location's coordinates exactly equal this location's.
   */
  public boolean equals( Location location )
  {
      return (location.row == row) && (location.col == col);
  }

  /**
   * @param loc another location to measure distance from.
   * @return the euclidean distance from this location to another.
   */
  public double getDistanceFrom(Location loc)
  {
      float xDif = Math.abs(col - loc.col);
      float yDif = Math.abs(row - loc.row);
      double dist = Math.sqrt( xDif*xDif + yDif*yDif);
      return dist;
  }

  /**
   * @param loc another arbitrary floating point location to measure distance from.
   * @return the euclidean distance from this location to another.
   */
  public double getDistanceFrom(Point2D loc)
  {
      double xDif = Math.abs(col - loc.getX());
      double yDif = Math.abs(row - loc.getY());
      double dist = Math.sqrt( xDif*xDif + yDif*yDif);
      return dist;
  }

  /**
   * @return the string form
   */
  public String toString()
  {
      return "row=" + row + ", colum=" + col;
  }
}

