package com.becker.java2d.examples;

import com.becker.ui.application.ApplicationFrame;

import java.awt.*;
import java.awt.geom.Rectangle2D;

public class ColorBlocks extends ApplicationFrame
{
    public ColorBlocks(String title) 
    {
        super(title);
    }
    
    public void paint( Graphics g )
    {
        super.paint(g);
        Graphics2D g2 = (Graphics2D) g;
        // Center User Space.
        Dimension d = getSize();
        g2.translate( d.width / 2, d.height / 2 );

        Color[] colors = {
            Color.white, Color.lightGray, Color.gray, Color.darkGray,
            Color.black, Color.red, Color.pink, Color.orange,
            Color.yellow, Color.green, Color.magenta, Color.cyan, Color.blue
        };

        int limit = colors.length;
        float s = 20;
        float x = -s * limit / 2;
        float y = -s * 3 / 2;

        // Show all the predefined colors.
        for ( int i = 0; i < limit; i++ ) {
            Rectangle2D r = new Rectangle2D.Float(
                    x + s * (float) i, y, s, s );
            g2.setPaint( colors[i] );
            g2.fill( r );
        }

        // Show a linear gradient.
        y += s;
        Color c1 = Color.yellow;
        Color c2 = Color.blue;
        for ( int i = 0; i < limit; i++ ) {
            float ratio = (float) i / (float) limit;
            int red = (int) (c2.getRed() * ratio + c1.getRed() * (1 - ratio));
            int green = (int) (c2.getGreen() * ratio +
                    c1.getGreen() * (1 - ratio));
            int blue = (int) (c2.getBlue() * ratio +
                    c1.getBlue() * (1 - ratio));
            Color c = new Color( red, green, blue );
            Rectangle2D r = new Rectangle2D.Float(
                    x + s * (float) i, y, s, s );
            g2.setPaint( c );
            g2.fill( r );
        }

        // Show an alpha gradient.
        y += s;
        c1 = Color.red;
        for ( int i = 0; i < limit; i++ ) {
            int alpha = (int) (255 * (float) i / (float) limit);
            Color c = new Color( c1.getRed(), c1.getGreen(),
                    c1.getBlue(), alpha );
            Rectangle2D r = new Rectangle2D.Float(
                    x + s * (float) i, y, s, s );
            g2.setPaint( c );
            g2.fill( r );
        }

        // Draw a frame around the whole thing.
        y -= s * 2;
        Rectangle2D frame = new Rectangle2D.Float( x, y, s * limit, s * 3 );
        g2.setPaint( Color.black );
        g2.draw( frame );
    }

    public static void main( String[] args )
    {
        
        new ColorBlocks( "ColorBlocks v1.0" );
        
    }
}