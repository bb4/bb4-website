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

import java.awt.*;
import java.awt.image.*;

/**
 * A Panel with a border (SOLID, RAISED, LOWERED, IN or OUT)
 */
public class BorderPanel extends Panel
{
  public static final int SOLID   = 0;
  public static final int RAISED  = 1;
  public static final int LOWERED = 2;
  public static final int IN      = 3;
  public static final int OUT     = 4;

  private static final int DEFAULT_TYPE = RAISED;
  private static final int DEFAULT_THICKNESS = 2;

  private int type = DEFAULT_TYPE;
  private int thickness = DEFAULT_THICKNESS;

  protected Insets myInsets = null;

  /**
   * Constructor.
   * @param type The border type (SOLID, RAISED, LOWERED, IN or OUT)
   * @param thickness The border thickness.
   */
  public BorderPanel( int type, int thickness )
  {
    this.type = type;
    this.thickness = thickness;
    build();
  }

  /**
   * Constructor.
   * @param type The border type (SOLID, RAISED, LOWERED, IN or OUT)
   */
  public BorderPanel( int type )
  {
    this( type, DEFAULT_THICKNESS );
  }

  public BorderPanel()
  {
  }

  private void build()
  {
    myInsets = new Insets( thickness, thickness, thickness, thickness );
  }

  public void paint( Graphics g )
  {
    update( g );
  }

  /**
   * Paint the border (if any), and then, paint the components.
   * @param graphics the specified Graphics window
   */
  public void update( Graphics graphics )
  {
    if( thickness > 0 )
    {
      // in some case getSize() doesn't return the right size.
      //
      Dimension size = this.getPreferredSize();
      graphics.setColor( getBackground() );

      switch ( type ) 
      {
        case SOLID:
          graphics.setColor( getForeground() );

          for ( int i = 0; i < thickness; i++ )
            graphics.drawRect( i, i, size.width - i * 2 - 1,
                               size.height - i * 2 - 1 );

          break;

        case RAISED:
          for ( int i = 0; i < thickness; i++ )
            graphics.draw3DRect( i, i, size.width - i * 2 - 1,
                                 size.height - i * 2 - 1, true );

          break;

        case LOWERED:
          for ( int i = 0; i < thickness; i++ )
            graphics.draw3DRect( i, i, size.width - i * 2 - 1,
                                 size.height - i * 2 - 1, false );

          break;

        case IN:
          graphics.draw3DRect( 0, 0, size.width - 1, size.height - 1, false );
          graphics.draw3DRect( thickness - 1, thickness - 1,
                               size.width - thickness * 2 + 1,
                               size.height - thickness * 2 + 1, true );
          break;

        case OUT:
          graphics.draw3DRect( 0, 0, size.width - 1, size.height - 1, true );
          graphics.draw3DRect( thickness - 1, thickness - 1,
                               size.width - thickness * 2 + 1, 
                               size.height - thickness * 2 + 1, false );

          break;
      }
    }
  }

  public Insets getInsets() { return myInsets; }
  public void setInsets( Insets i ) { myInsets = i; }
}

