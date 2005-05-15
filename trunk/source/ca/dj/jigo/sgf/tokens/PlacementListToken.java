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

package ca.dj.jigo.sgf.tokens;

import ca.dj.jigo.sgf.Point;

import java.io.StreamTokenizer;
import java.io.IOException;

import java.util.Enumeration;
import java.util.Vector;

/**
 * A list of points.
 */
public class PlacementListToken extends PlacementToken implements MarkupToken
{
  private Vector myPoints = new Vector();

  public PlacementListToken() { }

  protected boolean parseContent( StreamTokenizer st )
    throws IOException
  {
    do
    {
      // Read a point in the list of points (of which there must be at least
      // one), then add it to our internal list of points.
      //
      if ( parsePoint( st ) )    {
        addPoint( getPoint().copy() );
      }
    }
    while( st.nextToken() == (int)'[' );

    st.pushBack();

    return true;
  }

  private void addPoint( Point point ) { myPoints.addElement( point ); }
  public Vector getPoints() { return myPoints; }
}

