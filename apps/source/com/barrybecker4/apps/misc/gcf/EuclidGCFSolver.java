/** Copyright by Barry G. Becker, 2000-2011. Licensed under MIT License: http://www.opensource.org/licenses/MIT  */
package com.barrybecker4.apps.misc.gcf;

/**
 * Find the GCF by an elegant recursive approach developer by Euclid  over 2300 years ago.
 * See http://en.wikipedia.org/wiki/Euclidean_algorithm
 */
public class EuclidGCFSolver implements GCFSolver {

    public long findSolution(long a, long b) {

        return (a == 0) ? b : findSolution(b % a, a);

    }
}
