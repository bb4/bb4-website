/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package com.becker.optimization.parameter.redistribution;

import com.becker.common.math.Range;
import com.becker.common.math.function.InvertibleFunction;

/**
 *
 * @author Barry Becker
 */
public abstract class AbstractRedistributionFunction implements RedistributionFunction {

    /** the discretized redistribution function */
    protected InvertibleFunction redistributionFunction;
    
    /**
     * 
     * @param value
     * @return
     */
    public double getValue(double value) {
        verifyInRange(value);
        
        double newValue = redistributionFunction.getValue(value);
  
        verifyInRange(newValue);
        return newValue;
    }
    
    /**
     * 
     * @param value
     * @return
     */
    public double getInverseFunctionValue(double value) {
        verifyInRange(value);
        
        double newValue = redistributionFunction.getInverseValue(value);
  
        verifyInRange(newValue);
        return newValue;
    }

    public Range getDomain() {
        return new Range(0, 1.0);
    }
    
    protected abstract void initializeFunction();
    
    protected static void verifyInRange(double value) {
        assert (value >= 0) && (value <= 1.0): "value, "+value+", was outside the range 0 to 1.";
    }
    
    
    
}
