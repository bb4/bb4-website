package com.becker.common.util;

import com.becker.common.Range;
import java.util.Arrays;


/**
 * Some supplemental mathematics routines.
 * Static util class.
 *
 * @author Barry Becker Date: Jan 7, 2006
 */
public final class MathUtil {

    public static final double EPS = 0.000000000000000001;
    
    public static final double EPS_MEDIUM = 0.00000000001;
    
    public static final double EPS_BIG = 0.26;

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
        assert(x <= len) : index0 +" is >= " + len + ". x="+x;
        int index1 = index0 + 1;
        if (index0 == len) index1 = len; // I think this should never happen
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
    
   private static final double MAX_ERROR_FUNCTION_TABLE_VALUE = 5.3;
   
    /**
     * The gaussian error function table. 
     * See http://eceweb.uccs.edu/Wickert/ece3610/lecture_notes/erf_tables.pdf
     * for values of x  = 0.0, 0.1, ... MAX_ERROR_FUNCTION_TABLE_VALUE
     * @@ try plotting this in log scale.
     */
    private static final double[] ERROR_FUNCTION = {
          0.0000000,   0.0563721,   0.11246296,  0.16800,   0.2227026,  0.27633,    0.3286268,    0.37938,    0.4283924,   0.47548,     0.5204999,    0.56332,     0.6038561,    0.64203,    0.6778012,     0.71116,        0.7421008,       0.77067,        0.7969081,       0.82089, 
          0.8427007,   0.86244,   0.883533,    0.89612,   0.910314,    0.92290,    0.9340079,    0.94376,    0.9522851,   0.95970,     0.9661051,    0.97162,     0.9763484,    0.98038,    0.9837905,     0.98667,        0.9890905,       0.99111,        0.9927904,       0.99418, 
          0.99532213,   0.996258,   0.9970205,  0.99764,   0.9981372,  0.99854,    0.9988568,    0.99911,    0.9993115,   0.99947,     0.9995901,    0.99969,     0.99976,        0.99982,    0.99987,         0.9999,          0.99992,           0.99994,        0.99995887,         0.99996977878,
          0.999977894,   0.999983, 0.999988,    0.999991, 0.9999931,  0.9999957, 0.9999975,  0.9999980, 0.9999984,  0.99999870,   0.99999930, 0.99999970,  0.999999840,  0.999999901, 0.9999999350,   0.999999860,  0.999999910,     0.999999931,  0.999999955,    0.999999971, 
          //    4.0                      4.05                4.1                          4.15                      4.2                       4.25                         4.3                        4.35                      4.4                           4.45                          4.5                                  4.55                              4.6                                4.65                           4.7                                   4.75                            4.8                              4.85                                4.9                                  4.95
          0.99999998453,   0.999999988, 0.999999993279,  0.999999995601, 0.99999999713,  0.99999999814275, 0.999999998802, 0.999999999231, 0.9999999995088,  0.99999999968776,  0.9999999998024914, 0.999999999875673,  0.9999999999221209,  0.999999999951454, 0.9999999999698866,   0.9999999999814,  0.9999999999885819,   0.9999999999930206,   0.9999999999957546,    0.9999999999974303, 
          0.99999999999845,  0.9999999999988,   0.99999999999945,   0.9999999999996,   0.999999999999806,  0.9999999999996,  0.99999999999994                                                                   
    };
    
     /**
      * We expect x to be in the range approximately 0.0, 5.0.
      * Values outside of -MAX_ERROR_FUNCTION_TABLE_VALUE to MAX_ERROR_FUNCTION_TABLE_VALUE are 
      * Currently just using simple linear interpolation.
      * We could improve by using quadratic interpolation.
      * @param x
      */
    public static double errorFunction(double x) {
       
        double sign = (x>=0)? 1.0:-1.0;
        if (Math.abs(x) >  MAX_ERROR_FUNCTION_TABLE_VALUE) {
            
            double v = (1.0 -  (100.0 - Math.abs(x) )* EPS_MEDIUM);
            if (x>50) {
                System.out.println("erf("+x+")="+ v);
            }
            assert v > 0.0 : " x="+ x + " v="+ v;
            return sign *v;
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
        int lenm1 = len - 1;
        //System.out.println("orig fun=" +Arrays.toString(func));
        double[] invFunc = new double[len];
        int j = 0;
        double xMax = xRange.getMax();
        assert (func[lenm1] == 1.0) : func[lenm1] + " was not = 1.0";
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
                double y = (((double)(j-1) + nume/denom)/ (double)lenm1);
                //System.out.println("i="+i+" j=" + j +"  func[j]="+ func[j]  +" nume=" + nume + " denom="+denom +" lenm1=" + lenm1 + " y="+y + " xval="+xval);
                invFunc[i] = xRange.getMin() + y * xRange.getExtent();
                assert (invFunc[i]<xMax + EPS_BIG): invFunc[i] + " was not less than " + xMax;                 
            }
        }
        assert (invFunc[lenm1]>xMax - EPS_BIG): invFunc[lenm1] + " was not greater than " + xMax; 
        invFunc[lenm1] = xMax;
        
        System.out.println("inverse fun=" +Arrays.toString(invFunc));
        return invFunc;
    }
}
