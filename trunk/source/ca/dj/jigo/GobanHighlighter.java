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

import java.awt.event.MouseEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseMotionListener;

/**
 * Used to ask a Goban to highlight the intersection over which the mouse
 * is hovering.
 */
public class GobanHighlighter extends MouseAdapter
  implements MouseMotionListener
{
  private Goban myGoban;
  private Location myHighlight = new Location();

  /**
   * Creates a new GobanHighlighter, provided a Goban to highlight.  The
   * Goban must not be null.
   *
   * @param goban - The Goban to highlight on mouse moves.
   */
  public GobanHighlighter( Goban goban )
  {
    myGoban = goban;

    myGoban.addMouseListener( this );
    myGoban.addMouseMotionListener( this );
  }

  /**
   * Simply calls mouseMoved to simulate that the mouse has been moved.
   */
  public void mouseDragged( MouseEvent me ) { mouseMoved( me ); }

  /**
   * Simply calls mouseMoved to simulate that the mouse has been moved,
   * which will adjust the highlight to the proper position.
   */
  public void mouseEntered( MouseEvent me ) { mouseMoved( me ); }

  /**
   * Removes the highlighting.
   */
  public void mouseExited( MouseEvent me ) { myGoban.clearHighlight(); }

  /**
   * Removes the highlighting if the Goban has a stone where the mouse
   * was clicked.  Otherwise it adds highlighting.
   */
  public void mouseClicked( MouseEvent me )
  {
    myGoban.translateCoord( me.getX(), me.getY(), myHighlight );

    if( myGoban.hasStone( myHighlight ) )
      myGoban.clearHighlight();
    else
      myGoban.setHighlight( myHighlight );
  }

  /**
   * Called when the mouse has changed its position.  If the mouse has moved
   * to a spot that is already highlighted, nothing happens.  Otherwise,
   * the old highlighted spot is removed and the Goban is told to highlight
   * a new location (directly translated from the given MouseEvent's
   * coordinates).
   * <P>
   * Doesn't highlight a spot if a stone is there.
   */
  public void mouseMoved( MouseEvent me )
  {
    myGoban.translateCoord( me.getX(), me.getY(), myHighlight );

    // If the position has a stone, don't draw any highlight and remove the
    // highlight if present.  Otherwise, if the highlighted spot is already
    // highlighted, then don't do anything.  Finally, set the highlight if
    // nothing is highlighted and no stone is underneath.
    //
    if( myGoban.hasStone( myHighlight ) )
    {
      if( myGoban.isHighlighted() )
        myGoban.clearHighlight();
    }
    else if( !myGoban.isHighlighted( myHighlight ) )
      myGoban.setHighlight( myHighlight );
  }
}

