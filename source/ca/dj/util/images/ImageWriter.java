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

import java.applet.Applet;

import java.awt.Image;

import java.io.IOException;

import java.net.Socket;

import ca.dj.util.encoder.PngEncoder;

/**
 * A class that allows java.awt.Image instances to be saved as files on
 * a server by an Applet, provided an Image, filename, and Applet.  The
 * image is saved in PNG format.
 *
 * <P>
 * This should be made a bit more generic, in that it should allow any
 * stream of bytes to be saved to a file on the server.
 *
 * <P>
 * A daemon which understands the protocol must be listening on the server
 * on the default port (4242) for files.  The simple protocol is as follows:
 *   1. Write "filename\n"
 *   2. Write binary file content
 *
 * <P>
 * All this happens in its own thread so the application sees no delay.
 */
public final class ImageWriter implements Runnable
{
  private final static int DEFAULT_PORT = 4242;

  private Image myImage = null;
  private String myFileName = null;
  private int myPort = DEFAULT_PORT;
  private Applet myApplet = null;

  private ImageWriter( Image image, String fileName, int port, Applet applet )
  {
    setImage( image );
    setFileName( fileName + '\n' );
    setPort( port );
    setApplet( applet );
  }

  /**
   * Since the ImageWriter works in its own thread, it needs a run method.
   */
  public void run()
  {
    try
    {
      // Create a new PNG encoder with a compression level of 6.
      //
      PngEncoder encoder = new PngEncoder( getImage(), true, 0, 6 );

      Socket socket = new Socket(
        getApplet().getCodeBase().getHost(), getPort() );

      byte imageBytes[] = encoder.pngEncode();

      if( imageBytes != null )
      {
        socket.getOutputStream().write( getFileName().getBytes() );
        socket.getOutputStream().write( imageBytes );
      }

      socket.close();
    }
    catch( Exception e ) { }
  }

  /**
   * Helper for the other writeImage method.
   */
  public static void writeImage( Image image, String fileName, Applet applet )
  {
    writeImage( image, fileName, DEFAULT_PORT, applet );
  }

  /**
   * Writes a PNG image out to the server daemon, in a separate thread.
   *
   * @param image - The image to write to the server in PNG format.
   * @param applet - The applet responsible for creating the image.
   * @param port - The port the server daemon is listening.
   */
  public static void writeImage(
    Image image, String fileName, int port, Applet applet )
  {
    (new Thread( new ImageWriter( image, fileName, port, applet ) )).start();
  }

  private void setImage( Image image ) { myImage = image; }
  private Image getImage() { return myImage; }

  private void setFileName( String fn ) { myFileName = fn; }
  private String getFileName() { return myFileName; }

  private void setPort( int port ) { myPort = port; }
  private int getPort() { return myPort; }

  private void setApplet( Applet applet ) { myApplet = applet; }
  private Applet getApplet() { return myApplet; }
}

