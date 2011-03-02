package com.becker.common;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;

/**
 * This class maps numbers to colors.
 * The colors can include opacities and will get interpolated
 * The colormap can be dynamically changed by adding and removing control points.
 * @author Barry Becker
 */
public class ColorMap {
    final private List<Double> values_;
    final private List<Color> colors_;

    /**
     * give a list of (increasing) values and colors to map to.
     * the 2 arrays must be of the same length.
     * Colors will be linearly interpolated as appropriate
     * @param values a monotonically increasing sequence of numbers.
     * @param colors a corresponding set of colors to map to.
     */
    public ColorMap( double[] values, Color[] colors) {
        assert(values!=null) : "values was null";
        assert(colors!=null) : "colors was null";
        // should also assert that the values are increasing
        assert(values.length == colors.length): "there must be as many values as colors";
        values_ = new ArrayList<Double>();
        colors_ = new ArrayList<Color>();
        for (int i=0; i<values.length; i++) {
            values_.add(values[i]);
            colors_.add(colors[i]);
        }
    }

    public Color getColorForValue( final int value ) {
        return getColorForValue( (double) value );
    }

    /**
     *
     * @param value numeric value to get a color for from the continuous map.
     * @return color that corresponds to specified value.
     */
    public Color getColorForValue( final double value ) {
        int len = getNumValues();
        if ( value <= values_.get(0)) {
            return colors_.get(0);
        }
        else if (value >= values_.get(len-1)) {
            return colors_.get(len-1);
        }
        int i = 1;
        while ( i < len && value > values_.get(i) ) {
            i++;
        }
        if ( i == len ) {
            return colors_.get(len - 1);
        }

        double x = (double) i - 1.0 + (value - values_.get(i - 1)) / (values_.get(i) - values_.get(i - 1));
        return interpolate( x );
    }

    /**
     * I don't think we should get a race condition because the static rgb variables are only used in this
     * class and this method is synchronized. I want to avoid creating the rgb arrays each time the method is called.
     * @param x value to retur color for.
     * @return interpolated color
     */
    private Color interpolate( double x ) {
        int i = (int) x;
        double delta = x - (double) i;
        float[] rgba_ = new float[4];
        float[] rgba1_ = new float[4];
        colors_.get(i).getComponents( rgba_ );
        colors_.get(i + 1).getComponents( rgba1_ );
        return new Color( (float) (rgba_[0] + delta * (rgba1_[0] - rgba_[0])),
                (float) (rgba_[1] + delta * (rgba1_[1] - rgba_[1])),
                (float) (rgba_[2] + delta * (rgba1_[2] - rgba_[2])),
                (float) (rgba_[3] + delta * (rgba1_[3] - rgba_[3])) );
    }

    public double getMinValue() {
        return values_.get(0);
    }

    public double getMaxValue() {
        return values_.get(getNumValues() - 1);
    }

    public double getValueRange() {
        return getMaxValue() - getMinValue();
    }

    public double getValue(int index) {
        return values_.get(index);
    }

    public synchronized void setValue(int index, double value) {
        if (index > 0) {
            assert(value >= values_.get(index - 1)):
                    "Can't set value="+value+" that is less than "+ values_.get(index - 1);
        }
        if (index < getNumValues() -1) {
            assert(value <= values_.get(index + 1)):
                    "Can't set value="+value+" that is greater than "+ values_.get(index + 1);
        }
        values_.set(index, value);
    }


    public Color getColor(int index) {
        return colors_.get(index);
    }

    public synchronized void setColor(int index, Color newColor) {
         colors_.set(index, newColor);
    }

    public int getNumValues() {
        return values_.size();
    }

    public void insertControlPoint(int index, double value, Color color) {
        values_.add(index, value);
        colors_.add(index, color);
    }

    public void removeControlPoint(int index) {
        values_.remove(index);
        colors_.remove(index);
    }

    /**
     * Given a value, return the closest control index.
     * @return closest index looking to left or right.
     */
    public int getClosestIndexForValue(double value) {

        int len = getNumValues();
        if ( value <= values_.get(0)) {
            return 0;
        }
        else if (value >= values_.get(len-1)) {
            return len-1;
        }

        int i = 1;
        while ( i < len && value > values_.get(i) ) {
            i++;
        }
        if ( i == len ) {
            return len - 1;
        }
        if (value - values_.get(i - 1) > values_.get(i) - value) {
            return i;
        }
        else {
           return i - 1;
        }
    }

    /**
     * Given a value, return the control index to the left of value.
     * @return closest index just to the left.
     */
    public int getLeftIndexForValue(double value) {
        int len = getNumValues();
        assert(value >= values_.get(0));
        if (value >= values_.get(len-2))
            return len-2;
        int i = 1;
        while ( value > values_.get(i) ) {
            i++;
        }
        return i - 1;
    }
}