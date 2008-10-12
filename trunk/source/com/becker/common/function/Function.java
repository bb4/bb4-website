/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package com.becker.common.function;

/**
 *
 * @author becker
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
    
}
