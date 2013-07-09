package com.barrybecker4.java2d.examples;

import com.barrybecker4.ui.application.ApplicationFrame;

import java.awt.*;
import java.awt.font.FontRenderContext;
import java.awt.font.GlyphVector;
import java.awt.geom.AffineTransform;
import java.awt.geom.Point2D;

/**
 * Derived from code accompanying "Java 2D Graphics" by Jonathan Knudsen.
 */
public class RollingText {

    private static final String TEXT = "This an example of some pleasant rolling text...";
    private static final Font FONT = new Font( "Serif", Font.PLAIN, 24 );


    public static void main( String[] args ) {
        Frame f = new RollingTextFrame("RollingText" );
        f.setSize(new Dimension(920, 450));
        f.setVisible( true );
    }

    private RollingText() {}

    private static class RollingTextFrame extends ApplicationFrame {

        public RollingTextFrame(String title) {
            super(title);
        }

        @Override
        public void paint( Graphics g ) {
            super.paint(g);
            Graphics2D g2 = (Graphics2D) g;

            g2.setRenderingHint( RenderingHints.KEY_ANTIALIASING,
                    RenderingHints.VALUE_ANTIALIAS_ON );

            FontRenderContext frc = g2.getFontRenderContext();
            g2.translate( 40, 80 );

            GlyphVector gv = FONT.createGlyphVector( frc, TEXT );

            drawText(g2, gv);
        }

        private void drawText(Graphics2D g2, GlyphVector gv) {
            int numLetters = gv.getNumGlyphs();

            for ( int i = 0; i < numLetters; i++ ) {
                Shape transformedGlyph = createTransformedGlyph(gv, numLetters, i);
                g2.fill( transformedGlyph );
            }
        }

        private Shape createTransformedGlyph(GlyphVector gv, int length, int i) {
            Point2D p = gv.getGlyphPosition( i );
            double theta = (double) i / (double) (length - 1) * Math.PI / 4;
            AffineTransform at =
                    AffineTransform.getTranslateInstance(p.getX(), p.getY() );
            at.rotate( theta );
            Shape glyph = gv.getGlyphOutline( i );
            return at.createTransformedShape( glyph );
        }


    }
}