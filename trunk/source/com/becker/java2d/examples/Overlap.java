package com.becker.java2d.examples;

import com.becker.ui.ApplicationFrame;

import java.awt.*;
import java.awt.geom.Ellipse2D;

public class Overlap
        extends ApplicationFrame
{
    public static void main( String[] args )
    {
        Overlap f = new Overlap();
        f.setTitle( "Overlap v1.0" );
        f.setSize( 300, 150 );
        f.center();
        f.setVisible( true );
    }

    public void paint( Graphics g )
    {
        Graphics2D g2 = (Graphics2D) g;
        double x = 15, y = 50, w = 70, h = 70;
        Ellipse2D e = new Ellipse2D.Double( x, y, w, h );
        g2.setStroke( new BasicStroke( 8 ) );
        // Stroke and paint.
        Color smokey = new Color( 128, 128, 128, 128 );
        g2.setPaint( smokey );
        g2.fill( e );
        g2.draw( e );
        // Stroke, then paint.
        e.setFrame( x + 100, y, w, h );
        g2.setPaint( Color.black );
        g2.draw( e );
        g2.setPaint( Color.gray );
        g2.fill( e );
        // Paint, then stroke.
        e.setFrame( x + 200, y, w, h );
        g2.setPaint( Color.gray );
        g2.fill( e );
        g2.setPaint( Color.black );
        g2.draw( e );
    }
}