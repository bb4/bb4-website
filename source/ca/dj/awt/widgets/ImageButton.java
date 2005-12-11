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

package ca.dj.awt.widgets;

import java.awt.Color;
import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Image;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseListener;
import java.awt.event.MouseEvent;

import java.util.Enumeration;
import java.util.Vector;

/**
 * The ImageButton class places an image on a button which
 * handles its own mouse events.  Mouse overs are in the works.
 */
public class ImageButton extends Container implements MouseListener
{
  private Image myImage,
                myDisabledImage;

  /** Stores a list of ActionListeners who get modified on button clicks. */
  private Vector myListeners = new Vector( 1 );

  private final static Color UL_OUT = new Color( 0xC0, 0xC0, 0xC0 );
  private final static Color UL_IN  = new Color( 0xEE, 0xEE, 0xEE );
  private final static Color LR_IN  = new Color( 0x80, 0x80, 0x80 );
  private final static Color LR_OUT = new Color( 0x00, 0x00, 0x00 );

  /** Set to true when the mouse clicks the button */
  protected boolean amPressed = false;

  /** Need to know if the mouse was pressed in case it leaves the button. */
  private boolean wasPressed = false;

  private String myCommand;

  /**
   * Constructs an image button with specified image.  Don't pass in a null
   * image.  By default the button is enabled.
   *
   * @param image - What to display on the button.
   * @param command - The name of the command associated with this button.
   */
  public ImageButton( Image image, String command )
  {
    this( image, null, command );
  }

  /**
   * Constructs an image button with specified image and disabled image.
   * Don't pass in null images.  By default the button is enabled.
   *
   * @param image - What to display on the button, when enabled.
   * @param disabled - What to display on the button, when disabled.
   * @param command - The name of the command associated with this button.
   */
  public ImageButton( Image image, Image disabled, String command )
  {
    setCommand( command );
    setImage( image );
    setDisabledImage( disabled );
    setSize( new Dimension(
      image.getWidth( null ) + 4, image.getHeight( null ) + 4 ) );

    addMouseListener( this );

    // Default behaviour is to be enabled.
    //
    setEnabled( true );
  }

  /**
   * Paints the button.  The "paint" method should simply call update in
   * order to reduce flicker.  Most people get this wrong by having update
   * call paint ... Furthermore, we should use double-buffering.
   */
  public void update( Graphics graphics )
  {
    // Pressed down vs. normal press, only if the button is enabled.
    //
    if( isPressed() && isEnabled() )
      drawLoweredBorder( graphics );
    else
      drawRaisedBorder( graphics );

    Image toDraw = getDisabledImage();

    // If the button is enabled, then we can draw the image.  If there is
    // no disabled image, then we can draw the image.  Otherwise, if the
    // button is disabled and there isn't a disabled image, we can show a
    // disabled button.
    //
    if( isEnabled() || toDraw == null )
      toDraw = getImage();

    // Centre the image about the button.
    //
    Dimension totalSize = getSize();

    // The number of pixels we have to shift the image up and left
    // in order to centre it about the middle of the button.
    //
    int imageHeight = toDraw.getHeight( null ) >> 1,
        imageWidth = toDraw.getWidth( null ) >> 1;

    // Calculate the centre of the button.
    //
   int halfHeight = totalSize.height >> 1,
       halfWidth = totalSize.width >> 1;

    graphics.drawImage( toDraw,
      halfWidth - imageWidth + 1, halfHeight - imageHeight + 1, this );
  }

  /**
   * Simply calls update to refresh the dirty graphics context.
   */
  public void paint( Graphics graphics )
  {
    update( graphics  );
  }

  /**
   * Draws a raised border (for around the image).
   */
  private void drawRaisedBorder( Graphics graphics )
  {
    int w = getPreferredSize().width,
        h = getPreferredSize().height;

    // First, draw the outer: |~
    //
    graphics.setColor( UL_OUT );
    graphics.drawLine( 0, 0, 0, h );      // All the way down
    graphics.drawLine( 0, 0, w - 1, 0 );  // Nearly all the way right

    // Then, draw the inner: |~
    //
    graphics.setColor( UL_IN );
    graphics.drawLine( 1, 1, 1, h - 1 );  // Nearly all the way down
    graphics.drawLine( 1, 1, w - 2, 1 );  // Almost all the way right

    // Next, draw the inner: _|
    //
    graphics.setColor( LR_IN );
    graphics.drawLine( 2, h - 1, w - 1, h - 1 );  // Nearly all the way right
    graphics.drawLine( w - 1, 1, w - 1, h - 1 );  // Almost all the way down

    // Then, draw the outer: _|
    //
    graphics.setColor( LR_OUT );
    graphics.drawLine( 1, h, w, h );  // All the way right
    graphics.drawLine( w, 0, w, h );  // All the way down
  }

  /**
   * Draws a lowered border (for around the image).
   */
  private void drawLoweredBorder( Graphics graphics )
  {
    int w = getPreferredSize().width,
        h = getPreferredSize().height;

    // First, draw the outer: |~
    //
    graphics.setColor( LR_OUT );
    graphics.drawLine( 0, 0, 0, h );      // All the way down
    graphics.drawLine( 0, 0, w - 1, 0 );  // Nearly all the way right

    // Then, draw the inner: |~
    //
    graphics.setColor( LR_IN );
    graphics.drawLine( 1, 1, 1, h - 1 );  // Nearly all the way down
    graphics.drawLine( 1, 1, w - 2, 1 );  // Almost all the way right

    // Next, draw the inner: _|
    //
    graphics.setColor( UL_IN );
    graphics.drawLine( 2, h - 1, w - 1, h - 1 );  // Nearly all the way right
    graphics.drawLine( w - 1, 1, w - 1, h - 1 );  // Almost all the way down

    // Then, draw the outer: _|
    //
    graphics.setColor( UL_OUT );
    graphics.drawLine( 1, h, w, h );  // All the way right
    graphics.drawLine( w, 0, w, h );  // All the way down
  }

  public void mouseClicked( MouseEvent me )
  {
    if( !isEnabled() )
      return;

    ActionEvent ae = new ActionEvent(
      this, ActionEvent.ACTION_PERFORMED, getCommand(), me.getModifiers() );

    Enumeration e = getListeners();

    while( e.hasMoreElements() )
      try
      {
        ((ActionListener)(e.nextElement())).actionPerformed( ae );
      }
      catch( Exception exception ) { }
  }

  public void mousePressed( MouseEvent me )
  {
    wasPressed = true;
    setPressed( true );
    repaint();
  }

  public void mouseReleased( MouseEvent me )
  {
    wasPressed = false;
    setPressed( false );
    repaint();
  }

  /**
   * The button should push itself back up when the mouse stops hovering.
   * However, if the user holds the mouse down and comes back over the button,
   * the button should return to its former pressed state.  The "wasPressed"
   * variable handles this condition (in concert with this method and the
   * mouseEntered method below).
   */
  public void mouseExited( MouseEvent me )
  {
    if( isPressed() )
    {
      setPressed( false );
      wasPressed = true;
      repaint();
    }
  }

  public void mouseEntered( MouseEvent me )
  {
    if( wasPressed )
    {
      setPressed( true );
      wasPressed = false;
      repaint();
    }
  }

  public void addActionListener( ActionListener listener )
  {
    myListeners.addElement( listener );
  }

  public void removeActionListener( ActionListener listener )
  {
    myListeners.removeElement( listener );
  }

  public Dimension getPreferredSize() { return getSize(); }
  public Dimension getMinimumSize() { return getPreferredSize(); }

  private Enumeration getListeners() { return myListeners.elements(); }

  protected Image getImage() { return myImage; }
  protected void setImage( Image image ) { myImage = image; }

  public void setEnabled( boolean enable )
  {
    // Don't enable or disable an already enabled or disabled button.
    //
    if( enable == isEnabled() )
      return;

    super.setEnabled( enable );
    repaint();
  }

  private Image getDisabledImage() { return myDisabledImage; }
  protected void setDisabledImage( Image image ) { myDisabledImage = image; }

  protected boolean isPressed() { return amPressed; }
  private void setPressed( boolean b ) { amPressed = b; }

  private String getCommand() { return myCommand; }
  private void setCommand( String command ) { myCommand = command; }
}

