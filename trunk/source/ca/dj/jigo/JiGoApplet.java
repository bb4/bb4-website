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

//import java.applet.Applet;
import javax.swing.*;
import java.applet.*;
import java.awt.*;
import java.awt.event.*;
import java.io.IOException;
import java.net.URL;
import ca.dj.awt.util.Colour;
import ca.dj.util.images.ImageReader;

/**
 * Most applets using the JiGo API will want to extend this class.  The
 * responsibilities for this class include loading up the basic images
 * (white stone, black stone, board background) and settings (background
 * colour, default stone width/height, and such).
 *
 * <P>
 * Subclasses need only override the "start" and "stop" methods (the latter
 * being optional).
 */
public class JiGoApplet extends Applet
{
  /** The applet's default background colour. */
  private final static String DEFAULT_BG = "#FFFFFF";

  /** Used for reading images from a URL. */
  private ImageReader myImageReader = new ImageReader( this );

  private BlackStone myBlackStone;
  private WhiteStone myWhiteStone;
  private Image myBGImage;

  private int myBoardSize;
  
  // if true then running as an application
  boolean isStandalone = false;


  /**
   * Empty, public, constructor so that subclasses can exist.
   */
  public JiGoApplet() { }

  /**
   * Called by the browser to initialize the applet.  This loads up all
   * images and relevant settings.
   */
  public void init()
  {
    int stoneWidth = getParam( "STONE WIDTH", Stone.DEFAULT_WIDTH ),
        stoneHeight = getParam( "STONE HEIGHT", Stone.DEFAULT_HEIGHT );

    setBoardSize( getParam( "BOARD SIZE", Goban.DEFAULT_SIZE ) );

    Image image = loadImageParameter( "WHITE STONE" );

    // If the white stone is null, then create a regular stone of the
    // specified width and height.
    //
    setWhiteStone( (image == null) ?
                   new WhiteStone( stoneWidth, stoneHeight ) :
                   new WhiteStone( image ) );

    image = loadImageParameter( "BLACK STONE" );

    // If the white stone is null, then create a regular stone of the
    // specified width and height.
    //
    setBlackStone( (image == null) ?
                   new BlackStone( stoneWidth, stoneHeight ) :
                   new BlackStone( image ) );

    // The background image used when drawing the Goban (i.e., wood grain).
    //
    setBGImage( loadImageParameter( "BOARD IMAGE" ) );

    // Automatically change the background colour of the applet to match
    // the colour given as the "APPLET COLOUR" param tag (or the default
    // background colour).
    //
    setBackground( Colour.parse( getParam( "APPLET COLOUR", DEFAULT_BG ) ) );
  }

  /**
   * Given the name of a file, this method attempts to load it as an image.
   * <P>
   * This supports only GIF and JPG images.
   *
   * @param imageParameter - The relative pathname of an image file.
   */
  public Image loadImage( String fileName )
  {
    URL url = file2url( fileName );
    //System.out.println("about to load the image "+url);
    return getImageReader().get( url );
  }

  /**
   * Given the name of an applet PARAM tag, this method attempts to load
   * its associated value as an image.  This means, of course, that the
   * parameter's value should point to an image that resides on the same
   * server from whence the applet was downloaded.
   * <P>
   * This supports only GIF and JPG images.
   *
   * @param imageParameter - The relative pathname of an image file.
   */
  public Image loadImageParameter( String imageParameter )
  {
    return loadImage( getParam( imageParameter, "" ) );
  }

  /**
   * Helper method for determining the integer value of a parameter.  If the
   * specified parameter doesn't exist (or isn't a number), then the given
   * default value is returned.  Otherwise, the integer value of the given
   * parameter is returned.
   *
   * @param paramName - A parameter from the applet's PARAM tag.
   * @param defaultValue - Returned if the parameter was invalid.
   */
  public int getParam( String paramName, int defaultValue )
  {
    try
    {
      String par = getParameter( paramName );
      if (par == null)
          return defaultValue;
      return Integer.parseInt( par );
    }
    catch( Exception e ) { 
        e.printStackTrace();
    }

    return defaultValue;
  }

  /**
   * Helper method for determining the String value of a parameter.  If the
   * specified parameter doesn't exist, then the given default value is
   * returned; otherwise, the String value of the given parameter comes back.
   *
   * @param paramName - A parameter from the applet's PARAM tag.
   * @param defaultValue - Returned if the parameter was invalid.
   */
  public String getParam( String paramName, String defaultValue )
  {
    try
    {
      System.out.println("param value for "+paramName+" = "+getParameter( paramName ));
      return getParameter( paramName );
    }
    catch( Exception e ) { 
        e.printStackTrace();
    }

    return defaultValue;
  }

  /**
   * Converts a filename into a fully qualified URL.  The URL is based on
   * this applet's code base, since the applet may only communicate with
   * the server from whence it came (thus document base would foil the code).
   * The fileName is specified by relative path to where the applet's class
   * files can be found.
   *
   * @param fileName - The name of the file to convert into a URL.
   */
  private URL file2url( String fileName )
  {
    try
    {
      System.out.println("attempting to read image:"+getCodeBase() + fileName );
      return new URL( getCodeBase() + fileName );
    }
    catch( Exception e ) {
        e.printStackTrace(); 
    }

    return null;
  }

  /**
   * The number of gridlines for the Goban that will be created.
   *
   * @return Usually an integer in the set { 9, 13, 19 }.
   */
  public int getBoardSize() { return myBoardSize; }
  private void setBoardSize( int size ) { myBoardSize = size; }

  private ImageReader getImageReader() { return myImageReader; }
  private void setImageReader( ImageReader ir ) { myImageReader = ir; }

  /**
   * The White stone used by the Goban when displaying its stones.
   *
   * @return An instance of WhiteStone.
   */
  public WhiteStone getWhiteStone() { return myWhiteStone; }
  private void setWhiteStone( WhiteStone ws ) { myWhiteStone = ws; }

  /**
   * The Black stone used by the Goban when displaying its stones.
   *
   * @return An instance of BlackStone.
   */
  public BlackStone getBlackStone() { return myBlackStone; }
  private void setBlackStone( BlackStone bs ) { myBlackStone = bs; }

  /**
   * The background image used by the Goban for its wood grain.  This is
   * either a GIF JPEG image, which should be tilable.  This method can
   * return null if no image is specified.
   *
   * @return An image to be drawn for a Goban's background.
   */
  public Image getBGImage() { return myBGImage; }
  private void setBGImage( Image i ) { myBGImage = i; }
  
  /*
  protected static void showApplet(JiGoApplet applet, String title)
  {
      try  {
        UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
      }
      catch(Exception e) {}

      applet.isStandalone = false;
      JFrame frame = new JFrame();

      frame.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
      frame.addWindowListener(new WindowAdapter() {
          public void windowClosed(WindowEvent e) {
              System.exit(0);
          }
      });
      frame.setTitle(title);
      applet.init();
      frame.setContentPane(applet.getContentPane());

      frame.setSize(applet.getSize());

      Dimension d = Toolkit.getDefaultToolkit().getScreenSize();
      frame.setLocation((d.width - frame.getSize().width) / 2, (d.height - frame.getSize().height) / 2);
      frame.setVisible(true);
  }

  
  //------ Main method --------------------------------------------------------
  public static void main(String[] args) {

        JiGoApplet applet = new JiGoApplet();
        showApplet(applet, "JiGo Applet");
  }
  */
}

