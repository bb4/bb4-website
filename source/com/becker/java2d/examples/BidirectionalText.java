package com.becker.java2d.examples;

import com.becker.ui.ApplicationFrame;

import java.awt.*;

public class BidirectionalText
{
    public static void main( String[] args )
    {
        Frame f = new ApplicationFrame( "BidirectionalText v1.0" )
        {
            public void paint( Graphics g )
            {
                Graphics2D g2 = (Graphics2D) g;
                g2.setRenderingHint( RenderingHints.KEY_ANTIALIASING,
                        RenderingHints.VALUE_ANTIALIAS_ON );

                Font font = new Font( "Lucida Sans Regular", Font.PLAIN, 32 );

                g2.setFont( font );
                g2.drawString( "Please \u062e\u0644\u0639 slowly.", 40, 80 );
            }
        };
        f.setVisible( true );
    }
}