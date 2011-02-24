package com.becker.common.math.function;

import com.becker.common.math.Range;

/**
 * Defines interface for generic 1-1 function f(x).
 * @author Barry Becker
 */
public interface Function {

    /**
     * Given an x value, returns f(x)   (i.e. y)
     * @param value value to remap.
     * @return the remapped value.
     */
    double getValue(double value);

    /**
     * @return  range of x axis values
     */
    Range getDomain();
}
