package com.becker.common;


/**
 * Some supplemental mathematics routines.
 * Static util class.
 *
 * @author Barry Becker Date: Jan 7, 2006
 */
public final class MathUtil {


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
     * find the greatest common divisor of 2 positive integers.
     */
    public static int gcd( int x, int y )
    {
        if ( x % y == 0 ) return y;
        return gcd( y, x % y );
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

}
