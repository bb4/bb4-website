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
    public ArrayFunction(double[] funcMap, InterpolationMethod interpMethod) {
        functionMap = funcMap;
        inverseFunctionMap = MathUtil.createInverseFunction(funcMap, new Range(0, 1.0));
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
                funcValue = MathUtil.cubicInterpolate(value, functionMap);
                break;
            case LINEAR:
                 funcValue = MathUtil.linearlyInterpolate(value, functionMap);
                 break;
        }
  
        return funcValue;
    }
}
