package com.becker.java2d.examples;

import com.becker.ui.application.ApplicationFrame;

import java.awt.*;
import java.awt.image.BufferedImage;

public class TexturedText extends ApplicationFrame
{
    
    public TexturedText(String title)
    {
        super(title);
    }
    
    public void paint( Graphics g )
    {
        super.paint(g);
        
        Graphics2D g2 = (Graphics2D) g;        

        g2.setRenderingHint( RenderingHints.KEY_ANTIALIASING,
                RenderingHints.VALUE_ANTIALIAS_ON );
        Font font = new Font( "Times New Roman", Font.PLAIN, 72 );
        g2.setFont( font );

        String s = "Checkmate!";
        Dimension d = getSize();
        float x = 20, y = 100;

        BufferedImage bi = getTextureImage();
        Rectangle r = new Rectangle( 0, 0, bi.getWidth(), bi.getHeight() );
        TexturePaint tp = new TexturePaint( bi, r );
        g2.setPaint( tp );

        g2.drawString( s, x, y );
    }

    private BufferedImage getTextureImage()
    {
        // Create the misc image.
        int size = 8;
        BufferedImage bi = new BufferedImage( size, size,
                BufferedImage.TYPE_INT_ARGB );
        Graphics2D g2 = bi.createGraphics();
        g2.setPaint( Color.red );
        g2.fillRect( 0, 0, size / 2, size / 2 );
        g2.setPaint( Color.yellow );
        g2.fillRect( size / 2, 0, size, size / 2 );
        g2.setPaint( Color.green );
        g2.fillRect( 0, size / 2, size / 2, size );
        g2.setPaint( Color.blue );
        g2.fillRect( size / 2, size / 2, size, size );
        return bi;
    }

    public static void main( String[] args )
    {
        Frame f = new TexturedText( "TexturedText v1.0" );
    }
}