package com.barrybecker4.apps.misc.primes;

/**
 * Finds prime numbers.
 *
 * @author Barry Becker
 */
public class PrimeNumberGenerator {

    private long currentCandidate;


    public PrimeNumberGenerator(long startingNumber) {
        currentCandidate = startingNumber;
    }

    public PrimeNumberGenerator() {
        this(1);
    }

    /**
     * @return the next computed prime number
     */
    public long getNextPrimeNumber() {
        currentCandidate++;
        if (currentCandidate == 2)
            return 2;

        while (!PrimeNumberUtil.isPrime(currentCandidate)) {
            currentCandidate++;
        }
        return currentCandidate;
    }


}


