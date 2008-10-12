/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package com.becker.optimization.parameter.redistribution;

import com.becker.common.Range;
import com.becker.common.function.ArrayFunction;
import com.becker.common.util.MathUtil;

/**
 * Convert the uniform distribution to a normal (gaussian) one.
 * @author Barry Becker
 */
public class GaussianRedistribution extends AbstractRedistributionFunction {

    private double mean;
    private double stdDeviation;
    
    private static final int NUM_MAP_VALUES = 1000;
    private static final double SQRT2 = Math.sqrt(2.0);
    
    
    public GaussianRedistribution(double mean, double standardDeviation) {
        verifyInRange(mean);
        this.mean = mean;
        this.stdDeviation = standardDeviation;
         initializeFunction();       
    }
    
    protected void initializeFunction() {
        double[]  functionMap = new double[ NUM_MAP_VALUES];
        double inc = 1.0 /  (NUM_MAP_VALUES-1);
        int index = 1;
        double[] cdfFunction = new double[NUM_MAP_VALUES];
        cdfFunction[index] = 0;
        for (double x = inc; x<=1.0 + MathUtil.EPS; x+= inc) {
            double v = cdf(x);
            cdfFunction[index++] = v;         
        }  
        double lowMissing = cdf(0);
        double highMissing = 1.0 - cdfFunction[NUM_MAP_VALUES-1];
 
        System.out.println("lowMissing=" + lowMissing + " highMissing="+highMissing);
        assert (index == NUM_MAP_VALUES );
        // reallocate the part that is missing.
        int numMapValsm1 = NUM_MAP_VALUES-1;
        for (int i=1; i<NUM_MAP_VALUES; i++) {
            double aliasAllocation =  -lowMissing * (double)(numMapValsm1-i)/numMapValsm1 +  highMissing * (double)i/numMapValsm1;
            cdfFunction[i] += aliasAllocation;
        }
        Range xRange = new Range(0.0, 1.0);
        functionMap = 
                 MathUtil.createInverseFunction(cdfFunction, xRange);
        //for (int i=0; i<NUM_MAP_VALUES; i++) {
        //    System.out.println(i+"))  " + functionMap[i]);
        //}
        redistributionFunction = new ArrayFunction(functionMap);
    }
    
    /**
     * 1/2 (1 + erf((x-mean)/(SQRT2 *stdDeviation))
     * @param x
     * @return
     */
    private double cdf(double x) {
        
        double denom = SQRT2 * stdDeviation;
        double xx = (x - mean) / denom;
        double v =  0.5 * (1.0 + MathUtil.errorFunction(xx));
        return v;      
    }

    public static void main( String[] args )
    {
        RedistributionFunction f = new GaussianRedistribution(0.5, 10.0);
    }
    
}
