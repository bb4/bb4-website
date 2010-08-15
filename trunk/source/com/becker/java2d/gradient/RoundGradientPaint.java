package com.becker.java2d.gradient;

import java.awt.*;
import java.awt.geom.*;
import java.awt.image.ColorModel;

public class RoundGradientPaint
        implements Paint
{
    protected Point2D mPoint_;
    protected Point2D mRadius_;
    protected Color mPointColor_, mBackgroundColor_;

    public RoundGradientPaint( double x, double y, Color pointColor,
                               Point2D radius, Color backgroundColor )
    {
        if ( radius.distance( 0, 0 ) <= 0 )
            throw new IllegalArgumentException( "Radius must be greater than 0." );
        mPoint_ = new Point2D.Double( x, y );
        mPointColor_ = pointColor;
        mRadius_ = radius;
        mBackgroundColor_ = backgroundColor;
    }

    public PaintContext createContext( ColorModel cm,
                                       Rectangle deviceBounds, Rectangle2D userBounds,
                                       AffineTransform xform, RenderingHints hints )
    {
        Point2D transformedPoint = xform.transform( mPoint_, null );
        Point2D transformedRadius = xform.deltaTransform( mRadius_, null );
        return new RoundGradientContext( transformedPoint, mPointColor_,
                transformedRadius, mBackgroundColor_ );
    }

    public int getTransparency()
    {
        int a1 = mPointColor_.getAlpha();
        int a2 = mBackgroundColor_.getAlpha();
        return (((a1 & a2) == 0xff) ? OPAQUE : TRANSLUCENT);
    }
}