package com.becker.common.function;


/**
 * Piecewise linear function representation.
 * 
 * @author Barry Becker
 */
public class PiecewiseFunction implements Function {

    /** These parallel arrays define the piecewise function map. */
    protected double[] xValues;
    protected double[] yValues;
    
    
    /**
     * Constructor.
     * @param xVals
     * @param yVals
     */
    public PiecewiseFunction(double[] xVals, double[] yVals ) {
        xValues = xVals;
        yValues = yVals;     
        //for (int i=0; i< xValues.length; i++) {
        //    System.out.println("x="+xValues[i] + " y=" + yValues[i]);
        //}     
        assert xValues.length == yValues.length;
    }
  
    
    /**
     * 
     * @param value
     * @return
     */
    public double getFunctionValue(double value) {
        
        return getValue(value, xValues, yValues); 
    }
    
    /**
     * 
     * @param value
     * @return
     */
    public double getInverseFunctionValue(double value) {
        
        return getValue(value, yValues, xValues); 
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
        double ratio = 0;
        if (denom == 0) {
            return yVals[i - 1];
        } else {
            ratio = (value - xValm1) / denom;
            double yValm1 = yVals[i - 1];
            return yValm1 + ratio * (yVals[i] -  yValm1);
        }
    }
}
