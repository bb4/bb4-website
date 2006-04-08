package com.becker.common;

/**
 * Some supplemental mathematics routines
 *
 * @author Barry Becker Date: Jan 7, 2006
 */
public final class MathUtil {


    private MathUtil() {};

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



    public static long lcm(long a, long b) {
        return Math.abs(a * b) / gcd(a, b);
    }



    public static long lcm(int[] values) {

        long result = 1;
        for (int i = 0; i < values.length; i++) {
            result = lcm(result, values[i]);
        }
        return result;
    }

}
