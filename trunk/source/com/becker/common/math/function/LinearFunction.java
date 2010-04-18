package com.becker.common.math.function;

import com.becker.common.math.Range;

/**
 * The function scales and offsets a value
 *
 * @author Barry Becker
 */
public class LinearFunction implements Function {

    private double scale;
    private double offset;


    /**
     * Constructor.
     */
    public LinearFunction(double scale) {
        this(scale, 0);
    }

    /**
     * Constructor.
     * @param scale amount to multiply/scale the value by
     * @param offset amount to add after scaling.
     */
    public LinearFunction(double scale, double offset) {
        this.scale = scale;
        this.offset = offset;
        if (scale == 0)  {
            throw new IllegalArgumentException("scale cannot be 0.");
        }
    }

    public double getFunctionValue(double value) {
        return scale * value + offset;
    }

    public double getInverseFunctionValue(double value) {
        return (value - offset) / scale;
    }

    public Range getDomain() {
        return new Range(Double.MIN_VALUE, Double.MAX_VALUE);
    }
}