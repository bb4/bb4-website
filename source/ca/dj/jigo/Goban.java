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
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.Panel;

import java.util.Observable;
import java.util.Observer;

/**
 * The Goban knows about stones and board markup, and is responsible for
 * displaying itself, upon request (see the <CODE>forceRepaint()</CODE> method).
 * <P>
 * A blank board, when drawn, will look something like this:
 *
 * <PRE>
 * (0, 0)
 *    +---------------------------------+
 *    |  +-+-+-+-+-+-+-+-+-+-+-+-+-+-+  |
 *    |  | | | | | | | | | | | | | | |<----- Goban
 *    |  +-+-+-+-+-+-+-+-+-+-+-+-+-+-+  |
 *    |  | | | | | | | | | | | | | | |  |<-- Outside Border
 *                    : :               
 *                    : :               
 *    |  | | | | | | | | | | | | | | |  |
 *    |  +-+-+-+-+-+-+-+-+-+-+-+-+-+-+  |
 *    +---------------------------------+
 *                               (width, height)
 * </PRE>
 *
 * <P>
 * The outside border doesn't get drawn (that's up to the application).
 * The Goban will leave enough space between the outside border and the
 * first grid line (both horizontal and vertical) so that a stone, when
 * placed on an intersection, will have its edge touch the outside
 * border (presuming, of course, the stone is placed on an intersection
 * at the edge of the Goban).
 *
 * <P>
 * When dealing with the Goban, it is important to notice that locations
 * of the intersections are ZERO-based.  Thus the upper-left is at (0, 0),
 * the bottom right corner would be 18 x 18 for a 19-line board.
 *
 * <P>
 * Applications can simply add this subclass of Panel anywhere they would
 * normally place a Panel.  By using the API calls (such as placeStone,
 * removeMarkup, etc.) it's easy to change what's happening on the Goban
 * (provided you also tell it when to make its view current using
 * <CODE>forceRepaint()</CODE>--see the method for more information).
 *
 * <P>
 * Also note that the Goban knows nothing about the rules of Go.  This
 * is the responsiblity of the Rules class, which monitors the Goban for
 * stone placements (i.e., an Observer).  Since the Goban has no concept
 * of strings of stones, the Rules class must keep track of everything.
 * Even though this is a bit redundant, it allows for an enormous amount of
 * variations in what can be done with the Goban (e.g., a Rules class for
 * different games -- Go-moku, for instance).
 */
public final class Goban extends Panel
{
  /**
   * Most Gobans are 19 by 19.
   */
  public final static int DEFAULT_SIZE = 19;

  /**
   * Black is used for drawing lines.
   */
  private final static Color DEFAULT_FGCOLOUR = Color.black;

  /**
   * Colour used for highlighting intersections, if highlighting is enabled.
   */
  private final static Color DEFAULT_HLCOLOUR = new Color( 0xCC, 0xFF, 0x33 );

  /**
   * By default, lightgray is used for the board's background.
   */
  private final static Color DEFAULT_BGCOLOUR = Color.lightGray;

  private Color myFGColour = DEFAULT_FGCOLOUR,
                myBGColour = DEFAULT_BGCOLOUR,
                myHLColour = DEFAULT_HLCOLOUR;

  private Image myBGImage;

  private int myBoardSize = DEFAULT_SIZE;

  private Stone[][] myStones;
  private Markup[][] myMarks;

  private boolean drawMarkup = true,     // Draw board markup by default
                  drawBG = false,        // Becomes true when image is set
                  drawHoshi = true,      // Draw star-points by default
                  drawHighlight = true;  // Draw highlight by default

  private Stone mySizingStone;

  private Location myHighlight = new Location( -1, -1 );

  /**
   * The Goban delegates Observable-Observer information via the
   * GobanObserver class.
   */
  private GobanObserver myObserver = new GobanObserver();

  /** Used for double-buffering. */
  private Image myOffscreenImage;

  /**
   * Used for double double-buffering.  This image is the full Goban with
   * stones, board markup, and hoshi points.  It never has highlight.  This
   * is important for quickly repainting the board while highlighting
   * the intersection over which the mouse hovers.  Although redrawing
   * the entire board is fast, the mouse can always move faster ... So
   * this image prevents overflow of the repaint queue (which can crash the
   * Java virtual machine).
   */
  private Image myBasicBoard;

  /**
   * Creates a new instance of a Goban with default settings.  A Goban must
   * know the size of the stones that are going to be displayed on the board.
   * Black and White stones must have the same dimensions (given in pixels).
   * By changing the size of the sample stone, the size of the board will
   * change the next time drawn.  If a Goban is created without giving it a
   * stone, it will create a default (white) stone to use for pixel sizing.
   */
  public Goban()
  {
    this( new WhiteStone() );
  }

  /**
   * Creates a new instance of a Goban at 19 lines (width by height).
   *
   * @param stone - The stone to use for setting the Goban's dimensions.
   */
  public Goban( Stone stone )
  {
    this( DEFAULT_SIZE, stone );
  }

  /**
   * Creates a new instance of a Goban at a given size, with defaults set.
   *
   * @param size - How many horizontal and vertical gridlines for this Goban.
   * @param stone - The stone to use for setting the Goban's dimensions.
   */
  public Goban( int size, Stone stone )
  {
    this( size, DEFAULT_BGCOLOUR, stone );
  }

  /**
   * Creates a new instance of a Goban at a given size, with a given colour,
   * and remaining defaults set.
   *
   * @param size - How many horizontal and vertical gridlines for this Goban.
   * @param bgColour - The background colour to use for this Goban.
   * @param stone - The stone to use for setting the Goban's dimensions.
   */
  public Goban( int size, Color bgColour, Stone stone )
  {
    this( size, bgColour, null, stone );
  }

  /**
   * Creates a new instance of a Goban at a given size, with a given
   * background image, and remaining defaults set.
   *
   * @param size - How many horizontal and vertical gridlines for this Goban.
   * @param bgImage - The background image (wood grain) to use for this Goban.
   * @param stone - The stone to use for setting the Goban's dimensions.
   */
   public Goban( int size, Image bgImage, Stone stone )
  {
    this( size, DEFAULT_BGCOLOUR, bgImage, stone );
  }

  /**
   * The big daddy of Goban constructors.  This is used by all the other
   * constructors, so if you want to avoid a slight overhead, use this
   * one directly.
   *
   * @param size - How many horizontal and vertical gridlines for this Goban.
   * @param bgColour - The background colour to use for this Goban.
   * @param bgImage - The background image (wood grain) to use for this Goban.
   * @param stone - The stone to use for setting the Goban's dimensions.
   */
  public Goban( int size, Color bgColour, Image bgImage, Stone stone )
  {
    setSizingStone( stone );
    setBGColour( bgColour );
    setBGImage( bgImage );

    setBoardSize( size );
    initializeBoard();
    calculateSize();
  }

  /**
   * Creates a new instance of a Goban at a a given background image, and
   * remaining defaults set.
   *
   * @param bgImage - The background image (wood grain) to use for this Goban.
   * @param stone - The stone to use for setting the Goban's dimensions.
   */
  public Goban( Image bgImage, Stone stone )
  {
    this( DEFAULT_SIZE, DEFAULT_BGCOLOUR, bgImage, stone );
  }

  /**
   * Determines the minimum pixel height and width required to display this
   * Goban.  The size is calculated according to the sizing stone dimensions.
   *
   * @return true - The size has changed; false otherwise.
   */
  public void calculateSize()
  {
    int width = getSizingStone().getWidth() * getBoardSize(),
        height = getSizingStone().getHeight() * getBoardSize();

    // Only create a new minimum (preferred) size if we have to.
    //
    if( (getWidth() != width) || (getHeight() != height) )
    {
      setSize( new Dimension( width, height ) );

      // Force the next paint to draw everything.
      //
      setOffscreenImage( null );
    }
  }

  /**
   * Resets the board to a pristine state: no stones, no marks, no highlights.
   */
  public void initializeBoard()
  {
    int size = getBoardSize();

    myStones = new Stone[ size ][ size ];
    myMarks = new Markup[ size ][ size ];
  }

  public void update( Graphics graphics )
  {
    // If first time then create an offscreen buffer.
    //
    if( getOffscreenImage() == null )
    {
      setOffscreenImage( createImage( getWidth(), getHeight() ) );
      redraw();
    }

    graphics.drawImage( getOffscreenImage(), 0, 0, null );
  }

  /**
   * Called whenever damage occurs to the Goban's view.  Simply calls
   * update to repair things.
   */
  public void paint( Graphics graphics ) { update( graphics ); }

  /**
   * Asks the Goban to redraw everything: board marks, stones, etc.  This
   * ensures that the visual display of the Goban is synchronized with the
   * content of the Goban.
   * <P>
   * In order for the stone to appear, a call to "forceRepaint" is essential.
   * The reason this isn't done automatically is for efficiency's sake:
   * there's no point redrawing the board for each stone during a setup
   * phase, or if you are quickly scrolling to the end of a game (and thus
   * replaying all the moves on this Goban).
   * <P>
   * Another reason is because from a purist point of view, the methods
   * "placeStone" and "placeMark" should do that, and nothing else.  This
   * way if 100 marks and 50 stones were to be placed all at one time, they
   * could be made to appear simultaneously.  If each call to "placeStone"
   * and/or "placeMark" had the Goban update itself, then this case might
   * yield a "rolling effect" whereby you can actually tell that the Goban
   * is placing them and redrawing them one at a time (which looks
   * unprofessional; yet even if you wanted this effect, it can be produced
   * by adding a call to "forceRepaint" per-stone and/or per-mark).
   */
  public void forceRepaint()
  {
    getGobanObserver().setChanged();
    redraw();
  }

  /**
   * Used to draw the board on the offscreen graphics context.  The board is
   * drawn at (0, 0) and extends to its full width and height.  This is the
   * only place the entire contents of the board get painted to the offscreen
   * buffer.  Internally, there are two buffers.  One to be used by the
   * update method for bit-blasting, the other buffer stores an image of
   * the entire board without highlighting.
   * <P>
   * Since highlighting must paint rather quickly, there isn't enough time
   * to redraw everything all the time.  Thus, a basic board image is drawn,
   * stored, and highlighting can then be added to the basic image--provided
   * nothing about the Goban has changed since the last time the mouse
   * moved.
   */
  private void redraw()
  {
    Image image = getOffscreenImage();

    if( image == null )
      return;

    Graphics graphics = image.getGraphics();

    // If the board has changed, then we want to update the basic board to
    // reflect the current status of the board.  This allows highlighting to
    // operate at the speed of mouse movements without causing the applet
    // to crash.  (A bit-blasted image is orders of magnitude faster to draw
    // than trying to repaint the Goban's state from scratch every time.)
    //
    if( getGobanObserver().hasChanged() )
    {
      if( myBasicBoard == null )
        myBasicBoard = createImage( getWidth(), getHeight() );

      Graphics g = myBasicBoard.getGraphics();

      // 1. Draw the background image.
      //
      if( shouldDrawBG() )
        drawBG( g );
      else
      {
        g.setColor( getBGColour() );
        g.fillRect( 0, 0, getWidth(), getHeight() );
      }

      // 2. Draw the lines (having a toggle here would be a mind muck).
      //
      drawLines( g );

      // 3. Draw the hoshi points.
      //
      if( shouldDrawHoshi() )
        drawHoshi( g );

      // 4. Draw the stones.
      //
      drawStones( g );

      // 5. Draw the marks.
      //
      if( shouldDrawMarkup() )
        drawMarkup( g );

      getGobanObserver().clearChanged();
    }

    graphics.drawImage( myBasicBoard, 0, 0, null );

    // 6. Draw the highlight.
    //
    if( shouldDrawHighlight() )
      drawHighlight( graphics );

    // Make everything pretty.
    //
    repaint();
  }

  /**
   * Draws the board's background image, tiled.
   *
   * @param graphics - The graphics context on which the image is drawn.
   */
  private void drawBG( Graphics graphics )
  {
    Image image = getBGImage();
    int bgWidth = image.getWidth( null ),
        bgHeight = image.getHeight( null ),
        width = getWidth(),
        height = getHeight();

    // Figure out the number of times we have to tile the image; add one
    // because chances are the tiling won't work out to an even number of
    // tiles ... so we have to do an extra row and column to fill the gap.
    //
    // Normally the width (resp. height) would be (width / bgWidth) + 1,
    // and since we're looping from the bottom-right to the upper-left which
    // would require -1, we find that the + 1 and - 1 cancel out,  (This
    // is an added bonus to looping backwards, which is faster than looping
    // forwards since the compare operation to zero is faster than other
    // comparisons.)
    //
    for( int x = width / bgWidth; x >= 0; x-- )
      for( int y = height / bgHeight; y >= 0; y-- )
        graphics.drawImage( image, x * bgWidth, y * bgHeight, null );
  }

  /**
   * Draws the board's grid.
   *
   * @param graphics - The graphics context on which the lines are drawn.
   */
  private void drawLines( Graphics graphics )
  {
    int stoneWidth = getSizingStone().getWidth(),
        stoneHeight = getSizingStone().getHeight(),
        halfWidth = stoneWidth >> 1,
        halfHeight = stoneHeight >> 1,
        width = getWidth() - halfWidth - 1,
        height = getHeight() - halfHeight - 1,
        x = halfWidth,
        y = halfHeight;

    graphics.setColor( getFGColour() );

    // Draw the horizontal and veritcal lines.
    //
    for( int lines = getBoardSize() - 1; lines >= 0; lines-- )
    {
      // Draw vertical lines.
      //
      graphics.drawLine( halfWidth, y, width, y );

      // Draw horizontal lines.
      //
      graphics.drawLine( x, halfHeight, x, height );

      x += stoneWidth;
      y += stoneHeight;
    }
  }

  /**
   * Draws the board's stones.  This method actually asks each of its stones
   * to draw itself on the given graphics context at a particular location
   * (given in pixels).
   *
   * @param graphics - The graphics context on which the stones are drawn.
   */
  private void drawStones( Graphics graphics )
  {
    int size = getBoardSize() - 1,
        stoneWidth = getSizingStone().getWidth(),
        stoneHeight = getSizingStone().getHeight();

    for( int x = size; x >= 0; x-- )
    {
      int xPixel = x * stoneWidth;

      for( int y = size; y >= 0; y-- )
      {
        Stone stone = myStones[ x ][ y ];

        if( stone != null )
          stone.draw( graphics, xPixel, y * stoneHeight );
      }
    }
  }

  /**
   * Draws the board's hoshi points.
   *
   * @param graphics - The graphics context on which the hoshi are drawn.
   */
  private void drawHoshi( Graphics graphics )
  {
    // The "- 3" centres the hoshi point (6 x 6 pixels) at an intersection.
    //
    int size = getBoardSize(), 
        stoneWidth = getSizingStone().getWidth(),
        stoneHeight = getSizingStone().getHeight(),
        w2 = (stoneWidth >> 1) - 3,
        h2 = (stoneHeight >> 1) - 3;

    // No hoshi for Yoshi!
    //
    if( size < 9 )
      return;

    // Default hoshi point positions.
    //
    int n1 = 3, n2 = 9, n3 = 15;

    // Even though these seem like "magic" numbers, they aren't.  They are
    // the standard positions for hoshi points that have graced certain
    // intersections of Go boards for thousands of years.
    //
    if( size < 13 )
    {
      n1 = 2; n2 = 4; n3 = 6;
    }
    else if( size < 19 )
    {
      n1 = 3; n2 = 6; n3 = 9;
    }

    int n1w = n1 * stoneWidth + w2,
        n2w = n2 * stoneWidth + w2,
        n3w = n3 * stoneWidth + w2;

    int n1h = n1 * stoneHeight + h2,
        n2h = n2 * stoneHeight + h2,
        n3h = n3 * stoneHeight + h2;

    graphics.setColor( getFGColour() );

    // 6 = size of hoshi point.  Seems to be a good number.
    //
    graphics.fillOval( n1w, n1h, 6, 6 );
    graphics.fillOval( n1w, n2h, 6, 6 );
    graphics.fillOval( n1w, n3h, 6, 6 );

    graphics.fillOval( n2w, n1h, 6, 6 );
    graphics.fillOval( n2w, n2h, 6, 6 );
    graphics.fillOval( n2w, n3h, 6, 6 );

    graphics.fillOval( n3w, n1h, 6, 6 );
    graphics.fillOval( n3w, n2h, 6, 6 );
    graphics.fillOval( n3w, n3h, 6, 6 );
  }

  /**
   * Draws the board's markup.
   *
   * @param graphics - The graphics context on which the markup is drawn.
   */
  private void drawMarkup( Graphics graphics )
  {
    int size = getBoardSize() - 1,
        stoneWidth = getSizingStone().getWidth(),
        stoneHeight = getSizingStone().getHeight();

    Markup markup = null;

    for( int x = size; x >= 0; x-- )
    {
      int xPixel = x * stoneWidth;

      for( int y = size; y >= 0; y-- )
        if( (markup = myMarks[ x ][ y ]) != null )
        {
          // If a black stone is present, make the markup white.  Otherwise
          // always use black.  This behaviour really doesn't belong here.
          // The board markup should decide its colour for itself ...?
          //
          if( hasBlackStone( x, y ) )
            markup.setColour( Color.white );
          else
            markup.setColour( Color.black );

          markup.draw( graphics, xPixel,     (y * stoneHeight) - 1,
                                 stoneWidth, stoneHeight );
        }
    }
  }

  /**
   * If an intersection is supposed to be highlighted, this does it.
   */
  private void drawHighlight( Graphics graphics )
  {
    int stoneWidth = getSizingStone().getWidth(),
        stoneHeight = getSizingStone().getHeight();

    Location location = getHighlight();

    // Whoops!
    //
    if( !isValid( location ) )
      return;

    int x = stoneWidth * location.x,
        y = stoneHeight * location.y,
        w2 = stoneWidth >> 1,
        h2 = stoneHeight >> 1;

    graphics.setColor( getHighlightColour() );
    graphics.fillRect( x, y, stoneWidth, stoneHeight );

    graphics.setColor( getFGColour() );
    graphics.drawLine( x + w2, y, x + w2, y + stoneHeight );
    graphics.drawLine( x, y + h2, x + stoneWidth, y + h2 );
  }

  private Color getHighlightColour() { return myHLColour; }

  /**
   * Places a stone on the board, then notifies anyone who is observing that
   * the given location has been updated with a new Stone.  Either use
   * "null" stone to remove a stone, or call the helper method "removeStone".
   * <P>
   * Stones don't magically appear, you must use "forceRepaint".
   *
   * @param stone - The stone to place
   * @param location - Where to place the stone
   */
  public void placeStone( Stone stone, Location location )
  {
    myStones[ location.x ][ location.y ] = stone;

    if( stone != null )
      getGobanObserver().notifyObservers( location );
  }

  /**
   * Helper method.  This simply calls "placeStone" with the same location
   * and a null stone.
   * <P>
   * Stones don't magically disappear, you must use "forceRepaint".
   *
   * @param location - Where to remove the stone
   */
  public void removeStone( Location location )
  {
    placeStone( null, location );
  }

  /**
   * Quickly removes all stones from the board.  This is much faster than
   * calling removeStone or placeStone.
   */
  public void removeAllStones()
  {
    int size = getBoardSize();

    for( int x = size - 1; x >= 0; x-- )
      for( int y = size - 1; y >= 0; y-- )
        myStones[ x ][ y ] = null;
  }

  /**
   * Helper method.
   */
  protected boolean hasStone( int x, int y )
  {
    return myStones[ x ][ y ] != null;
  }

  /**
   * Lets the world know if the board has a stone at a given location.
   *
   * @param location - The intersection to examine for a stone.
   */
  public boolean hasStone( Location location )
  {
    return hasStone( location.x, location.y );
  }

  /**
   * Helper method.
   */
  protected boolean hasWhiteStone( int x, int y )
  {
    Stone stone = myStones[ x ][ y ];
    return (stone == null) ? false : stone.isWhite();
  }

  /**
   * Lets the world know if the board has a White stone at a given location.
   * If no stone is located at the given location, this method returns false.
   *
   * @param location - The intersection to examine for a White stone.
   * @return true - There is a white stone at the given location.
   */
  public boolean hasWhiteStone( Location location )
  {
    return hasWhiteStone( location.x, location.y );
  }

  /**
   * Helper method.
   */
  protected boolean hasBlackStone( int x, int y )
  {
    Stone stone = myStones[ x ][ y ];
    return (stone == null) ? false : !stone.isWhite();
  }

  /**
   * Lets the world know if the board has a Black stone at a given location.
   * If no stone is located at the given location, this method returns false.
   *
   * @param location - The intersection to examine for a Black stone.
   * @return true - There is a black stone at the given location.
   */
  public boolean hasBlackStone( Location location )
  {
    return hasBlackStone( location.x, location.y );
  }

  /**
   * Places a mark on the board.
   * <P>
   * Marks don't magically appear, you must use "forceRepaint".
   *
   * @param markup - What type of markup to use at the given location
   * @param location - Where to place the mark
   */
  public void placeMark( Markup markup, Location location )
  {
    myMarks[ location.x ][ location.y ] = markup;
  }

  /**
   * Helper method.  This simply calls "placeMark" with the same (x, y)
   * pair and null for the board mark.
   * <P>
   * Marks don't magically disappear, you must use "forceRepaint".
   *
   * @param location - Where to remove the mark
   */
  public void removeMark( Location location )
  {
    placeMark( null, location );
  }

  /**
   * Quickly removes all marks from the board.  This is faster than calling
   * either placeMark or removeMark.
   * <P>
   * Marks don't magically disappear, you must use "forceRepaint".
   */
  public void removeAllMarks()
  {
    int size = getBoardSize();

    for( int x = size - 1; x >= 0; x-- )
      for( int y = size - 1; y >= 0; y-- )
        myMarks[ x ][ y ] = null;
  }

  /**
   * Removes all stones and marks from the board.  This method uses
   * removeAllStones and removeAllMarks.
   * <P>
   * Everything doesn't magically disappear, you must use "forceRepaint".
   */
  public void removeAll()
  {
    removeAllStones();
    removeAllMarks();
  }

  /**
   * Overrides the default behaviour for addObserver in order to ensure that
   * the observer may be added at most once.  Observers are told when a stone
   * has been placed on the board.  They are not told when a stone is removed.
   * The second argument to the corresponding "update" method is the location
   * at which the stone was added.
   *
   * <P>
   * Notice that this delegates the request to our internal GobanObserver
   * instance.
   */
  public synchronized void addObserver( Observer o )
  {
    getGobanObserver().addObserver( o );
  }

  /**
   * Answers whether the given (x, y) coordinates are valid for this Goban.
   *
   * @return true - The given coordinates can be found on this Goban.
   */
  private boolean isValid( int x, int y )
  {
    // If the x values are valid and the y values are valid, then the
    // coordinates are valid.
    //
    return ((x >= 0) && (x < getBoardSize())) &&
           ((y >= 0) && (y < getBoardSize()));
  }

  /**
   * Answers whether the given location is valid for this Goban.  This is
   * a helper method.
   *
   * @return true - The given location can be found on this Goban.
   */
  private boolean isValid( Location location )
  {
    return isValid( location.x, location.y );
  }

  /**
   * Helper method.
   */
  private Stone getStone( int x, int y )
  {
    return myStones[ x ][ y ];
  }

  /**
   * Classes within this package may get a handle to the stone at the given
   * location.  But since this is kind of dangerous (as setting its width
   * and height is a bad thing), only classes within the package will have
   * this ability.
   *
   * @param location - Where to look for a stone on the Goban.
   * @return The stone at the location.
   * @throws ArrayIndexOutOfBoundsException
   */
  protected Stone getStone( Location location )
  {
    return getStone( location.x, location.y );
  }

  /**
   * Specifies the preferred area (in pixels) that the Goban needs in order
   * to display a full grid such that all the stones will be visible.
   */
  public Dimension getPreferredSize() { return getSize(); }

  /**
   * See getPreferredSize.
   */
  public Dimension getMinimumSize() { return getPreferredSize(); }

  /**
   * Helper method; returns the Goban's width in pixels.
   */
  public int getWidth() { return getPreferredSize().width; }

  /**
   * Helper method; returns the Goban's height in pixels.
   */
  public int getHeight() { return getPreferredSize().height; }

  /**
   * Changes the number of lines for this Goban.
   *
   * @param size - The number of lines displayed by this Goban.
   */
  private void setBoardSize( int size ) { myBoardSize = size; }

  /**
   * Returns the number which represents both the number of rows and columns
   * for this Goban's grid.
   *
   * @return The number of lines for this Goban.
   */
  protected int getBoardSize() { return myBoardSize; }

  /**
   * @return The background image (wood grain) used when drawing the Goban
   * (the image is tiled).
   */
  private Image getBGImage() { return myBGImage; }

  /**
   * Changes the background image used when drawing the Goban.
   *
   * @param image - The new background image.
   */
  public void setBGImage( Image image )
  {
    setDrawBG( image != null );

    myBGImage = image;
  }

  /**
   * Calculates the location that the pixel coordinates map to for this
   * Goban.  Even though it is bad form to pass in the location, it
   * saves from having to create a new instance Location each time.  Since
   * "translateCoord" can be called quite often, this is a necessity.  If
   * this method returns true, then the given coordinates were successfully
   * mapped to their equivalent intersection on the Goban; the result is
   * stored in Location's (x, y) pair.
   *
   * @param mouseX - The x value (in pixels) of the intersetion to retrieve.
   * @param mouseX - The y value (in pixels) of the intersetion to retrieve.
   * @param location - Where to put the resulting intersection.
   * @return true - The given location has valid values.
   */
  public boolean translateCoord( int mouseX, int mouseY, Location location )
  {
    if( (mouseX < 0) || (mouseX > getWidth()) ||
        (mouseY < 0) || (mouseY > getHeight()) )
      return false;

    // mouseX and mouseY are now guaranteed to be somewhere on the board;
    // calculate the position.
    //
    location.x = mouseX / getSizingStone().getWidth();
    location.y = mouseY / getSizingStone().getHeight();

    return true;
  }

  private void setOffscreenImage( Image image ) { myOffscreenImage = image; }
  private Image getOffscreenImage() { return myOffscreenImage; }

  /**
   * Returns the stone used to calculate the width and height of the Goban.
   *
   * @return The stone used to calculate the width and height of the Goban
   * in pixels.
   */
  public Stone getSizingStone() { return mySizingStone; }

  /**
   * Changes the stone used to calculate the width and height of the Goban
   * in pixels.
   *
   * @param stone - The new sizing stone.
   */
  private void setSizingStone( Stone stone ) { mySizingStone = stone; }

  /**
   * Changes the foreground colour used in drawing hoshi and board lines.
   *
   * @param colour - The new foreground colour.
   */
  public void setFGColour( Color colour ) { myFGColour = colour; }

  /**
   * @return The foreground colour used in drawing hoshi and board lines.
   */
  private Color getFGColour() { return myFGColour; }

  /**
   * Changes the background colour used for the board.
   *
   * @param colour - The new background colour.
   */
  public void setBGColour( Color colour ) { myBGColour = colour; }

  /**
   * @return The background colour used for the board.
   */
  private Color getBGColour() { return myBGColour; }

  private Location getHighlight() { return myHighlight; }

  /**
   * Answers whether the given location is the same spot as the Goban's
   * currently highlighted location.
   *
   * @param location - The Goban location to check for a highlight.
   * @return true - The locations are the same.
   */
  public boolean isHighlighted( Location location )
  {
    return getHighlight().equals( location );
  }

  /**
   * Checks to see if highlighting on.
   *
   * @return true - Highlighting is being added to this Goban.
   */
  public boolean isHighlighted()
  {
    return (myHighlight.x != -1) && shouldDrawHighlight();
  }

  /**
   * Asks the Goban to highlight a particular intersection.
   *
   * @param location - Where the Goban should highlight.
   */
  public void setHighlight( int x, int y )
  {
    if( isValid( x, y ) )
    {
      myHighlight.x = x;
      myHighlight.y = y;
    }
    else
      myHighlight.x = -1;

    redraw();
  }

  /**
   * Helper method.  Simply calls "setHighlight( x, y )" with the values
   * stored in the Location parameter.
   *
   * @param location - Where the Goban should highlight an intersection.
   */
  public void setHighlight( Location location )
  {
    setHighlight( location.x, location.y );
  }

  /**
   * Asks the Goban to clear its highlighted intersection.  Nothing happens
   * if the highlighting was already cleared.
   */
  public void clearHighlight()
  {
    setHighlight( -1, -1 );
  }

  /**
   * Answers whether this Goban is drawing a background image (typically
   * a wood grain).
   *
   * @return true - The background image should be drawn.
   * @return false - The background image should not be drawn.
   */
  public boolean shouldDrawBG() { return drawBG; }

  /**
   * Asks the Goban to draw its background image, if it isn't,
   * or to stop if it is.
   */
  public void toggleBackground() { setDrawBG( !shouldDrawBG() ); }

  /**
   * Changes whether the background image is drawn.  The default is to draw.
   *
   * @param b - Set true if the background image should be drawn.
   */
  private void setDrawBG( boolean b )
  {
    drawBG = b;
    forceRepaint();
  }

  /**
   * Answers whether this Goban is drawing hoshi points.
   *
   * @return true - The hoshi (star points) should be drawn.
   * @return false - The hoshi (star points) should not be drawn.
   */
  public boolean shouldDrawHoshi() { return drawHoshi; }

  /**
   * Asks the Goban to draw its hoshi points, if it isn't,
   * or to stop if it is.
   */
  public void toggleHoshi() { setDrawHoshi( !shouldDrawHoshi() ); }

  /**
   * Changes whether the hoshi points are drawn.  The default is to draw.
   *
   * @param b - Set true if the hoshi points should be drawn.
   */
  private void setDrawHoshi( boolean b )
  {
    drawHoshi = b;
    forceRepaint();
  }

  /**
   * Answers whether this Goban is drawing board markup.
   *
   * @return true - The board markup should be drawn.
   * @return false - The boark markup should not be drawn.
   */
  public boolean shouldDrawMarkup() { return drawMarkup; }

  /**
   * Asks the Goban to draw board markup if it isn't, otherwise stop.
   * Markup is drawn by default.
   */
  public void toggleMarkup() { setDrawMarkup( !drawMarkup ); }

  /**
   * Changes whether the markup is drawn.  The default is to draw.
   *
   * @param b - Set true if the markup should be drawn.
   */
  private void setDrawMarkup( boolean b )
  {
    drawMarkup = b;
    forceRepaint();
  }

  /**
   * Answers whether this Goban is drawing highlighted intersections.
   *
   * @return true - The highlighting should be drawn.
   * @return false - The highlighting should not be drawn.
   */
  public boolean shouldDrawHighlight() { return drawHighlight; }

  /**
   * Asks the Goban to begin drawing highlight if it isn't, otherwise stop.
   * Highlighting is drawn by default.  Although useless without having
   * instantiated a GobanHighlighter ...
   */
  public void toggleHighlight() { setDrawHighlight( !shouldDrawHighlight() ); }

  /**
   * Changes whether the highlight is drawn.  The default is to draw.
   * Although useless without having instantiated a GobanHighlighter ...
   *
   * @param b - Set true if the highlight should be drawn.
   */
  private void setDrawHighlight( boolean b )
  {
    drawHighlight = b;
    forceRepaint();
  }

  /**
   * Creates a copy of this Goban's graphics image.
   *
   * @param graphics - The graphics context on which to draw the Goban.
   */
  public void copyGraphics( Graphics graphics )
  {
    graphics.drawImage( getOffscreenImage(), 0, 0, null );
  }

  private GobanObserver getGobanObserver() { return myObserver; }

  /**
   * Used to inform observers of changes to a Goban.  Ideally, the Goban
   * should be observable, but since the Goban <B>must</B> be a Panel
   * (in order to implement a complicated off-screen double double-buffering
   * technique), this becomes the second-best way to allow Goban observers.
   *
   * <P>
   * Inner classes require a JDK v1.1 compiler; they are v1.0.2 compatible.
   */
  public class GobanObserver extends Observable
  {
    protected GobanObserver()
    {
      setChanged();
    }

    /**
     * Adds an Observer to this Observable subclass.  Doesn't allow multiple
     * observers on the same Goban.  This method is public not by choice;
     * ideally it should be protected; however since the constructor is
     * protected, this shouldn't cause any glaring encapsulation holes.
     *
     * @param o - The observer to add
     */
    public void addObserver( Observer o )
    {
      deleteObserver( o );
      super.addObserver( o );
    }

    /**
     * Indicates this Goban has changed in some way.
     */
    protected void setChanged()
    {
      super.setChanged();
    }

    /**
     * Indicates this Goban has no more changes.
     */
    protected void clearChanged()
    {
      super.clearChanged();
    }

    /**
     * Used to tell all Observers that the given Location on the Goban has
     * changed.  At the moment, this only happens when a stone is added.
     *
     * @param location - The location on the Goban that has changed.
     */
    protected void notifyObservers( Location location )
    {
      setChanged();
      super.notifyObservers( location );
    }
  }
}
