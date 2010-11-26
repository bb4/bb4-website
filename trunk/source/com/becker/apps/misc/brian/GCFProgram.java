package com.becker.apps.misc.brian;

/**
 * Compare two methods for finding the greatest comon factor.
 * See gcf subpackage for a better way to do this same thing.
 */
public final class GCFProgram  {

    /** can take very long to run */
    private static long bruteGCF(long a, long b) {

        if (a > b) {
            long temp = b;
            b = a;
            a = temp;
        }
        for (long i=a; i > 1; i--) {
            if ((a % i == 0) && (b % i == 0))  {
                return i;
            }
        }
        return 1;
    }

    /** recursive */
    private static long euclidGCF(long a, long b) {
        if (a == 0) {
            return b;
        } else {
            return euclidGCF(b % a, a);
        }
    }


    public static void main( String[] args ) {

        //long a = 23423423454L;
        //long b = 4567876976786L;
        long a = 36618;
        long b = 8105362;

        System.out.println("Finding Greatest Common Factor of a=" + a + " and b="+ b);

        long time = System.currentTimeMillis();
        long answer = bruteGCF(a, b);
        System.out.println("brute answer =" + answer + "  time="+ (System.currentTimeMillis() - time) );

        time = System.currentTimeMillis();
        answer = euclidGCF(a, b);
        System.out.println("brute answer =" + answer + "  time="+ (System.currentTimeMillis() - time) );

        System.out.println("DONE!");
    }
}
