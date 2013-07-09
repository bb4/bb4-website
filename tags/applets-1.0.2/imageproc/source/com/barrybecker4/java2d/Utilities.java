package com.barrybecker4.java2d;

import com.barrybecker4.common.util.FileUtil;

import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.image.BufferedImage;
import java.net.URL;

public class Utilities {

    private Utilities() {}

    private static final Component sComponent = new Component() {};

    private static final MediaTracker sTracker = new MediaTracker( sComponent );
    private static int sID = 0;

    public static final String DEFAULT_IMAGE_DIR =
            FileUtil.PROJECT_HOME + "main/source/com/barrybecker4/java2d/images/";

    /**
     * @param image image to load
     * @return true when the image has been loaded.
     */
    public static boolean waitForImage( Image image ) {
        int id;
        synchronized (sComponent) {
            id = sID++;
        }
        sTracker.addImage( image, id );
        try {
            sTracker.waitForID( id );
        } catch (InterruptedException ie) {
            return false;
        }
        return !sTracker.isErrorID(id);
    }

    public static Image blockingLoad( String path ) {
        Image image = Toolkit.getDefaultToolkit().getImage( path );
        if ( !waitForImage(image) ) return null;
        return image;
    }

    public static Image blockingLoad( URL url ) {
        Image image = Toolkit.getDefaultToolkit().getImage( url );
        if ( !waitForImage(image) ) return null;
        return image;
    }

    public static BufferedImage makeBufferedImage( Image image ) {
        if (image == null)  {
            System.out.println( "Warning image is null" );
            return null;
        }

        return makeBufferedImage( image, BufferedImage.TYPE_INT_ARGB );
    }

    public static BufferedImage makeBufferedImage( Image image, int imageType ) {
        if ( !waitForImage(image) ) return null;

        BufferedImage bufferedImage = new BufferedImage(
                image.getWidth( null ), image.getHeight( null ),
                imageType );
        Graphics2D g2 = bufferedImage.createGraphics();
        g2.drawImage( image, null, null );
        return bufferedImage;
    }

    public static BufferedImage getBufferedImage(String path) {
        Image image = Utilities.blockingLoad( path );
        return  Utilities.makeBufferedImage( image );
    }


    public static Frame getNonClearingFrame( String name, Component c ) {
        final Frame f = new Frame( name ) {
            @Override
            public void update( Graphics g ) {
                paint( g );
            }
        };
        sizeContainerToComponent( f, c );
        centerFrame( f );
        f.setLayout( new BorderLayout() );
        f.add( c, BorderLayout.CENTER );
        f.addWindowListener( new WindowAdapter() {
            @Override
            public void windowClosing( WindowEvent e ) {
                f.dispose();
            }
        } );
        return f;
    }

    public static void sizeContainerToComponent( Container container,
                                                 Component component ) {
        if ( !container.isDisplayable() ) container.addNotify();
        Insets insets = container.getInsets();
        Dimension size = component.getPreferredSize();
        int width = insets.left + insets.right + size.width;
        int height = insets.top + insets.bottom + size.height;
        container.setSize( width, height );
    }

    public static void centerFrame( Frame f ) {
        Dimension screen = Toolkit.getDefaultToolkit().getScreenSize();
        Dimension d = f.getSize();
        int x = (screen.width - d.width) >> 1;
        int y = (screen.height - d.height) >> 1;
        f.setLocation( x, y );
    }
}