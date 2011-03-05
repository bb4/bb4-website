package com.becker.java2d.examples;

import com.becker.ui.application.ApplicationFrame;

import java.awt.*;
import java.awt.font.TextAttribute;
import java.awt.geom.AffineTransform;
import java.util.Hashtable;

public class FontDerivation extends ApplicationFrame
{
    public FontDerivation(String title)
    {
        super(title);
    }
    
    public void paint( Graphics g )
    {
        super.paint(g);
        Graphics2D g2 = (Graphics2D) g;

        g2.setRenderingHint( RenderingHints.KEY_ANTIALIASING,
                RenderingHints.VALUE_ANTIALIAS_ON );

        // Create a 1-point font.
        Font font = new Font( "Serif", Font.PLAIN, 1 );
        float x = 20, y = 20;

        // Derive a 24-point font.
        Font font24 = font.deriveFont( 24.0f );
        g2.setFont( font24 );
        g2.drawString( "font.deriveFont(24.0f)", x, y += 30 );

        // Now make it italic.
        Font font24italic = font24.deriveFont( Font.ITALIC );
        g2.setFont( font24italic );
        g2.drawString( "font24.deriveFont(Font.ITALIC)", x, y += 30 );

        // Now make it slant backwards with a shearing transformation.
        AffineTransform at = new AffineTransform();
        at.shear( .2, 0 );
        Font font24shear = font24.deriveFont( at );
        g2.setFont( font24shear );
        g2.drawString( "font24.deriveFont(at)", x, y += 30 );

        // Derive a bold font using an attribute Map.
        Hashtable attributes = new Hashtable();
        attributes.put( TextAttribute.WEIGHT, TextAttribute.WEIGHT_BOLD );
        Font font24bold = font24.deriveFont( attributes );
        g2.setFont( font24bold );
        g2.drawString( "font24.deriveFont(attributes)", x, y += 30 );
    }
    
    public static void main( String[] args )
    {
        new FontDerivation( "FontDerivation v1.0" );        
    }
}