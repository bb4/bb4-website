package com.becker.common;

import java.awt.*;

/**
 * This class maps numbers to colors.
 * The colors can include opacities and will get interpolated
 *
 * @author Barry Becker
 */
public class ColorMap
{

    protected double[] values_ = null;
    protected Color[] colors_ = null;
    // temp vars for interpolation
    private static float[] rgba_ = new float[4];
    private static float[] rgba1_ = new float[4];
    //private float opacity_ = 1.0f; // opaque by default

    /**
     * give a list of (increasing) values and colors to map to.
     * the 2 arrays must be of the same length.
     * Colors will be linearly interpolated as appropriate
     * @param values a monotonically increasing sequence of numbers.
     * @param colors a corresponding set of colors to map to.
     */
    public ColorMap( double[] values, Color[] colors )
    {
        assert(values!=null) : "values was null";
        assert(colors!=null) : "colors was null";
        values_ = values;
        colors_ = colors;
        // should also assert that the values are increasing
        assert(values_.length == colors_.length): "there must be as many values as colors";
    }

    public Color getColorForValue( double value )
    {
        int len = values_.length;
        if ( value <= values_[0] )
            return colors_[0];
        else if (value >= values_[len-1])
            return colors_[len-1];
        int i = 1;
        while ( i < len && value > values_[i] )
            i++;
        if ( i == len )
            return colors_[len - 1];

        double x = (double) i - 1.0 + (value - values_[i - 1]) / (values_[i] - values_[i - 1]);
        return interpolate( x );
    }

    private Color interpolate( double x )
    {
        int i = (int) x;
        double delta = x - (double) i;
        colors_[i].getComponents( rgba_ );
        colors_[i + 1].getComponents( rgba1_ );
        return new Color( (float) (rgba_[0] + delta * (rgba1_[0] - rgba_[0])),
                (float) (rgba_[1] + delta * (rgba1_[1] - rgba_[1])),
                (float) (rgba_[2] + delta * (rgba1_[2] - rgba_[2])),
                (float) (rgba_[3] + delta * (rgba1_[3] - rgba_[3])) );
    }

    public Color getMinColor()
    {
        return  colors_[0];
    }

    public Color getMaxColor()
    {
        return  colors_[values_.length-1];
    }

}