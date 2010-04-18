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
    double getFunctionValue(double value);
    
    /**
     * Given a y value (i.e. f(x)) return the corresponding x value.
     * Inverse of the above.
     * @param value
     * @return
     */
    double getInverseFunctionValue(double value);

    /**
     * @return  range of x axis values
     */
    Range getDomain();
}
