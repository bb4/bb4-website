package com.becker.java2d.examples;

import com.becker.ui.ApplicationFrame;
import com.sun.image.codec.jpeg.JPEGCodec;
import com.sun.image.codec.jpeg.JPEGImageDecoder;

import java.awt.*;
import java.awt.font.FontRenderContext;
import java.awt.font.GlyphVector;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;

public class ClipImage
{
    public static void main( String[] args ) throws IOException
    {
        String filename = "roa2.jpg";
        InputStream in = ClipImage.class.getResourceAsStream( filename );
        JPEGImageDecoder decoder = JPEGCodec.createJPEGDecoder( in );
        final BufferedImage image = decoder.decodeAsBufferedImage();
        in.close();
        ApplicationFrame f = new ApplicationFrame( "ClipImage v1.0" )
        {
            private Shape mClippingShape;

            public void paint( Graphics g )
            {
                Graphics2D g2 = (Graphics2D) g;
                Dimension d = getSize();
                g2.rotate( -Math.PI / 12, d.width / 2, d.height / 2 );
                g2.clip( getClippingShape( g2 ) );
                g2.drawImage( image, 0, 0, null );
            }

            private Shape getClippingShape( Graphics2D g2 )
            {
                if ( mClippingShape != null ) return mClippingShape;
                String s = "bella";
                Font font = new Font( "Serif", Font.PLAIN, 192 );
                FontRenderContext frc = g2.getFontRenderContext();
                GlyphVector gv = font.createGlyphVector( frc, s );
                mClippingShape = gv.getOutline( 10, 200 );
                return mClippingShape;
            }
        };
        f.setSize( 400, 250 );
        f.center();
        f.setVisible( true );
    }
}