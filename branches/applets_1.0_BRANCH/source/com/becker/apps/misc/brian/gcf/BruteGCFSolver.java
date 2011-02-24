package com.becker.apps.misc.brian.gcf;

/**
 * Find the GCF by brute force approach.
 * This is <i>much</i> slower than Euclid's algorithm
 */
public final class BruteGCFSolver implements GCFSolver {

    public long findSolution(long a, long b) {

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
}
