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
import java.awt.Image;

/**
 * Information about a Stone (such as width, height, image).  You'll notice
 * a Stone doesn't have a location on a Goban.  Only the Goban knows where
 * its Stones are located.  (This isn't entirely true; any object that
 * observes the Goban for stone placements can keep track of stone locations
 * itself.)
 */
public abstract class Stone
{
  /**
   * 20 pixels wide is a decent width for a Go stone.
   */
  public final static int DEFAULT_WIDTH = 20;

  /**
   * 22 pixels high is a decent height for a Go stone.
   */
  public final static int DEFAULT_HEIGHT = 22;

  private boolean mustDrawImage = false;

  private Image myImage;

  // The size of stones for black and the size for white are entangled.
  //
  private static int myWidth  = DEFAULT_WIDTH,
                     myHeight = DEFAULT_HEIGHT;

  /**
   * Creates a new stone of default width and height.
   */
  public Stone() { }

  /**
   * Creates a stone with a given width and height (in pixels), but no
   * associated image.
   */
  public Stone( int width, int height )
  {
    setWidth( width );
    setHeight( height );
  }

  /**
   * Creates a stone with a given image (the width and height are derived
   * from the image's width and height).  A Stone must have a colour and a
   * graphical image representation.  If the image is null, a default width
   * and height are used (currently twenty pixels for both).
   */
  public Stone( Image stoneImage )
  {
    setImage( stoneImage );
  }

  /**
   * Used to draw the stone at a given location on the graphics context.
   * Notice that the stone does not centre itself about this point; the
   * Goban adjusts accordingly.
   *
   * @param graphics - The graphics context on which to draw.
   * @param x - The x location to draw the stone.
   * @param y - The y location to draw the stone.
   */
  public void draw( Graphics graphics, int x, int y )
  {
    if( mustDrawImage() )
      graphics.drawImage( getImage(), x, y, null );
    else
    {
      if( isWhite() )
      {
        graphics.setColor( Color.white );
        graphics.fillOval( x + 1, y + 1, getWidth() - 1, getHeight() - 1 );
      }

      graphics.setColor( Color.black );

      if( isWhite() )
        graphics.drawOval( x, y, getWidth(), getHeight() );
      else
        graphics.fillOval( x, y, getWidth(), getHeight() );
    }
  }

  /**
   * Returns true if this is a White stone; otherwise false.
   *
   * @return true - This is a White stone.
   */
  public abstract boolean isWhite();

  /**
   * Returns the width, in pixels, of this stone.
   *
   * @return The width, in pixels, used for drawing this stone.
   */
  public int getWidth() { return myWidth; }

  /**
   * Used to set the width, in pixels, of this stone.  Normally, this
   * is derived by the stone's image.  If the width is not even, it is
   * shrunken by 1 pixel.
   *
   * @param width - The width of this stone, in pixels (forced to be even).
   */
  protected void setWidth( int width )
  {
    // If the last bit is a one, then the number is odd.
    //
    if( (width & 1) == 1 )
      width--;

    myWidth = width;
  }

  /**
   * Returns the height, in pixels, of this stone.
   *
   * @return The height, in pixels, used for drawing this stone.
   */
  public int getHeight() { return myHeight; }

  /**
   * Used to set the height, in pixels, of this stone.  Normally, this
   * is derived by the stone's image.  If the height is not even, it is
   * shrunken by 1 pixel.
   *
   * @param height - The height of this stone, in pixels (forced to be even).
   */
  protected void setHeight( int height )
  {
    // If the last bit is a one, then the number is odd.
    //
    if( (height & 1) == 1 )
      height--;

    myHeight = height;
  }

  /**
   * Returns the image used to draw this stone (might be null).
   *
   * @return The graphical image used when drawing this stone.
   */
  private Image getImage() { return myImage; }

  /**
   * Changes the image used for drawing this stone.  The width and height
   * are updated automatically.  An image's width and height should be an
   * even number of pixels (if they aren't, the board size will be shrunk by
   * 1 pixel per stone, and stones will overlap).
   */
  public void setImage( Image image )
  {
    if( image == null )
    {
      setDrawImage( false );
      return;
    }

    myImage = image;

    setWidth( myImage.getWidth( null ) );
    setHeight( myImage.getHeight( null ) );
    setDrawImage( true );
  }

  /**
   * Toggles whether this draws a graphical image or a circle.
   */
  public void toggleDrawImage() { setDrawImage( !mustDrawImage() ); }

  private boolean mustDrawImage() { return mustDrawImage; }
  private void setDrawImage( boolean b ) { mustDrawImage = b; }
}

