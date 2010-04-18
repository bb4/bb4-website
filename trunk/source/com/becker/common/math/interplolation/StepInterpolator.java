package com.becker.common.math.interplolation;

/**
 * @author Barry Becker
 */
public class StepInterpolator extends AbstractInterpolator {

    public StepInterpolator(double[] function) {
        super(function);
    }

    public double interpolate(double value) {
        return function[(int)(value * (function.length-1))];
    }

}