package com.becker.java2d.examples;

import com.becker.ui.ApplicationFrame;

import java.awt.*;
import java.awt.geom.Line2D;

public class StringArt extends ApplicationFrame
{
    private int mNumberOfLines = 25;
    private Color[] mColors = {Color.red, Color.green, Color.blue};

            
    public StringArt(String title)
    {
        super(title);
    }
    
    public void paint( Graphics g )
        {
            Graphics2D g2 = (Graphics2D) g;

            Dimension d = getSize();
            for ( int i = 0; i < mNumberOfLines; i++ ) {
                double ratio = (double) i / (double) mNumberOfLines;
                Line2D line = new Line2D.Double( 0, ratio * d.height,
                        ratio * d.width, d.height );
                g2.setPaint( mColors[i % mColors.length] );
                g2.draw( line );
            }
        }
    
    public static void main( String[] args )
    {
        Frame f = new StringArt( "StringArt v1.0" );         
    }
}