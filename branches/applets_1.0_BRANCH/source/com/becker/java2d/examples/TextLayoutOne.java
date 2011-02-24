package com.becker.java2d.examples;

import com.becker.ui.ApplicationFrame;

import java.awt.*;
import java.awt.font.TextLayout;

public class TextLayoutOne
{
    public static void main( String[] args )
    {
        Frame f = new ApplicationFrame( "TextLayoutOne v1.0" )
        {
            public void paint( Graphics g )
            {
                super.paint(g);
                Graphics2D g2 = (Graphics2D) g;

                g2.setRenderingHint( RenderingHints.KEY_ANTIALIASING,
                        RenderingHints.VALUE_ANTIALIAS_ON );

                String s = "Always eat slowly.";
                Font font = new Font( "Serif", Font.PLAIN, 32 );

                TextLayout textLayout = new TextLayout( s, font,
                        g2.getFontRenderContext() );
                textLayout.draw( g2, 40, 80 );
            }
        };
        f.setVisible( true );
    }
}