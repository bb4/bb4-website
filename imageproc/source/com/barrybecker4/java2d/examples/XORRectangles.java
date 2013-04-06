package com.barrybecker4.java2d.examples;

import com.barrybecker4.ui.application.ApplicationFrame;

import java.awt.*;
import java.awt.geom.AffineTransform;
import java.awt.geom.Rectangle2D;

/**
 * Derived from code accompanying "Java 2D Graphics" by Jonathan Knudsen.
 */
public class XORRectangles {

    private XORRectangles() {}

    public static void main( String[] args ) {
        ApplicationFrame f = new ApplicationFrame( "XORRectangles v1.0" ) {

            @Override
            public void paint( Graphics g ) {
                Graphics2D g2 = (Graphics2D) g;

                // Set XOR mode, using white as the XOR color.
                g2.setXORMode( Color.white );
                // Paint a red rectangle.
                Rectangle2D r = new Rectangle2D.Double( 50, 50, 150, 100 );
                g2.setPaint( Color.red );
                g2.fill( r );
                // Shift the coordinate space.
                g2.transform( AffineTransform.getTranslateInstance( 25, 25 ) );
                // Draw a blue rectangle.
                g2.setPaint( Color.blue );
                g2.fill( r );
            }
        };
        f.setSize( 300, 200 );
        f.center();
        f.setVisible( true );
        f.repaint();
    }
}