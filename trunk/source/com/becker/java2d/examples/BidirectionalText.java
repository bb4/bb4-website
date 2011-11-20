package com.becker.java2d.examples;

import com.becker.ui.application.ApplicationFrame;

import java.awt.*;

public class BidirectionalText extends ApplicationFrame {
    
    public BidirectionalText(String title) {
        super(title);
    }
    
    public void paint( Graphics g ) {
        super.paint(g);
        Graphics2D g2 = (Graphics2D) g;
        g2.setRenderingHint( RenderingHints.KEY_ANTIALIASING,
                RenderingHints.VALUE_ANTIALIAS_ON );

        Font font = new Font( "Lucida Sans Regular", Font.PLAIN, 32 );

        g2.setFont( font );
        g2.drawString( "Please \u062e\u0644\u0639 slowly.", 40, 80 );
    }

    public static void main( String[] args ) {
        new BidirectionalText( "BidirectionalText v1.0" );
     
    }
}