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

package ca.dj.awt.util;

import java.awt.Frame;

import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;

/**
 * A frame which is aware when the user requests to close it.
 */
public class ClosableFrame extends Frame implements WindowListener
{
  /**
   * A frame with no title, that can be closed.
   */
  public ClosableFrame()
  {
    this( "Untitled" );
  }

  /**
   * A frame with a given title, that can be closed.
   *
   * @param title - The title for this frame.
   */
  public ClosableFrame( String title )
  {
    super( title );
    addWindowListener( this );
  }

  /**
   * The user has requested that the window be closed, so we oblige.
   *
   * @param we - The WindowEvent telling us lots of superfluous details.
   */
  public void windowClosing( WindowEvent we )
  {
    setVisible( false );
    dispose();
  }

  public void windowClosed( WindowEvent we ) { }
  public void windowActivated( WindowEvent we ) { }
  public void windowDeactivated( WindowEvent we ) { }
  public void windowDeiconified( WindowEvent we ) { }
  public void windowIconified( WindowEvent we ) { }
  public void windowOpened( WindowEvent we ) { }
}

