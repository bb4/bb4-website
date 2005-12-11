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

import java.awt.Color;

/**
 * Handles parsing of hexadecimal colour codes in the format
 * #RRGGBB where RR is red content from 0x00 - 0xFF, GG is amount of green,
 * and BB is the amount of blue.  Color.black is the default colour, if the
 * given hexademical colour couldn't be parsed (for whatever reason).
 */
public final class Colour
{
  /**
   * Returns the object representation of the colour defined by a string
   * in the format of #RRGGBB.  For example: #FF7711.
   * <P>
   * By default, this will return Color.black.
   */
  public static Color parse( String colour )
  {
    try
    {
      return new Color(
        Integer.valueOf( colour.substring( 1, 3 ), 16 ).intValue(),
        Integer.valueOf( colour.substring( 3, 5 ), 16 ).intValue(),
        Integer.valueOf( colour.substring( 5, 7 ), 16 ).intValue() );
    }
    catch( Exception e ) { }

    return Color.black;
  }
}

