package com.becker.java2d.examples;

import com.becker.ui.application.ApplicationFrame;

import java.awt.*;
import java.awt.font.TextAttribute;
import java.text.AttributedString;

public class IteratorTest
{
    public static void main( String[] args )
    {
        Frame f = new ApplicationFrame( "IteratorTest v1.0" )
        {
            public void paint( Graphics g )
            {
                Graphics2D g2 = (Graphics2D) g;

                String s = "a big surprise";
                Dimension d = getSize();

                g2.setRenderingHint( RenderingHints.KEY_ANTIALIASING,
                        RenderingHints.VALUE_ANTIALIAS_ON );
                Font serifFont = new Font( "Serif", Font.PLAIN, 48 );
                Font sansSerifFont = new Font( "Monospaced", Font.PLAIN, 48 );

                AttributedString as = new AttributedString( s );
                as.addAttribute( TextAttribute.FONT, serifFont );
                as.addAttribute( TextAttribute.FONT, sansSerifFont, 2, 5 );
                as.addAttribute( TextAttribute.FOREGROUND, Color.red, 2, 5 );

                g2.drawString( as.getIterator(), 40, 80 );
            }
        };
        f.setVisible( true );
    }
}