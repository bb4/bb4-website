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
import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.Frame;
import java.awt.Panel;

import java.awt.event.ActionListener;

/**
 * A frame for holding a toolbar.  A toolbar consists of a whack of
 * ImageButtons (and/or ToggleImageButtons); just keep adding them and
 * they'll line themselves up.  Eventually this should take an alignment
 * (horizontal vs. vertical) parameter, as well as being able to set the
 * amount of spacing between each button that gets added.
 */
public class Toolbar extends Frame
{
  private final static String DEFAULT_TITLE = "Toolbar";

  ToolbarPanel myPanel = new ToolbarPanel();

  /**
   * Allows subclasses to exist; creates a new (untitled) Toolbar frame.
   */
  public Toolbar()
  {
    this( DEFAULT_TITLE );
  }

  /**
   * Creates a new Toolbar with the title shown along the top of its frame
   * in full glory.
   *
   * @param title - The title to display on the window's border.
   */
  public Toolbar( String title )
  {
    super( title );
    add( myPanel );
    setBackground( Color.lightGray );
    pack();
  }

  /**
   * Adds an ImageButton (or subclass) to this Toolbar.  Any subclass of
   * ImageButton can be used.  Don't pass in null values.
   *
   * @param button - The ImageButton to append to the end of the toolbar.
   */
  public void addButton( ImageButton button )
  {
    myPanel.add( button );
    pack();
  }

  /**
   * Allows other objects in the system to know when any button on the toolbar
   * was activated.
   *
   * @param al - The ActionListener that requires notification of toolbar
   * button presses.
   */
  public void addActionListener( ActionListener al )
  {
    myPanel.addActionListener( al );
  }

  public class ToolbarPanel extends Panel
  {
    public ToolbarPanel()
    {
      // Create a new GridLayout with one row and any number of columns.
      //
      GridLayout gl = new GridLayout( 1, 0 );

      gl.setVgap( 4 );
      gl.setHgap( 4 );

      this.setLayout( gl );
      this.setSize( 0, 0 );
    }

    public Dimension getPreferredSize()
    {
      return this.getMinimumSize();
    }

    public Dimension getMinimumSize()
    {
      return this.getSize();
    }

    public Component add( Component toAdd )
    {
      Component returnValue = super.add( toAdd );

      Dimension addSize = toAdd.getSize(),
                mySize = this.getSize();

      mySize.height = Math.max( mySize.height, addSize.height + 2 );
      mySize.width += (addSize.width + 6);

      this.setSize( mySize );

      return returnValue;
    }

    public void addActionListener( ActionListener al )
    {
      Component components[] = this.getComponents();
  
      for( int i = components.length - 1; i >= 0; i-- )
        if( components[i] instanceof ImageButton )
          ((ImageButton)components[i]).addActionListener( al );
    }
  }
}

