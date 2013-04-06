package com.barrybecker4.java2d.examples;

import com.barrybecker4.java2d.Utilities;
import com.barrybecker4.ui.application.ApplicationFrame;

import java.awt.*;
import java.awt.font.FontRenderContext;
import java.awt.font.GlyphVector;
import java.awt.image.BufferedImage;
import java.io.IOException;

/**
 * Derived from code accompanying "Java 2D Graphics" by Jonathan Knudsen.
 */
public class ClipImage extends ApplicationFrame {
    private Shape mClippingShape;

    final BufferedImage image;

    public ClipImage(String title) throws IOException {
        super(title);

        String filename = Utilities.DEFAULT_IMAGE_DIR + "roa2.jpg";

        Image img = Utilities.blockingLoad( filename );
        image = Utilities.makeBufferedImage( img );
    }

    @Override
    public void paint( Graphics g ) {
        super.paint(g);
        Graphics2D g2 = (Graphics2D) g;
        Dimension d = getSize();
        g2.rotate( -Math.PI / 12, d.width / 2, d.height / 2 );
        g2.scale(3.0, 3.0);
        g2.clip( getClippingShape( g2 ) );
        g2.drawImage( image, 0, 0, null );
    }

    private Shape getClippingShape( Graphics2D g2 ) {
        if ( mClippingShape != null ) return mClippingShape;
        String s = "bella";
        Font font = new Font( "Serif", Font.PLAIN, 32 );
        FontRenderContext frc = g2.getFontRenderContext();
        GlyphVector gv = font.createGlyphVector( frc, s );
        mClippingShape = gv.getOutline( 10, 40 );
        return mClippingShape;
    }

    public static void main( String[] args ) throws Exception {

        ClipImage frame = new ClipImage( "ClipImage v1.0" );
        frame.setSize( 400, 250 );
    }
}