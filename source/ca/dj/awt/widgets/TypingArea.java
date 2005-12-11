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

import java.awt.Event;
import java.awt.TextField;

import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

import java.util.Vector;


/**
 * This class is a TextField with a memory!  It remembers the last 20
 * things the user has typed, and allows them to be scrolled (up/down)
 * using the up and down arrow keys.
 */
public final class TypingArea extends TextField implements KeyListener
{
  private final static int DEFAULT_LENGTH = 20;

  // Number of previous strings to remember.
  //
  private final static int MAX_STRINGS = 20;

  // Used for scrolling through the list of strings.
  //
  private int currElement = 0;

  private Vector strings = new Vector( MAX_STRINGS );

  /**
   * Parameter 'size' represents the number of visible characters shown.
   */
  public TypingArea( int size )
  {
    super( size );
    addKeyListener( this );
  }

  public TypingArea()
  {
    this( DEFAULT_LENGTH );
  }

  private void addString( String s )
  {
    strings.insertElementAt( s, 0 );

    if( strings.size() > MAX_STRINGS )
      strings.setSize( MAX_STRINGS );
  }

  /**
   * The act of setting the text to "" causes the current text to
   * be stored, then reset.
   */
  public void setText( String newText )
  {
    if( newText.equals( "" ) )
    {
      if( !getText().equals( "" ) )
        addString( getText() );

      currElement = 0;
    }

    super.setText( newText );
  }

  public void keyPressed( KeyEvent ke ) { }
  public void keyTyped( KeyEvent ev ) { }

  public void keyReleased( KeyEvent ev )
  {
    // If either up or down are released, change the text accordingly.
    // At the moment, this will cause the user to lose what they've currently
    // typed.
    //
    switch( ev.getKeyCode() )
    {
      // Up means to scroll through the list, starting with the most recent
      // things the user has typed.
      //
      case KeyEvent.VK_UP:
        if( strings.size() == 0 )
          break;

        // If there are no more elements, then index the first one.
        //
        if( ++currElement > strings.size() - 1 )
          currElement = 0;

        // Change the text accordingly, and highlight it.
        //
        setText( (String)(strings.elementAt( currElement )) );
        setCaretPosition( getText().length() );
        break;

      case KeyEvent.VK_DOWN:
        if( strings.size() == 0 )
          break;

        if( --currElement < 0 )
          currElement = strings.size() - 1;

        setText( (String)(strings.elementAt( currElement )) );
        setCaretPosition( getText().length() );
        break;

      // There's a reason why this gets set to -1, and not 0.  Just accept it.
      //
      default:
        currElement = -1;
    }
  }
}

