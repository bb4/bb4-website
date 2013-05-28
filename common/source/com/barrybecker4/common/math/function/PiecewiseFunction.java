/** Copyright by Barry G. Becker, 2000-2011. Licensed under MIT License: http://www.opensource.org/licenses/MIT  */
package com.barrybecker4.common.math.function;

import com.barrybecker4.common.math.Range;


/**
 * Piecewise linear function representation.
 *
 * @author Barry Becker
 */
public class PiecewiseFunction implements InvertibleFunction {

    /** These parallel arrays define the piecewise function map. */
    protected double[] xValues;
    protected double[] yValues;

    /**
     * Constructor.
     * @param xValues
     * @param yValues
     */
    public PiecewiseFunction(double[] xValues, double[] yValues ) {
        this.xValues = xValues;
        this.yValues = yValues;
        assert this.xValues.length == this.yValues.length;
    }

    /**
     *
     * @param value
     * @return
     */
    @Override
    public double getValue(double value) {

        return getValue(value, xValues, yValues);
    }

    /**
     *
     * @param value
     * @return inverse function value.
     */
    @Override
    public double getInverseValue(double value) {

        return getValue(value, yValues, xValues);
    }

    @Override
    public Range getDomain() {
        return new Range(xValues[0], xValues[xValues.length-1]);
    }


    private double getValue(double value, double[] xVals, double [] yVals) {

        // first find the x value
        int i=0;
        while (value > xVals[i]) {
            i++;
        }

        // return the linearly interpolated y value
        if (i == 0) {
            return yVals[0];
        }
        double xValm1 = xVals[i - 1];
        double denom = (xVals[i] - xValm1);

        if (denom == 0) {
            return yVals[i - 1];
        } else {
            double ratio = (value - xValm1) / denom;
            double yValm1 = yVals[i - 1];
            return yValm1 + ratio * (yVals[i] -  yValm1);
        }
    }

    public String toString() {
        StringBuilder bldr = new StringBuilder("PiecewiseFunction: "); //NON-NLS
        for (int i=0; i< xValues.length; i++) {
            bldr.append("x=").append(xValues[i]).append(" y=").append(yValues[i]);
        }
        return bldr.toString();
    }
}
