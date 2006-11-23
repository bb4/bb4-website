package com.becker.common;

import java.awt.*;
import java.util.*;
import java.util.List;

/**
 * This class maps numbers to colors.
 * The colors can include opacities and will get interpolated
 * The colormap can be dynamically changed by adding and removing control points.
 * @author Barry Becker
 */
public class ColorMap
{

    private List<Double> values_;
    private List<Color> colors_;

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
    public ColorMap( double[] values, Color[] colors)
    {
        assert(values!=null) : "values was null";
        assert(colors!=null) : "colors was null";
        // should also assert that the values are increasing
        assert(values.length == colors.length): "there must be as many values as colors";
        values_ = new ArrayList<Double>(); //Collections.synchronizedList(new ArrayList<Double>());
        colors_ = new ArrayList<Color>(); //Collections.synchronizedList(new ArrayList<Color>());
        for (int i=0; i<values.length; i++) {
            values_.add(values[i]);
            colors_.add(colors[i]);
        }
    }

    public synchronized Color getColorForValue( double value )
    {
        int len = getNumValues();
        if ( value <= values_.get(0))
            return colors_.get(0);
        else if (value >= values_.get(len-1))
            return colors_.get(len-1);
        int i = 1;
        while ( i < len && value > values_.get(i) )
            i++;
        if ( i == len )
            return colors_.get(len - 1);

        double x = (double) i - 1.0 + (value - values_.get(i - 1)) / (values_.get(i) - values_.get(i - 1));
        return interpolate( x );
    }

    private synchronized Color interpolate( double x )
    {
        int i = (int) x;
        double delta = x - (double) i;
        colors_.get(i).getComponents( rgba_ );
        colors_.get(i + 1).getComponents( rgba1_ );
        return new Color( (float) (rgba_[0] + delta * (rgba1_[0] - rgba_[0])),
                (float) (rgba_[1] + delta * (rgba1_[1] - rgba_[1])),
                (float) (rgba_[2] + delta * (rgba1_[2] - rgba_[2])),
                (float) (rgba_[3] + delta * (rgba1_[3] - rgba_[3])) );
    }

    public synchronized Color getMinColor()
    {
        return colors_.get(0);
    }

    public synchronized Color getMaxColor()
    {
        return colors_.get(getNumValues() - 1);
    }

    public synchronized double getMinValue() {
        return values_.get(0);
    }

    public synchronized double getMaxValue() {
        return values_.get(getNumValues() - 1);
    }

    public synchronized double getMidPointValue() {
        return (getMaxValue() - getMinValue())/2.0;
    }

    public synchronized double getValueRange() {
        return getMaxValue() - getMinValue();
    }

    public synchronized double getValue(int index) {
        return values_.get(index);
    }

    public synchronized void setValue(int index, double value) {
        if (index > 0)
            assert(value >= values_.get(index - 1)):
                    "Can't set value="+value+" that is less than "+ values_.get(index - 1);
        if (index < getNumValues() -1)
            assert(value <= values_.get(index + 1)):
                    "Can't set value="+value+" that is greater than "+ values_.get(index + 1);
        values_.set(index, value);
    }


    public synchronized Color getColor(int index) {
        return colors_.get(index);
    }

    public synchronized void setColor(int index, Color newColor) {
         colors_.set(index, newColor);
    }

    public synchronized int getNumValues() {
        return values_.size();
    }

    public synchronized void insertControlPoint(int index, double value, Color color) {
        values_.add(index, value);
        colors_.add(index, color);
    }

    public synchronized void removeControlPoint(int index) {
        values_.remove(index);
        colors_.remove(index);
    }

    /**
     * Given a value, return the closest control index.
     */
    public synchronized int getClosestIndexForValue(double value) {
        
        int len = getNumValues();
        if ( value <= values_.get(0))
            return 0;
        else if (value >= values_.get(len-1))
            return len-1;
        int i = 1;
        while ( i < len && value > values_.get(i) )
            i++;
        if ( i == len )
            return len - 1;

        if (value - values_.get(i - 1) > values_.get(i) - value)
           return i;
        else
           return i - 1;
    }

    /**
     * Given a value, return the control index to the left of value.
     */
    public synchronized int getLeftIndexForValue(double value) {
        int len = getNumValues();
        assert(value >= values_.get(0));
        if (value >= values_.get(len-2))
            return len-2;
        int i = 1;
        while ( value > values_.get(i) )
            i++;
        return i - 1;
    }
}