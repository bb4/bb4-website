package com.barrybecker4.java2d.ui;

import com.barrybecker4.ui.application.ApplicationFrame;

import java.awt.*;
import java.awt.image.ImageObserver;
import java.net.URL;

/**
 * Shows an image in a frame of the same size.
 */
public class RightSizer extends ApplicationFrame {

    private Image image;

    public static void main( String[] args ) throws Exception {
        String url = "http://barrybecker4.com/familyPictures/family_portrait_s.JPG";
        if ( args.length > 0 ) {
            url = args[0];
        }

        new RightSizer( new URL( url ) );
    }

    public RightSizer( URL url ) {
        super( "RightSizer v1.0" );
        image = Toolkit.getDefaultToolkit().getImage( url );
        rightSize();
    }

    /**
     * Set the frame size to the same size as the image (once it has loaded).
     */
    private void rightSize() {
        int width = image.getWidth( this );
        int height = image.getHeight( this );
        if ( width == -1 || height == -1 ) return;
        addNotify();
        Insets insets = getInsets();
        setSize( width + insets.left + insets.right,
                height + insets.top + insets.bottom );
        center();
        setVisible( true );
    }

    @Override
    public boolean imageUpdate(Image img, int infoflags,
                               int x, int y, int width, int height) {
        if ( (infoflags & ImageObserver.ERROR) != 0 ) {
            System.out.println( "Error loading image!" );
            System.exit( -1 );
        }
        if ( (infoflags & ImageObserver.WIDTH) != 0 &&
                (infoflags & ImageObserver.HEIGHT) != 0 )
            rightSize();
        if ( (infoflags & ImageObserver.SOMEBITS) != 0 )
            repaint();
        if ( (infoflags & ImageObserver.ALLBITS) != 0 ) {
            rightSize();
            repaint();
            return false;
        }
        return true;
    }

    @Override
    public void update( Graphics g ) {
        paint( g );
    }

    @Override
    public void paint( Graphics g ) {
        Insets insets = getInsets();
        g.drawImage(image, insets.left, insets.top, this );
    }
}