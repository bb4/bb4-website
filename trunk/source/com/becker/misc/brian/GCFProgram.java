package com.becker.misc.brian;




/**
 * Brian's Second Program
 */

public final class GCFProgram  {




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


    private static long euclidGCF(long a, long b) {
        if (a == 0) {
            return b;
        } else {
            return euclidGCF(b % a, a);
        }

    }

    public static void main( String[] args ) {

        long a = 23423423454L;
        long b = 4567876976786L;

        long time = System.currentTimeMillis();
        long answer = bruteGCF(a, b);
        System.out.println("brute answer =" + answer + "  time="+ (System.currentTimeMillis() - time) );

        time = System.currentTimeMillis();
        answer = euclidGCF(a, b);
        System.out.println("brute answer =" + answer + "  time="+ (System.currentTimeMillis() - time) );


        System.out.println("DONE!");

    }
}
