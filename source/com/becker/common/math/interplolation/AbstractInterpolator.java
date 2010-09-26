package com.becker.common.math.interplolation;

/**
 * Use to interpolate between values on a function defined only at discrete points.
 * @author Barry Becker
 */
public abstract class AbstractInterpolator implements Interpolator {

    protected double[] function;

    public AbstractInterpolator(double[] function) {
        this.function = function;
    }

}