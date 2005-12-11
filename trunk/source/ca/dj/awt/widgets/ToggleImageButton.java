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

import java.awt.Image;

import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

/**
 * The ImageButton class creates and image button which performs mouse
 * overs and handles its own mouse events.
 */
public class ToggleImageButton extends ImageButton
{
  private Image myToggleImage = null;

  private boolean amToggled = false;

  /**
   * Constructs a toggle image button with specified images.  By default,
   * the button is in the OFF state.
   *
   * @param onToggle - When the button is toggled, this image is displayed.
   * @param offToggle - Opposite of onToggle (normal image).
   * @param command - The name of the command associated with this button.
   */
  public ToggleImageButton( Image onToggle, Image offToggle, String command )
  {
    super( offToggle, command );
    setToggleImage( onToggle );
  }

  /**
   * The mouse has clicked the button.  Swap the images so that the button
   * appears to toggle between two states.
   */
  public void mouseClicked( MouseEvent me )
  {
    swapImages();
    super.mouseClicked( me );
  }

  /**
   * Asks the toggle button to swap the two toggling images.
   */
  private void swapImages()
  {
    Image image = getImage();
    setImage( getToggleImage() );
    setToggleImage( image );
    amToggled = !amToggled;
  }

  /**
   * Asks the button to change state, then update its view.
   */
  private void toggle()
  {
    swapImages();
    repaint();
  }

  /**
   * Indicates if this ToggleButton is in the "on" state, or not.
   *
   * @return true - The ImageButton is toggled on.
   */
  public boolean isToggledOn()
  {
    return amToggled;
  }

  /**
   * Toggles the image into the off position (normal image).  If it is already
   * toggled off, this method does nothing.
   */
  public void toggleOff()
  {
    if( amToggled )
      toggle();
  }

  /**
   * Toggles the image into the on position (active image).  If it is already
   * toggled on, this method does nothing.
   */
  public void toggleOn()
  {
    if( !amToggled )
      toggle();
  }

  private Image getToggleImage() { return myToggleImage; }
  private void setToggleImage( Image image ) { myToggleImage = image; }
}

