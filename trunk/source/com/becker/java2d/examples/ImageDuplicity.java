package com.becker.java2d.examples;

import com.becker.ui.ApplicationFrame;
import com.sun.image.codec.jpeg.JPEGCodec;
import com.sun.image.codec.jpeg.JPEGImageDecoder;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.InputStream;

public class ImageDuplicity
        extends Component
{
    public static void main( String[] args )
    {
        ApplicationFrame f = new ApplicationFrame( "ImageDuplicity v1.0" );
        f.setLayout( new BorderLayout() );
        Component c = new ImageDuplicity();
        f.add( c, BorderLayout.CENTER );
        f.setSize( 200, 250 );
        f.center();
        f.setVisible( true );
    }

    private BufferedImage mImage;

    @Override
    public void paint( Graphics g )
    {
        Graphics2D g2 = (Graphics2D) g;
        // If the offscreen image is not defined, create it.
        if ( mImage == null ) createOffscreenImage();
        // Render the offscreen image.
        g2.drawImage( mImage, 0, 0, this );
    }

    private void createOffscreenImage()
    {
        // Create a BufferedImage the same size as this component.
        Dimension d = getSize();
        int w = d.width, h = d.height;
        mImage = new BufferedImage( w, h, BufferedImage.TYPE_INT_RGB );
        // Obtain the Graphics2D for the offscreen image.
        Graphics2D g2 = mImage.createGraphics();
        g2.setRenderingHint( RenderingHints.KEY_ANTIALIASING,
                RenderingHints.VALUE_ANTIALIAS_ON );
        // Load an image from a file.
        try {
            String filename = "Raphael.jpg";
            InputStream in = getClass().getResourceAsStream( filename );
            JPEGImageDecoder decoder = JPEGCodec.createJPEGDecoder( in );
            BufferedImage image = decoder.decodeAsBufferedImage();
            in.close();
            // Draw the loaded image on the offscreen image.
            g2.drawImage( image, 0, 0, w, h, null );
        } catch (Exception e) {
            System.out.print( e );
        }
        // Draw some concentric ellipses.
        g2.setStroke( new BasicStroke( 2 ) );
        Color[] colors = {Color.red, Color.blue, Color.green};
        for ( int i = -32; i < 40; i += 8 ) {
            g2.setPaint( colors[Math.abs( i ) % 3] );
            g2.drawOval( i, i, w - i * 2, h - i * 2 );
        }
    }
}