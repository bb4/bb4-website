/*
 * Copyright (C) 2001 by Dave Jarvis
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 2
 * of the License, or (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA  02111-1307, USA.
 * Online at: http://www.gnu.org/copyleft/gpl.html
 */

package ca.dj.jigo;

/**
 * A location on a Goban.  This is a holder, nothing more, and
 * certainly nothing less, for an (x, y) pair.  Since it is used quite
 * often, the (x, y) values are made directly accessible (for reasons of
 * speed).
 */
public final class Location
{
  /**
   * The x component to an intersection on a Goban.
   */
  public int x = 0;

  /**
   * The y component to an intersection on a Goban.
   */
  public int y = 0;

  /**
   * Empty constructor; you can set the x and y values directly.
   */
  public Location() { }

  /**
   * Creates a new Location instance with the given x and y values.
   *
   * @param newX - The new value for the x portion of this Location.
   * @param newY - The new value for the y portion of this Location.
   */
  public Location( int newX, int newY )
  {
    x = newX;
    y = newY;
  }

  /**
   * Checks to see if the given location has the same coordinates as this
   * one.  Returns true if they do, otherwise false.
   *
   * @param location - The location whose coordinates are to be compared.
   * @return true - The location's coordinates exactly equal this location's.
   */
  public boolean equals( Location location )
  {
    return (location.x == x) && (location.y == y);
  }

}

