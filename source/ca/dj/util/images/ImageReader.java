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

package ca.dj.util.images;

import java.awt.Component;
import java.awt.Image;
import java.awt.MediaTracker;
import java.awt.Toolkit;

import java.net.URL;

/**
 * A class that allows java.awt.Image instances to be created given a web
 * address.  This requires server-side software be set up that understands
 * the file naming scheme used by the XML documents (and hence the Catalog
 * applet).
 * <P>
 * After ImageReader has been instantiated once, any number of calls can
 * be made to the static loadImage methods.
 */
public final class ImageReader
{
  // Initialized upon creation of a new ImageReader.
  //
  private static MediaTracker TRACKER = null;

  public ImageReader( Component parent )
  {
    // The MediaTracker must have a valid parent.
    //
    TRACKER = new MediaTracker( parent );
  }

  public final static Image get( String urlAddress )
  {
    try
    {
      return get( new URL( urlAddress ) );
    }
    catch( Exception e ) { 
        e.printStackTrace();
    }

    return null;
  }

  public final static Image get( URL address )
  {
    if( address == null )
      return null;

    try
    {
      //System.out.println("ImageReader: getting image for "+address);
      Image image = Toolkit.getDefaultToolkit().getImage( address );
      
      TRACKER.addImage( image, 0 );
      TRACKER.waitForID( 0 );
      return TRACKER.statusID( 0, true ) != TRACKER.ERRORED ? image : null;
    }
    catch( Exception e ) { 
        e.printStackTrace();
    }

    return null;
  }
}

