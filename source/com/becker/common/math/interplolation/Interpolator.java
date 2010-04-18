package com.becker.common.math.interplolation;

import com.becker.common.math.Range;

/**
 * Defines a way to interploate between 2 points in function that is defined by an array of y values
 * .
 * @author Barry Becker
 */
public interface Interpolator {

    /**
     * Given an x value, returns f(x)   (i.e. y)
     * @param value value to find interpolated function value for.
     * @return the interpolated value.
     */
    double interpolate(double value);

}