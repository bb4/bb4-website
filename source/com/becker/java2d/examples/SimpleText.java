package com.becker.java2d.examples;

import com.becker.ui.ApplicationFrame;

import java.awt.*;

public class SimpleText
{
    public static void main( String[] args )
    {
        Frame f = new ApplicationFrame( "SimpleText v1.0" )
        {
            public void paint( Graphics g )
            {
                super.paint(g);
                Graphics2D g2 = (Graphics2D) g;

                g2.setRenderingHint( RenderingHints.KEY_ANTIALIASING,
                        RenderingHints.VALUE_ANTIALIAS_ON );
                Font font = new Font( "Serif", Font.PLAIN, 96 );
                g2.setFont( font );

                g2.drawString( "jade", 40, 120 );
            }
        };
        f.setVisible( true );
    }
}