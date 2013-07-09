package com.barrybecker4.java2d.examples;

import com.barrybecker4.ui.application.ApplicationFrame;

import java.awt.*;
import java.awt.geom.AffineTransform;

/**
 * Derived from code accompanying "Java 2D Graphics" by Jonathan Knudsen.
 */
public class TextRendering {

    private TextRendering() {}

    public static void main( String[] args ) {
        Frame frame = new ApplicationFrame( "TextRendering v1.0" ) {

            @Override
            public void paint( Graphics g ) {
                super.paint(g);
                Graphics2D g2 = (Graphics2D) g;
                g2.setRenderingHint( RenderingHints.KEY_ANTIALIASING,
                        RenderingHints.VALUE_ANTIALIAS_ON );

                // Transform the origin to the bottom center of the window.
                Dimension d = getSize();
                AffineTransform ct = AffineTransform.getTranslateInstance(
                        d.width / 2, d.height * 3 / 4 );
                g2.transform( ct );

                // Get an appropriate font.
                String s = "jade";
                Font f = new Font( "Serif", Font.PLAIN, 128 );
                g2.setFont( f );

                int limit = 6;
                for ( int i = 1; i <= limit; i++ ) {
                    // Save the original transformation.
                    AffineTransform oldTransform = g2.getTransform();

                    float ratio = (float) i / (float) limit;
                    g2.transform( AffineTransform.getRotateInstance(
                            Math.PI * (ratio - 1.0f) ) );
                    float alpha = ((i == limit) ? 1.0f : ratio / 3);
                    g2.setComposite( AlphaComposite.getInstance(
                            AlphaComposite.SRC_OVER, alpha ) );
                    g2.drawString( s, 0, 0 );

                    // Restore the original transformation.
                    g2.setTransform( oldTransform );
                }
            }
        };
        frame.setVisible( true );
    }
}