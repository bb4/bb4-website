package com.becker.apps.misc.brian.gcf;

/**
 * Find the GCF by an elegant recursive approach developer by Euclid  over 2300 years ago.
 * See http://en.wikipedia.org/wiki/Euclidean_algorithm
 */
public class EuclidGCFSolver implements GCFSolver {

    public long findSolution(long a, long b) {
        if (a == 0) {
            return b;
        } else {
            return findSolution(b % a, a);
        }
    }
}
