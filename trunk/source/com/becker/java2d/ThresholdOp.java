package com.becker.java2d;

import java.awt.*;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.awt.image.*;

public class ThresholdOp implements BufferedImageOp
{
    protected int mThreshold, mMinimum, mMaximum;

    public ThresholdOp( int threshold, int minimum, int maximum )
    {
        mThreshold = threshold;
        mMinimum = minimum;
        mMaximum = maximum;
    }

    public final BufferedImage filter( BufferedImage src, BufferedImage dst )
    {
        if ( dst == null ) dst = createCompatibleDestImage( src, null );

        for ( int y = 0; y < src.getHeight(); y++ ) {
            for ( int x = 0; x < src.getWidth(); x++ ) {
                int srcPixel = src.getRGB( x, y );
                Color c = new Color( srcPixel );
                int red = threshold( c.getRed() );
                int green = threshold( c.getGreen() );
                int blue = threshold( c.getBlue() );
                dst.setRGB( x, y, new Color( red, green, blue ).getRGB() );
            }
        }

        return dst;
    }

    public int threshold( int input )
    {
        if ( input < mThreshold )
            return mMinimum;
        else
            return mMaximum;
    }

    public BufferedImage createCompatibleDestImage( BufferedImage src,
                                                    ColorModel dstCM )
    {
        BufferedImage image;
        if ( dstCM == null ) dstCM = src.getColorModel();

        int width = src.getWidth();
        int height = src.getHeight();
        image = new BufferedImage( dstCM,
                dstCM.createCompatibleWritableRaster( width, height ),
                dstCM.isAlphaPremultiplied(), null );

        return image;
    }

    public final Rectangle2D getBounds2D( BufferedImage src )
    {
        return src.getRaster().getBounds();
    }

    public final Point2D getPoint2D( Point2D srcPt, Point2D dstPt )
    {
        if ( dstPt == null ) dstPt = new Point2D.Float();
        dstPt.setLocation( srcPt.getX(), srcPt.getY() );
        return dstPt;
    }

    public final RenderingHints getRenderingHints()
    {
        return null;
    }
}