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

package ca.dj.jigo.designer;

import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Image;
import java.awt.Rectangle;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.event.WindowEvent;

import java.io.IOException;

import java.net.InetAddress;

import java.util.Calendar;

import ca.dj.awt.widgets.Toolbar;
import ca.dj.awt.widgets.ImageButton;
import ca.dj.awt.widgets.ToggleImageButton;

import ca.dj.jigo.BlackStone;
import ca.dj.jigo.Goban;
import ca.dj.jigo.GobanFrame;
import ca.dj.jigo.JiGoApplet;
import ca.dj.jigo.Location;
import ca.dj.jigo.WhiteStone;

import ca.dj.util.images.ImageWriter;

/**
 * The frame that houses a Goban.
 */
public class DesignerFrame extends GobanFrame
  implements ActionListener, ComponentListener,
             MouseListener, MouseMotionListener
{
  private final static String TB_DIRECTORY = "images/toolbar/";

  private final static String CHECKMARK    = "chk-",
                              NO_CHECKMARK = "tb-";

  /** Action commands for the action event; numbers faster than strings. */
  private final static int BSTONE   = 1,
                           WSTONE   = 2,
                           CIRCLE   = 3,
                           SQUARE   = 4,
                           TRIANGLE = 5,
                           NUMBERS  = 6,
                           LETTERS  = 7,
                           SAVE     = 8;

  private final static String BSTONE_FILE   = "bstone.gif",
                              WSTONE_FILE   = "wstone.gif",
                              CIRCLE_FILE   = "circle.gif",
                              SQUARE_FILE   = "square.gif",
                              TRIANGLE_FILE = "triangle.gif",
                              NUMBERS_FILE  = "numbers.gif",
                              LETTERS_FILE  = "letters.gif",
                              SAVE_FILE     = "save.gif";

  private Toolbar myToolbar = new Toolbar();

  /** The button that was last toggled when the user toggled another button. */
  private ToggleImageButton currToggleButton = null;

  /** Where intersection where the mouse clicked. */
  private Location myClickLocation = new Location( 0, 0 );

  public DesignerFrame( String title, JiGoApplet applet )
  {
    super( title, applet );

    createToolbar();

    addWindowListener( this );
    addComponentListener( this );

    // Stay in touch of the Goban's mouse clicks, and mouse drags.
    //
    getGoban().addMouseListener( this );
    getGoban().addMouseMotionListener( this );
  }

  protected void initGUI()
  { 
    add( getGoban() );
    pack();
    setResizable( false );
  }

  private void createToolbar()
  {
    JiGoApplet applet = getJiGoApplet();
    Toolbar tb = getToolbar();

    // The first button on the toolbar (the white stone) should be set to
    // our currently toggled button.  Furthermore, it should be toggled on
    // immediately.
    //
    currToggleButton = new ToggleImageButton(
      applet.loadImage( TB_DIRECTORY + CHECKMARK + WSTONE_FILE ),
      applet.loadImage( TB_DIRECTORY + NO_CHECKMARK + WSTONE_FILE ),
      Integer.toString( WSTONE ) );

    currToggleButton.toggleOn();

    tb.addButton( currToggleButton );

    tb.addButton( new ToggleImageButton(
      applet.loadImage( TB_DIRECTORY + CHECKMARK + BSTONE_FILE ),
      applet.loadImage( TB_DIRECTORY + NO_CHECKMARK + BSTONE_FILE ),
      Integer.toString( BSTONE ) ) );

    tb.addButton( new ToggleImageButton(
      applet.loadImage( TB_DIRECTORY + CHECKMARK + CIRCLE_FILE ),
      applet.loadImage( TB_DIRECTORY + NO_CHECKMARK + CIRCLE_FILE ),
      Integer.toString( CIRCLE ) ) );

    tb.addButton( new ToggleImageButton(
      applet.loadImage( TB_DIRECTORY + CHECKMARK + SQUARE_FILE ),
      applet.loadImage( TB_DIRECTORY + NO_CHECKMARK + SQUARE_FILE ),
      Integer.toString( SQUARE ) ) );

    tb.addButton( new ToggleImageButton(
      applet.loadImage( TB_DIRECTORY + CHECKMARK + TRIANGLE_FILE ),
      applet.loadImage( TB_DIRECTORY + NO_CHECKMARK + TRIANGLE_FILE ),
      Integer.toString( TRIANGLE ) ) );

    tb.addButton( new ToggleImageButton(
      applet.loadImage( TB_DIRECTORY + CHECKMARK + NUMBERS_FILE ),
      applet.loadImage( TB_DIRECTORY + NO_CHECKMARK + NUMBERS_FILE ),
      Integer.toString( NUMBERS ) ) );

    tb.addButton( new ToggleImageButton(
      applet.loadImage( TB_DIRECTORY + CHECKMARK + LETTERS_FILE ),
      applet.loadImage( TB_DIRECTORY + NO_CHECKMARK + LETTERS_FILE ),
      Integer.toString( LETTERS ) ) );

    tb.addButton( new ImageButton(
      applet.loadImage( TB_DIRECTORY + NO_CHECKMARK + SAVE_FILE ),
      Integer.toString( SAVE ) ) );

    tb.addActionListener( this );
  }

  private String createFileName()
  {
    Calendar calendar = Calendar.getInstance();
    int hour   = calendar.get( Calendar.HOUR   ),
        minute = calendar.get( Calendar.MINUTE ),
        second = calendar.get( Calendar.SECOND );

    String systemDate =
      ((hour   < 10) ? ("0" + hour  ) : "" + hour  ) + "-" +
      ((minute < 10) ? ("0" + minute) : "" + minute) + "-" +
      ((second < 10) ? ("0" + second) : "" + second);

    try
    {
      return InetAddress.getLocalHost().getHostName() + "-" + systemDate;
    }
    catch( Exception e ) { }

    return null;
  }

  private void saveImage()
  {
    Dimension d = getGoban().getSize();
    Image screenShot = createImage( d.width, d.height );

    getGoban().copyGraphics( screenShot.getGraphics() );

    try
    {
      ImageWriter.writeImage( screenShot, createFileName(), getJiGoApplet() );
    }
    catch( Exception e )
    {
      System.out.println( e.toString() );
    }
  }

  private void showDefaultCursor()
  {
    Cursor cursor = Cursor.getPredefinedCursor( Cursor.DEFAULT_CURSOR );
    getToolbar(). setCursor( cursor );
    setCursor( cursor );
  }

  private void showWaitCursor()
  {
    Cursor cursor = Cursor.getPredefinedCursor( Cursor.WAIT_CURSOR );
    getToolbar(). setCursor( cursor );
    setCursor( cursor );
  }

  public void actionPerformed( ActionEvent ae )
  {
    int action = Integer.parseInt( ae.getActionCommand() );

    // The save button isn't a toggle button, so we have to treat it
    // special.
    //
    if( action == SAVE )
    {
      showWaitCursor();
      saveImage();
      showDefaultCursor();
      return;
    }

    ToggleImageButton toggledButton = (ToggleImageButton)(ae.getSource());

    // If the button that was just pressed has gone into the On state, then
    // we want to turn our current button Off.
    //
    if( toggledButton.isToggledOn() )
      currToggleButton.toggleOff();
    else
      currToggleButton.toggleOn();

    // Save the currently On button for the next time the user clicks.
    //
    currToggleButton = toggledButton;
  }

  /**
   * State Machine for Mouse Clicks.  Legend:
   * <PRE>
   * N = No Modifier
   * S = Shift
   * C = Control
   * A = Alt
   * </PRE>
   * 
   * <PRE>
   * Modifier | Stone/Mark | Action
   * ---------+------------+------------------
   * N        | Yes  /No   | Remove Stone
   * N        | Yes  /Yes  | Remove Stone
   * N        | No   /No   | Add White Stone
   * N        | No   /Yes  | Add White Stone
   * S        | No   /Yes  | Add Black Stone
   * S        | No   /No   | Add Black Stone
   * S        | Yes  /No   | Toggle Stone Colour
   * S        | Yes  /Yes  | Toggle Stone Colour
   * C        | No   /No   | Add Circle
   * C        | No   /Yes  | Toggle Mark: SQ -> TR -> Circle -> SQ
   *
   * </PRE>
   */
  public void mousePressed( MouseEvent me )
  {
    Goban goban = getGoban();
    WhiteStone wStone = getJiGoApplet().getWhiteStone();
    BlackStone bStone = getJiGoApplet().getBlackStone();

    // Figure out the intersection where the user clicked.
    //
    if( !goban.translateCoord( me.getX(), me.getY(), myClickLocation ) )
      return;

    // Get the modifiers at the time of the click.
    //
    int mods = me.getModifiers();
    boolean shift = (mods & MouseEvent.SHIFT_MASK) == MouseEvent.SHIFT_MASK,
            control = (mods & MouseEvent.CTRL_MASK) == MouseEvent.CTRL_MASK;

    boolean hasStone = goban.hasStone( myClickLocation );

    // Nothing was held during the click; this is a Stone operation.
    //
    if( !shift && !control )
    {
      // If there is a stone, then remove it, otherwise add a white stone.
      //
      if( hasStone )
        goban.removeStone( myClickLocation );
      else
        goban.placeStone( wStone, myClickLocation );

      goban.forceRepaint();
    }
    else if( shift )
    {
      // Shift was held ... place a white stone, or toggle the current.
      //
      if( hasStone )
      {
        if( goban.hasWhiteStone( myClickLocation ) )
          goban.placeStone( bStone, myClickLocation );
        else
          goban.placeStone( wStone, myClickLocation );
      }
      else
        goban.placeStone( bStone, myClickLocation );

      goban.forceRepaint();
    }
  }

  public void mouseDragged( MouseEvent me )
  {
    Goban goban = getGoban();
    WhiteStone wStone = getJiGoApplet().getWhiteStone();
    BlackStone bStone = getJiGoApplet().getBlackStone();

    if( !goban.translateCoord( me.getX(), me.getY(), myClickLocation ) )
      return;

    boolean hasStone = goban.hasStone( myClickLocation );

    if( hasStone )
      return;

    if( (me.getModifiers() & MouseEvent.SHIFT_MASK) == MouseEvent.SHIFT_MASK )
      goban.placeStone( bStone, myClickLocation );
    else
      goban.placeStone( wStone, myClickLocation );

    goban.forceRepaint();
  }

  public void mouseMoved( MouseEvent me ) { }

  public void mouseClicked( MouseEvent me ) { }
  public void mouseReleased( MouseEvent me ) { }
  public void mouseExited( MouseEvent me ) { }
  public void mouseEntered( MouseEvent me ) { }

  /**
   * Helper method.  Used to attach the toolbar frame to this frame.
   */
  private void attachToolbar()
  {
    Rectangle rect = getBounds();
    Toolbar tb = getToolbar();
    tb.setLocation( rect.x + rect.width + 1, rect.y );
    tb.setVisible( true );
    tb.toFront();
  }

  public void windowClosing( WindowEvent we )
  {
    Toolbar tb = getToolbar();
    tb.setVisible( false );
    tb.dispose();
    super.windowClosing( we );
  }

  public void windowIconified( WindowEvent we )
  {
    getToolbar().setVisible( false );
  }

  public void windowActivated( WindowEvent we )
  {
    attachToolbar();
  }

  public void componentMoved( ComponentEvent compEvent )
  {
    attachToolbar();
  }

  public void componentResized( ComponentEvent compEvent )
  {
    attachToolbar();
  }

  public void componentHidden( ComponentEvent compEvent )
  {
    getToolbar().setVisible( false );
  }

  public void componentShown( ComponentEvent compEvent )
  {
    attachToolbar();
  }

  private void setToolbar( Toolbar tb ) { myToolbar = tb; }
  private Toolbar getToolbar() { return myToolbar; }
}

