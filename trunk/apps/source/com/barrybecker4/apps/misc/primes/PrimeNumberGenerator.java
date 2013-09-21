package com.barrybecker4.apps.misc.primes;

/**
 * Finds prime numbers.
 *
 * @author Barry Becker
 */
public class PrimeNumberGenerator {

    private int currentCandidate;

    public PrimeNumberGenerator() {
        currentCandidate = 1;
    }

    /**
     * @return the next computed prime number
     */
    public int getNextPrimeNumber() {
        currentCandidate++;
        if (currentCandidate == 2)
            return 2;

        while (!isPrime(currentCandidate)) {
            currentCandidate++;
        }
        return currentCandidate;
    }


    private boolean isPrime(int num) {

        for (int i=2; i<=Math.sqrt(num); i++) {
            if (num % i == 0) return false;
        }
        return true;
    }
}


