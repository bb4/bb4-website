// Copyright by Barry G. Becker, 2016. Licensed under MIT License: http://www.opensource.org/licenses/MIT
package com.barrybecker4.apps.misc.codeforces.problem584A;


/**
 * @author Barry Becker
 */
public class Problem584A implements ProblemSolver {

    @Override
    public String findSolution(int n, int t) {
        if ((n < 1) || (n > 100)) {
            throw new IllegalArgumentException(n + " isn't between 1 and 100, inclusive");
        }
        if ((t < 2) || (t > 10)) {
            throw new IllegalArgumentException(t + " isn't between 2 and 10, inclusive");
        }
        long fromlength = (long) Math.pow(10, n - 1);
        long tolength = (long) Math.pow(10, n);
        for (long candidate = fromlength; candidate < tolength; candidate++) {
            if (candidate % t == 0) {
                return Long.toString(candidate);
            }
        }
        return "-1";
    }

    public static void main(String args[]) {
        System.out.println("hi");
    }
}
