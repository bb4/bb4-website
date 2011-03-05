package com.becker.java2d.examples;

import com.becker.ui.application.ApplicationFrame;

import java.awt.*;
import java.awt.geom.Rectangle2D;

public class TransparentText
{
    public static void main( String[] args )
    {
        ApplicationFrame f = new ApplicationFrame( "TransparentText v1.0" )
        {
            private int mNumberOfLines = 25;
            private Color[] mColors = {Color.red, Color.green, Color.blue};

            public void paint( Graphics g )
            {
                super.paint(g);
                Graphics2D g2 = (Graphics2D) g;

                // Set the rendering quality.
                g2.setRenderingHint( RenderingHints.KEY_ANTIALIASING,
                        RenderingHints.VALUE_ANTIALIAS_ON );
                // Paint a red rectangle.
                Rectangle2D r = new Rectangle2D.Double( 50, 50, 150, 100 );
                g2.setPaint( Color.red );
                g2.fill( r );
                // Set a composite with transparency.
                Composite c = AlphaComposite.getInstance( AlphaComposite.SRC_OVER,
                        .4f );
                g2.setComposite( c );
                // Draw some blue text.
                g2.setPaint( Color.blue );
                g2.setFont( new Font( "Times New Roman", Font.PLAIN, 72 ) );
                g2.drawString( "Composite", 25, 130 );
            }
        };
        f.setSize( 400, 200 );
        f.center();
        f.setVisible( true );
    }
}