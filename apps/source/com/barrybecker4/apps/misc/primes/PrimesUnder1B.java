// Copyright by Barry G. Becker, 2014. Licensed under MIT License: http://www.opensource.org/licenses/MIT
package com.barrybecker4.apps.misc.primes;

/**
 * Check that the algorithm proposed by Mike Fink and Tim Swenson actually
 * works as advertised. In other words, does it accurately determine the
 * primality of integers under one billion.
 * @author Barry Becker
 */
public class PrimesUnder1B {


    public static void main(String[] args) {

        int numErrors = 0;
        // checked everything < 200,000,000 and between 900,000,000 and 1,000,000,000 but then I got tired of waiting.
        for (int i = 1; i < 1000000000; i++) {
            boolean isPrime = PrimeNumberUtil.isPrime(i);
            boolean isPrimeUnder1b = PrimeNumberUtil.isPrimeUnder1B(i);

            if (isPrime != isPrimeUnder1b) {
                System.out.println("The Fink algorithm said "+ i);
                if (isPrime) {
                    System.out.println(" is not prime when it actually is.");
                }
                else {
                    System.out.println(" is prime when it actually is not.");
                }
            }
        }

        System.out.println("There were " + numErrors +" errors.");
    }
}
