package com.becker.java2d.examples;

import com.becker.ui.ApplicationFrame;

import java.awt.*;
import java.awt.font.FontRenderContext;
import java.awt.font.LineMetrics;
import java.awt.geom.Line2D;

public class LineMetricsIllustrated
{
    public static void main( String[] args )
    {
        Frame frame = new ApplicationFrame( "LineMetricsIllustrated v1.0" )
        {
            public void paint( Graphics g )
            {
                super.paint(g);
                Graphics2D g2 = (Graphics2D) g;

                g2.setRenderingHint( RenderingHints.KEY_ANTIALIASING,
                        RenderingHints.VALUE_ANTIALIAS_ON );
                Font font = new Font( "Serif", Font.PLAIN, 72 );
                g2.setFont( font );

                String s = "Porphyry";
                float x = 50, y = 150;

                // Draw the baseline.
                FontRenderContext frc = g2.getFontRenderContext();
                float width = (float) font.getStringBounds( s, frc ).getWidth();
                Line2D baseline = new Line2D.Float( x, y, x + width, y );
                g2.setPaint( Color.lightGray );
                g2.draw( baseline );

                // Draw the ascent.
                LineMetrics lm = font.getLineMetrics( s, frc );
                Line2D ascent = new Line2D.Float( x, y - lm.getAscent(),
                        x + width, y - lm.getAscent() );
                g2.draw( ascent );

                // Draw the descent.
                Line2D descent = new Line2D.Float( x, y + lm.getDescent(),
                        x + width, y + lm.getDescent() );
                g2.draw( descent );

                // Draw the leading.
                Line2D leading = new Line2D.Float(
                        x, y + lm.getDescent() + lm.getLeading(),
                        x + width, y + lm.getDescent() + lm.getLeading() );
                g2.draw( leading );

                // Render the string.
                g2.setPaint( Color.black );
                g2.drawString( s, x, y );
            }
        };
        frame.setVisible( true );
    }
}