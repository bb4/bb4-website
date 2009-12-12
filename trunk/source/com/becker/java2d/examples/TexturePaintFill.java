package com.becker.java2d.examples;

import com.becker.java2d.Utilities;
import com.becker.ui.ApplicationFrame;

import java.awt.*;
import java.awt.geom.Rectangle2D;
import java.awt.geom.RoundRectangle2D;
import java.awt.image.BufferedImage;
import java.io.IOException;

public class TexturePaintFill
        extends ApplicationFrame
{
    public static void main( String[] args ) throws Exception
    {
        TexturePaintFill f = new TexturePaintFill( "roa2.jpg" );
        f.setTitle( "TexturePaintFill v1.0" );
        f.setSize( 200, 200 );
        f.center();
        f.setVisible( true );
    }

    private BufferedImage mImage;

    public TexturePaintFill( String filename )
            throws IOException
    {        
        Image img = Utilities.blockingLoad( Utilities.DEFAULT_IMAGE_DIR +  filename );
        mImage = Utilities.makeBufferedImage( img );
    }

    public void paint( Graphics g )
    {
        super.paint(g);
        Graphics2D g2 = (Graphics2D) g;
        // Create a round rectangle.
        RoundRectangle2D r = new RoundRectangle2D.Float( 25, 35, 150, 150, 25, 25 );
        // Create a texture rectangle the same size as the texture image.
        Rectangle2D tr = new Rectangle2D.Double( 0, 0,
                mImage.getWidth(), mImage.getHeight() );
        // Create the TexturePaint.
        TexturePaint tp = new TexturePaint( mImage, tr );
        // Now fill the round rectangle.
        g2.setPaint( tp );
        g2.fill( r );
    }
}