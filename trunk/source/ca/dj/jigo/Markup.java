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

import java.awt.Color;
import java.awt.Graphics;

/**
 * A board markup item.
 */
public class Markup
{
  /**
   * The mark type is unknown.
   */
  public final static int UNKNOWN  = 0;

  /**
   * The mark is a circle.
   */
  public final static int CIRCLE   = 1;

  /**
   * The mark is a square.
   */
  public final static int SQUARE   = 2;

  /**
   * The mark is a triangle.
   */
  public final static int TRIANGLE = 3;

  /**
   * The mark is a number.
   */
  public final static int NUMBER   = 4;

  /**
   * The mark is a label (letters).
   */
  public final static int LABEL    = 5;

  private int myType = UNKNOWN;

  private int myNumber;
  private String myLabel;

  private Color myColour = Color.black;

  /**
   * Creates a new board markup.
   *
   * @param markupType - The type of markup this object represents.
   */
  public Markup( int markupType )
  {
    setType( markupType );
  }

  /**
   * Creates a new board markup, with a specific colour.
   *
   * @param markupType - The type of markup this object represents.
   * @param colour - The colour to use when drawing the markup.
   */
  public Markup( int markupType, Color colour )
  {
    setType( markupType );
    setColour( colour );
  }

  /**
   * Used to draw the mark at a given location on the graphics context.
   * Notice that the mark does not centre itself about this point; the
   * Goban is responsible for making sure the marks are centred about a point.
   *
   * @param g - The graphics context on which to draw.
   * @param x - The x location to begin drawing.
   * @param y - The y location to begin drawing.
   * @param width - Number of pixels wide for this markup.
   * @param height - Number of pixels high for this markup.
   */
  protected void draw( Graphics graphics, int x, int y, int width, int height )
  {
    // This centres the "stone" about a point.  Could use bit shifting instead
    // of dividing by two to speed things up ...
    //
    width /= 2;
    height /= 2;

    // This centres the mark about a "stone".
    //
    x += width / 2;
    y += height / 2;

    // Adjust for unevenness.  Odd widths/heights need an extra pixel, so if
    // the lowest significant bit is set, add 1 to the x and/or y values.
    //
    x += (width & 1);
    y += (height & 1);

    graphics.setColor( getColour() );

    // Figure out our type of Markup, then draw it on the graphics context.
    // If the type isn't here, then there's not much we can do.
    //
    switch( getType() )
    {
      case CIRCLE:
        drawCircle( graphics, x, y, width, height );
        break;

      case SQUARE:
        drawSquare( graphics, x, y, width, height );
        break;

      // The triangle has to shift up a bit to look correct.
      //
      case TRIANGLE:
        drawTriangle( graphics, x, y - 2, width, height );
        break;

      case NUMBER:
        drawNumber( graphics, x, y );
        break;

      case LABEL:
        drawLabel( graphics, x, y );
        break;
    }
  }

  /**
   * Draws: O
   */
  private void drawCircle(
    Graphics graphics, int x, int y, int width, int height )
  {
    graphics.drawOval( x, y, width, height );
  }

  /**
   * Draws: []
   */
  private void drawSquare(
    Graphics graphics, int x, int y, int width, int height )
  {
    graphics.drawRect( x, y, width, height );
  }

  /**
   * Draws: /_\
   */
  private void drawTriangle(
    Graphics graphics, int x, int y, int width, int height )
  {
    // Draws: /
    //
    graphics.drawLine( x + (width / 2), y, x, y + height );

    // Draws: \
    //
    graphics.drawLine( x + (width / 2), y, x + width, y + height );

    // Draws: _
    //
    graphics.drawLine( x, y + height, x + width, y + height  );
  }

  /**
   * Draws the first letter of our text label.
   */
  private void drawLabel( Graphics graphics, int x, int y )
  {
    try
    {
      graphics.drawString( getLabel(), x, y );
    }
    catch( Exception e ) { }
  }

  /**
   * Draws our number.
   */
  private void drawNumber( Graphics graphics, int x, int y )
  {
    try
    {
      graphics.drawString( Integer.toString( getNumber() ), x, y );
    }
    catch( Exception e ) { }
  }

  /**
   * Changes the (foreground) colour used for drawing the mark.
   *
   * @param colour - The new foreground colour.
   */
  public void setColour( Color colour )
  {
    if( colour != null )
      myColour = colour;
  }

  private Color getColour() { return myColour; }

  protected int getType() { return myType; }

  /**
   * Changes this board markup's type.
   */
  public void setType( int type ) { myType = type; }

  protected int getNumber() { return myNumber; }

  /**
   * Changes this board markup's number.  This only has an affect if the
   * markup type is NUMBER.
   */
  public void setNumber( int number ) { myNumber = number; }

  protected String getLabel() { return myLabel; }

  /**
   * Changes this board markup's text label.  This only has an affect if the
   * markup type is LABEL.
   */
  public void setLabel( String label ) { myLabel = label; }
}

