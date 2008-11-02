package com.becker.common.function;

import com.becker.common.Range;
import com.becker.common.util.MathUtil;

/**
 * The function is represented with an array of lookups that
 * can be interpolated using some method.
 * 
 * @author Barry Becker
 */
public class ArrayFunction implements Function {

    /** the linear function lookup table. */
    private double[] functionMap;
    
    /** The inverse lookup for the main function. */
    private double[] inverseFunctionMap;
    
    public enum InterpolationMethod {LINEAR, CUBIC};
    
    private InterpolationMethod interpolationMethod;
     
    /**
     * Constructor.
     * @param funcMap
     */
    public ArrayFunction(double[] func, InterpolationMethod interpMethod) {
        this(func, MathUtil.createInverseFunction(func, new Range(0, 1.0)), interpMethod);        
    }
    
    /**
     * Constructor.
     * Use this version of the constructor if you already know the inverse function and do not 
     * want to compute it (because computing it will not be as accurate).
     * @param funcMap
     */
    public ArrayFunction(double[] func, double[] inverseFunc) {
        this(func, inverseFunc, InterpolationMethod.LINEAR);        
    }
    
    /**
     * Constructor.
     * Use this version of the constructor if you already know the inverse function and do not 
     * want to compute it (because computing it will not be as accurate).
     * @param funcMap
     */
    public ArrayFunction(double[] func, double[] inverseFunc, InterpolationMethod interpMethod) {
        functionMap = func;
        inverseFunctionMap = inverseFunc;
        interpolationMethod = interpMethod;        
    }
    
    
    /**
     * Constructor.
     * @param funcMap
     */
    public ArrayFunction(double[] funcMap) {
        this(funcMap, InterpolationMethod.LINEAR);
    }
    
    /**
     * 
     * @param value
     * @return
     */
    public double getFunctionValue(double value) {
        
        return getValue(value, functionMap); 
    }
    
    /**
     * 
     * @param value
     * @return
     */
    public double getInverseFunctionValue(double value) {
        
        return getValue(value, inverseFunctionMap); 
    }
    
    private double getValue(double value, double[] func) {
        double funcValue = 0;
        
        switch (interpolationMethod) {
            case CUBIC: 
                funcValue = MathUtil.cubicInterpolate(value, func);
                break;
            case LINEAR:
                 funcValue = MathUtil.linearlyInterpolate(value, func);
                 break;
        }
  
        return funcValue;
    }
}
