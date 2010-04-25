/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package com.becker.optimization.parameter.redistribution;

import com.becker.common.math.function.Function;

/**
 * Responsible for defining the probability distribution for selecting random parameter values.
 * Derived classes will defin the different sorts of redistribution functions.
 * 
 * @author Barry Becker
 */
public interface RedistributionFunction extends Function {
   
    /**
     * Given an x value, returns f(x)   (i.e. y)
     * Remaps values in the range [0, 1] -> [0, 1]
     * @param value value to remap.
     * @return the remapped value.
     */
    double getValue(double value);
    
    /**
     * Given a y value (i.e. f(x)) return the corresponding x value.
     * Inverse of the above.
     * @param value
     * @return
     */
    double getInverseFunctionValue(double value);
    
}
