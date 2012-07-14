package com.barrybecker4.java2d.gradient;

import java.awt.*;
import java.awt.geom.Point2D;
import java.awt.image.ColorModel;
import java.awt.image.Raster;
import java.awt.image.WritableRaster;

public class RoundGradientContext implements PaintContext {
    protected Point2D mPoint_;
    protected Point2D mRadius_;
    protected Color mC1_, mC2_;

    public RoundGradientContext( Point2D p, Color c1, Point2D r, Color c2 ) {
        mPoint_ = p;
        mC1_ = c1;
        mRadius_ = r;
        mC2_ = c2;
    }

    public void dispose() {}

    public ColorModel getColorModel() {
        return ColorModel.getRGBdefault();
    }

    public Raster getRaster( int x, int y, int w, int h ) {
        WritableRaster raster =
                getColorModel().createCompatibleWritableRaster( w, h );

        int[] data = new int[w * h << 2];
        double radius = mRadius_.distance( 0, 0 );
        for ( int j = 0; j < h; j++ ) {
            for ( int i = 0; i < w; i++ ) {
                double distance = mPoint_.distance( x + i, y + j );
                double ratio = distance / radius;
                if ( ratio > 1.0 )
                    ratio = 1.0;

                int base = (j * w + i) << 2;
                data[base] = (int) (mC1_.getRed() + ratio *
                        (mC2_.getRed() - mC1_.getRed()));
                data[base + 1] = (int) (mC1_.getGreen() + ratio *
                        (mC2_.getGreen() - mC1_.getGreen()));
                data[base + 2] = (int) (mC1_.getBlue() + ratio *
                        (mC2_.getBlue() - mC1_.getBlue()));
                data[base + 3] = (int) (mC1_.getAlpha() + ratio *
                        (mC2_.getAlpha() - mC1_.getAlpha()));
            }
        }
        raster.setPixels( 0, 0, w, h, data );

        return raster;
    }
}