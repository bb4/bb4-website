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

import java.awt.List;

/**
 * This subclass of java.awt.List adds automagic ascending sorting.
 * The choice between ascending and descending would be an ideal feature.
 */
public final class OrderedList extends List
{
  /**
   * Creates a new OrderedList with the understanding that it will display
   * the given number of elements.
   *
   * @param size - The number of elements in the display area.
   */
  public OrderedList( int size )
  {
    super( size );
  }

  /**
   * Creates a new OrderedList with the understanding that it will display
   * the given number of elements, and indicate if multiple selections are
   * on.
   *
   * @param size - The number of elements in the display area.
   * @param b - true means multiple selections are on.
   */
  public OrderedList( int size, boolean b )
  {
    super( size, b );
  }

  /**
   * Removes the item from the list, ignoring case sensitivity.
   *
   * @param item - The item to remove from the list.
   */
  public synchronized void removeIgnoreCase( String item )
  {
    int i = getItemCount();

    while( --i > 0 )
      if( getItem( i ).equalsIgnoreCase( item ) )
      {
        remove( i );
        break;
      }
  }

  /**
   * Adds an item to the list in ascending (alphabetical) order.
   *
   * @param item - The item to add to the list.
   */
  public synchronized void add( String item )
  {
    int numItems = getItemCount();

    if( numItems == 0 )
    {
      super.add( item );
      return;
    }

    int index = 0,
        //prevCompare = 0,
        compareValue = 0;

    boolean added = false;

    // No complex stuff here, just find out where it should go, and plonk
    // it in ... For really large lists this will get quite inefficient.
    //
    while( (index < numItems) && !added )
    {
      // Did we find the string to add?  If not, don't add it.
      //
      compareValue =
        item.toLowerCase().compareTo( getItem( index ).toLowerCase() );

      if( compareValue == 0 )
        added = true;
      else if( compareValue < 0 )
      {
        added = true;
        super.add( item, index );
      }

      index++;
    }

    if( !added )
      super.add( item );
  }
}

