package com.becker.common.math.function;

import com.becker.common.math.Range;
import com.becker.common.math.MathUtil;
import com.becker.common.math.interplolation.InterpolationMethod;
import com.becker.common.math.interplolation.Interpolator;

/**
 * The function is represented with an array of lookups that
 * can be interpolated using some method.
 * 
 * @author Barry Becker
 */
public class ArrayFunction implements InvertibleFunction {

    /** the linear function lookup table. */
    private double[] functionMap;
    
    /** The inverse lookup for the main function. */
    private double[] inverseFunctionMap;

    private Interpolator interpolator_;
    private Interpolator inverseInterpolator_;

     
    /**
     * Constructor.
     * @param func
     * @parma interpolationMethod
     */
    public ArrayFunction(double[] func, InterpolationMethod interpMethod) {
        this(func, MathUtil.createInverseFunction(func, new Range(0, 1.0)), interpMethod);
    }
    
    /**
     * Constructor.
     * Use this version of the constructor if you already know the inverse function and do not 
     * want to compute it (because computing it will not be as accurate).
     * @param func function definition.
     */
    public ArrayFunction(double[] func, double[] inverseFunc) {
        this(func, inverseFunc, InterpolationMethod.LINEAR);        
    }
    
    /**
     * Constructor.
     * Use this version of the constructor if you already know the inverse function and do not 
     * want to compute it (because computing it will not be as accurate).
     * @param func
     */
    public ArrayFunction(double[] func, double[] inverseFunc, InterpolationMethod interpMethod) {
        functionMap = func;
        inverseFunctionMap = inverseFunc;
        interpolator_ = interpMethod.createInterpolator(func);
        inverseInterpolator_ = interpMethod.createInterpolator(inverseFunc);
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
    public double getValue(double value) {

        return interpolator_.interpolate(value);
    }

    public Range getDomain() {
        return new Range(0, 1.0);
    }
    
    /**
     * 
     * @param value
     * @return  inverse function value
     */
    public double getInverseValue(double value) {

        return inverseInterpolator_.interpolate(value);
    }

    public void setInterpolationMethod(InterpolationMethod interp) {
        interpolator_ = interp.createInterpolator(functionMap);
        inverseInterpolator_ = interp.createInterpolator(inverseFunctionMap);
    }
}