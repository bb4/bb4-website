// Copyright by Barry G. Becker, 2014. Licensed under MIT License: http://www.opensource.org/licenses/MIT
package com.barrybecker4.apps.misc.primes;

/**
 * Some static methods for dealing with prime numbers.
 * The isPrimeUnder1B method is from an article by Mike Fink in QL Hackers Journal.
 */
public class PrimeNumberUtil {

    private static long[] LOW_PRIMES = new long[] {
            7, 11, 13, 17, 19, 23, 29, 31, 37, 41, 43, 47, 49,
            53, 59, 61, 67, 71, 73, 77, 79, 83, 89, 91
    };

    /**
     * @param num the number to check primality of
     * @return true of num is prime, else false.
     */
    public static boolean isPrime(long num) {

        if (num == 2) return true;
        if (num % 2 == 0) return false;

        for (long i=3; i <= Math.sqrt(num); i+=2) {
            if (num % i == 0) return false;
        }
        return true;
    }


    /**
     * Prime Numbers - Mike Fink
     * This program ascertains the primality of any
     * number less than a billion.
     * (C) 1988 by Mike Fink with help from Robert Fink
     * @param num must be under 1 billion, or all bets are off.
     * @return true if under 1 billion and prime.
     */
    public static boolean isPrimeUnder1B(long num) {

        if (num == 2 || num == 3 || num == 5)  {
            return true;
        }
        if (num % 2 == 0 || num % 3 == 0 || num % 5 == 0) {
            return false;
        }

        long sqrt = (long) Math.sqrt(num);

        long c = 0;
        while (true)  {
            for (int index = 0; index < 24; index++) {
                long v1 = LOW_PRIMES[index] + 90 * c;
                if (v1 > sqrt) {
                    return true;
                }
                if (num % v1 == 0) {
                    return false;
                }
            }
            c++;
        }
    }

    private PrimeNumberUtil() {}
}
