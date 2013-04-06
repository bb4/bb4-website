package com.barrybecker4.java2d.examples;

import com.barrybecker4.java2d.Utilities;
import com.barrybecker4.ui.application.ApplicationFrame;

import java.awt.*;
import java.awt.geom.Rectangle2D;
import java.awt.geom.RoundRectangle2D;
import java.awt.image.BufferedImage;
import java.io.IOException;

/**
 * Derived from code accompanying "Java 2D Graphics" by Jonathan Knudsen.
 */
public class TexturePaintFill extends ApplicationFrame {

    public static void main( String[] args ) throws Exception {
        TexturePaintFill f = new TexturePaintFill( "roa2.jpg" );
        f.setTitle( "TexturePaintFill v1.0" );
        f.setSize( 200, 200 );
        f.center();
        f.setVisible( true );
    }

    private BufferedImage mImage;

    public TexturePaintFill( String filename ) throws IOException  {
        Image img = Utilities.blockingLoad( Utilities.DEFAULT_IMAGE_DIR + filename );
        mImage = Utilities.makeBufferedImage( img );
    }

    @Override
    public void paint( Graphics g ) {

        super.paint(g);
        Graphics2D g2 = (Graphics2D) g;

        RoundRectangle2D r = new RoundRectangle2D.Float(25, 35, 150, 150, 55, 55 );

        if (mImage == null) {
            System.out.println("mImage=" + mImage);
            return;
        }

        // Create a texture rectangle with the same size as the texture image.
        Rectangle2D tr = new Rectangle2D.Double( 0, 0,
                mImage.getWidth(), mImage.getHeight() );

        TexturePaint tp = new TexturePaint(mImage, tr);
        g2.setPaint( tp );

        g2.fill( r );
    }
}