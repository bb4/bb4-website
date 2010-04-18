package com.becker.common.math.function;

import com.becker.common.math.MathUtil;
import com.becker.common.math.Range;

/**
 * The function takes the log of a value in the specified base, then scales it.
 *
 * @author Barry Becker
 */
public class LogFunction implements Function {

    private static final double DEFAULT_BASE = 10;
    private double base;   
    private double scale;
    private double baseConverter;
    private boolean positiveOnly;

    /**
     * Constructor.
     */
    public LogFunction(double scale) {
        this(DEFAULT_BASE, scale, false);
    }

    /**
     * Constructor.
     * @param base  logarithm base.
     * @param scale amount to scale after taking the logarithm.
     * @param positiveOnly if true then clamp negative log values at 0.
     */
    public LogFunction(double scale,  double base, boolean positiveOnly) {
        this.scale = scale;
        this.base = base;
        baseConverter = Math.log(base);
        this.positiveOnly = positiveOnly;
    }

    public double getFunctionValue(double value) {

        if (value <= 0) {
            throw new IllegalArgumentException("Cannot take the log of a number (" + value + ") that is <=0");
        }
        double logValue = positiveOnly ?  Math.max(0, Math.log(value)) : Math.log(value);
        return  scale * logValue / baseConverter;
    }


    public double getInverseFunctionValue(double value) {

        return Math.pow(base, value / scale);
    }

    public Range getDomain() {
        return new Range(0, Double.MAX_VALUE);
    }

}