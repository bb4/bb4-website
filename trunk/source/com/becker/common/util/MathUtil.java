package com.becker.common.util;

import com.becker.common.Range;


/**
 * Some supplemental mathematics routines.
 * Static util class.
 *
 * @author Barry Becker Date: Jan 7, 2006
 */
public final class MathUtil {

    public static final double EPS = 0.0000000000001;
    
    public static final double BIG_EPS = 0.03;

    private MathUtil() {};

    /**
     * @return the greatest common divisor of 2 longs (may be negative).
     */
    public static long gcd(long a, long b) {

        if (a == 0 && b == 0) return 1;
        if (a < 0) return gcd(-a, b);
        if (b < 0) return gcd(a, -b);
        if (a == 0) return b;
        if (b == 0) return a;
        if (a == b) return a;
        if (b < a) return gcd(b, a);

        return gcd(a, b % a);
    }


    /**
     *
     * @param a
     * @param b
     * @return the least common multiple of a and b
     */
    public static long lcm(long a, long b) {
        return Math.abs(a * b) / gcd(a, b);
    }



    public static long lcm(int[] values) {

        long result = 1;
        for (final int v : values) {
            result = lcm(result, v);
        }
        return result;
    }
    
    /**
     * Linearly interpolate between 2 values in the function that is defined as a double array.
     * @param value x value in range [0,1] to determine y value for using specified function.
     * @param function the function to linearly interpolate.
     * @return interpolated value.
     */
    public static double linearlyInterpolate(double value, double[] function) {
        // now use linear interpolation beteen values
        int len =  function.length-1;
        double x = value *(double) len;
        
        int index0 = (int) x;
        assert(index0 < len);
        int index1 = index0 + 1;
        double xdiff = x - index0;
        assert  (index0 >= 0) : "index0 must be greater than 0, but was "+index0;
        assert(index1 <= len) : "index1 must be less than the size of the array, but was "+index1;
        //System.out.println("xdif="+ xdiff + " f0="+ function[ index0 ]  +" f1="+function[ index1 ] );
        return (1.0 - xdiff) * function[ index0 ] +  xdiff * function[ index1 ];
    }
    
    /**
     * @@ something flawed in this method. Need to fix!
     * Use cubic interpolation between 2 values in the function that is defined as a double array.
     * @param value x value to determine y value for using specified function
     * @param function the function to linearly interpolate.
     * @return interpolated value.
     */
    public static double cubicInterpolate(double value, double[] function) {
        // now use linear interpolation beteen values
        int len =  function.length-1;
        double x = value *(double) len;       
        int index0 = (int) x;
        int index1 = index0 + 1;        
        double xdiff = x - index0;
       
         // we need to come up with the 4 points to use for interpolation        
        double y1 =  function[ index0 ];
        double y0 = y1;
        double y2 =  function[ index1 ];
        double y3 = y2;
        if (index0 > 0) {
            y0 = function[index0-1];
        }
        if (index1 < len) {
            y3 = function[index1+1];
        }
        
        double xdiff2 = xdiff * xdiff;
        double mxdiff = 1.0-xdiff;
        double mxdiff2 = mxdiff * mxdiff;
        return y0 * mxdiff * mxdiff2 + 3.0 * mxdiff2 *xdiff * y1 + 3.0 *  mxdiff *xdiff2 * y2 + xdiff * xdiff2 * y3;
    }
    
   private static final double MAX_ERROR_FUNCTION_TABLE_VALUE = 3.4;
    /**
     * The gaussian error function table. 
     * See http://eceweb.uccs.edu/Wickert/ece3610/lecture_notes/erf_tables.pdf
     * for values of x  = 0.0, 0.1, ... 3.4
     */
    private static final double[] ERROR_FUNCTION = {
          0.000000,   0.1124629, 0.2227026, 0.3286268, 0.4283924, 0.5204999, 0.6038561, 0.6778012, 0.7421008, 0.7969081, 0.8427007,
          0.883533,   0.910314,   0.9340079, 0.9522851, 0.9661051, 0.9763484, 0.9837905, 0.9890905, 0.9927904, 0.9953223, 
          0.9970205, 0.9981372, 0.9988568, 0.9990646, 0.9993115, 0.9995901, 0.99976,     0.99987,     0.99992,     0.99996, 
          0.99998,     0.99999,     0.999993,   1.0000
    };
    
     /**
      * We expect x to be in the range approximately 0.0, 4.0.
      * Values outside of 3.3 to 3.3 are 
      * Currently just using simple linear interpolation.
      * We could improve by using quadratic interpolation.
      * @param x
      */
    public static double errorFunction(double x) {
       
        double sign = (x>=0)? 1.0:-1.0;
        if (Math.abs(x) >  MAX_ERROR_FUNCTION_TABLE_VALUE) {
            return sign;
        }        
        return sign * linearlyInterpolate((Math.abs(x) / MAX_ERROR_FUNCTION_TABLE_VALUE), ERROR_FUNCTION);
    }
    
     /** for values of x  = 0.0, 0.1, ... 1.0 */
    private static final double[] INVERSE_ERROR_FUNCTION = {
        0.0,   0.089,   0.18,  0.28,   0.379,   0.479,   0.596,   0.738,   0.91,   1.161,   3.28
    };
    
    /**
     * We expect x to be in the range [-1.0, 1.0].
     * Currently just using simple linear interpolation.
     * We could improve by using quadratic interpolation.
     * @param x
     */
    public static double inverseErrorFunction(double x) {
        assert (x>=-1.0 && x<=1.0);
        double sign = (x>=0)? 1.0:-1.0;
        return sign *linearlyInterpolate(Math.abs(x), INVERSE_ERROR_FUNCTION);
    }
    
    /**
     * Creates an inverse of the function specified
     * assuming that function func is monotonic and maps [xRange] into [yRange]
     * @param func
     * @param xRange 
     */
    public static double[] createInverseFunction(double[] func, Range xRange) {
        int len = func.length;
        int lenm1 = len -1;
        
        double[] invFunc = new double[len];
        int j = 0;
        double xMax = xRange.getMax();
        for (int i=0; i<len; i++) {     
            double xval = (double)i/lenm1;
            while (j<lenm1 && func[j] <= xval) {
                j++;
            }
            assert (xval<=func[j]+EPS): xval + " was not less than " + func[j] 
                    +". That means the function was not monotonic as we assumed.";
            invFunc[i] = xRange.getMin(); 
            if (j>0)
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
                invFunc[i] = xRange.getMin() + (((double)j + nume/denom)/ lenm1) * xRange.getExtent();
                assert (invFunc[i]<xMax + BIG_EPS): invFunc[i] + " was not less than " + xMax; 
                if (invFunc[i] > xMax) {
                    invFunc[i] = xMax;
                }
            }
        }
        return invFunc;
    }
}
