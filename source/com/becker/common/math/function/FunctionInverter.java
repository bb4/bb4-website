// Copyright by Barry G. Becker, 2011. Licensed under MIT License: http://www.opensource.org/licenses/MIT
package com.becker.common.math.function;

import com.becker.common.math.MathUtil;
import com.becker.common.math.Range;


/**
 * Use to find the invers of a given function
 * @author Barry Becker
 */
public class FunctionInverter {
    
    private static final double EPS_BIG = .1;
    private double[] func;

    /**
     * 
     * @param function the range values of the function to invert assuming domain is [0,1]
     */
    public FunctionInverter(double[] function) {
        func = function;
    }
        
    /**
     * Creates an inverse of the function specified
     * assuming that function func is monotonic and maps [xRange] into [yRange]
     * @param xRange the extent of the domain
     * @return inverse error function for specified range
     */
    public double[] createInverseFunction(Range xRange) {
        int len = func.length;
        int lenm1 = len - 1;

        double[] invFunc = new double[len];
        int j = 0;
        double xMax = xRange.getMax();
        assert (func[lenm1] == 1.0) : func[lenm1] + " was not = 1.0";
        for (int i=0; i<len; i++) {     
            double xval = (double)i/lenm1;
            while (j<lenm1 && func[j] <= xval) {
                j++;
            }
            assert (xval<=func[j]+ MathUtil.EPS): xval + " was not less than " + func[j] 
                    +". That means the function was not monotonic as we assumed.";
            invFunc[i] = xRange.getMin(); 
            if (j > 0)
            {
                double fm1 = func[j-1];
                assert(xval>=fm1);
                double denom = func[j] - fm1;
                double nume = xval - fm1;
                assert denom >=0;
                if (denom == 0) {
                    assert nume == 0;
                    denom = 1.0;
                }
                double y = ((double)(j-1) + nume/denom) / (double)lenm1;
                //System.out.println("i="+i+" j=" + j +"  func[j]="+ func[j]
                // +" nume=" + nume + " denom="+denom +" lenm1=" + lenm1 + " y="+y + " xval="+xval);
                invFunc[i] = xRange.getMin() + y * xRange.getExtent();
                assert (invFunc[i] < xMax + EPS_BIG): invFunc[i] + " was not less than " + xMax;
            }
        }
        assert (invFunc[lenm1] > xMax - EPS_BIG): invFunc[lenm1] + " was not greater than " + xMax;
        invFunc[lenm1] = xMax;
        
        //System.out.println("inverse fun=" + Arrays.toString(invFunc));
        return invFunc;
    }
}
