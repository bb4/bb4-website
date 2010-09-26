package com.becker.java2d.examples;

import com.becker.java2d.Utilities;
import com.becker.ui.ApplicationFrame;

import java.awt.*;
import java.awt.font.GraphicAttribute;
import java.awt.font.ImageGraphicAttribute;
import java.awt.font.ShapeGraphicAttribute;
import java.awt.font.TextAttribute;
import java.awt.geom.Ellipse2D;
import java.text.AttributedString;

public class InlineGraphics extends ApplicationFrame
{
    
    public InlineGraphics()
    {
        super( "InlineGraphics v1.0");
    }
    
    public void paint( Graphics g )
    {
        super.paint(g);
        Graphics2D g2 = (Graphics2D) g;

        g2.setRenderingHint( RenderingHints.KEY_ANTIALIASING,
                RenderingHints.VALUE_ANTIALIAS_ON );
        Font serifFont = new Font( "Serif", Font.PLAIN, 32 );

        AttributedString as = new AttributedString( "Star \ufffc pin" );
        as.addAttribute( TextAttribute.FONT, serifFont );
        
        String filename = Utilities.DEFAULT_IMAGE_DIR+  "roa2.jpg";        
        Image image = new javax.swing.ImageIcon( filename ).getImage();
        
        ImageGraphicAttribute imageAttribute = new ImageGraphicAttribute(
                image, GraphicAttribute.TOP_ALIGNMENT );
        as.addAttribute( TextAttribute.CHAR_REPLACEMENT,
                imageAttribute, 5, 6 );
        g2.drawString( as.getIterator(), 20, 120 );

        as = new AttributedString( "Red \ufffc circle" );
        as.addAttribute( TextAttribute.FONT, serifFont );

        Shape shape = new Ellipse2D.Float( 0, -25, 25, 25 );
        ShapeGraphicAttribute shapeAttribute = new ShapeGraphicAttribute(
                shape, GraphicAttribute.ROMAN_BASELINE,
                ShapeGraphicAttribute.STROKE );
        as.addAttribute( TextAttribute.CHAR_REPLACEMENT,
                shapeAttribute, 4, 5 );
        as.addAttribute( TextAttribute.FOREGROUND, Color.red, 4, 5 );
        g2.drawString( as.getIterator(), 20, 200 );
    }
    
    public static void main( String[] args )
    {
         new InlineGraphics();
       
    }
}