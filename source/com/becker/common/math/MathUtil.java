package com.becker.common.math;

import java.util.Arrays;


/**
 * Some supplemental mathematics routines.
 * Static util class.
 *
 * @author Barry Becker Date: Jan 7, 2006
 */
public final class MathUtil {

    public static final double EPS = 0.000000000000000001;
    
    public static final double EPS_MEDIUM = 0.0000000001;
    
    public static final double EPS_BIG = 0.26;

    private MathUtil() {}

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
     * @param a
     * @param b
     * @return the least common multiple of a and b
     */
    public static long lcm(long a, long b) {
        return Math.abs(a * b) / gcd(a, b);
    }


    /**
     * @param values
     * @return the least common multiple of values[0], values[1],... values[i].
     */
    public static long lcm(int[] values) {

        long result = 1;
        for (final int v : values) {
            result = lcm(result, v);
        }
        return result;
    }
    
    /**
     * Creates an inverse of the function specified
     * assuming that function func is monotonic and maps [xRange] into [yRange]
     * @param func
     * @param xRange
     * @return inverse error functin for specified range
     */
    public static double[] createInverseFunction(double[] func, Range xRange) {
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


    /**
     * factorial function.
     * 0! = 1 (http://www.zero-factorial.com/whatis.html)
     * This could be a recursive function, but it would be slow and run out of memory for large num.
     * @param num   number to take factorial of
     * @return  num!
     */
    public static long factorial(int num) {
        assert num >=0;
        if (num == 0) return 1;
        int ct = num;
        long f = 1;
        while (ct > 1) {
            f *= ct;
            ct--;
        }
        return f;
    }
}