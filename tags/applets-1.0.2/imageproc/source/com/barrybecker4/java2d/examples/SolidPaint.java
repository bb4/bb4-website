package com.barrybecker4.java2d.examples;

import com.barrybecker4.ui.application.ApplicationFrame;

import java.awt.*;
import java.awt.geom.Arc2D;

/**
 * Derived from code accompanying "Java 2D Graphics" by Jonathan Knudsen.
 */
public class SolidPaint
        extends ApplicationFrame{
    public static void main( String[] args ) {
        SolidPaint f = new SolidPaint();
        f.setTitle( "SolidPaint v1.0" );
        f.setSize( 200, 200 );
        f.center();
        f.setVisible( true );
    }

    @Override
    public void paint( Graphics g ){
        Graphics2D g2 = (Graphics2D) g;
        Arc2D pie = new Arc2D.Float( 0, 50, 150, 150, -30, 90, Arc2D.PIE );
        g2.setPaint( Color.blue );
        g2.fill( pie );
    }
}