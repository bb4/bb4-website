package com.becker.java2d.examples;

import com.becker.ui.ApplicationFrame;

import java.awt.*;
import java.awt.geom.Ellipse2D;

public class GradientPaintFill
        extends ApplicationFrame
{
    public static void main( String[] args )
    {
        GradientPaintFill f = new GradientPaintFill();
        f.setTitle( "GradientPaintFill v1.0" );
        f.setSize( 200, 200 );
        f.center();
        f.setVisible( true );
    }

    public void paint( Graphics g )
    {
        Graphics2D g2 = (Graphics2D) g;
        Ellipse2D e = new Ellipse2D.Float( 40, 40, 120, 120 );
        GradientPaint gp = new GradientPaint( 75, 75, Color.white,
                95, 95, Color.gray, true );
        g2.setPaint( gp );
        g2.fill( e );
    }
}