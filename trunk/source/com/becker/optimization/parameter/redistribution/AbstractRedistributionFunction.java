/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package com.becker.optimization.parameter.redistribution;

import com.becker.common.function.Function;
import com.becker.common.util.MathUtil;

/**
 *
 * @author Barry Becker
 */
public abstract class AbstractRedistributionFunction implements RedistributionFunction {

    /** the discretized redistribution function */
    protected Function redistributionFunction;
    
    /**
     * 
     * @param value
     * @return
     */
    public double getFunctionValue(double value) {
        verifyInRange(value);
        
        double newValue = redistributionFunction.getFunctionValue(value);   // MathUtil.cubicInterpolate(value, functionMap);
  
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
        
        double newValue = redistributionFunction.getInverseFunctionValue(value); 
  
        verifyInRange(newValue);
        return newValue;
    }
    
    protected abstract void initializeFunction();
    
    protected static void verifyInRange(double value) {
        assert (value >= 0) && (value <= 1.0): "value, "+value+", was outside the range 0 to 1.";
    }
    
    
    
}
