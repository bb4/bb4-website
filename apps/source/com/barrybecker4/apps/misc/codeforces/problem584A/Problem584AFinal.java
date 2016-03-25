// Copyright by Barry G. Becker, 2016. Licensed under MIT License: http://www.opensource.org/licenses/MIT
package com.barrybecker4.apps.misc.codeforces.problem584A;


import java.math.BigInteger;
import java.util.Scanner;

/**
 * @author Barry Becker
 */
public class Problem584AFinal /*implements ProblemSolver*/ {

    //@Override
    public String findSolution(int n, int t) {
        if ((n < 1) || (n > 100)) {
            throw new IllegalArgumentException(n + " isn't between 1 and 100, inclusive");
        }
        if ((t < 2) || (t > 10)) {
            throw new IllegalArgumentException(t + " isn't between 2 and 10, inclusive");
        }
        BigInteger startNum = BigInteger.TEN.pow(n - 1);
        BigInteger stopNum = BigInteger.TEN.pow(n);
        BigInteger theT = new BigInteger(Integer.toString(t));

        for (BigInteger candidate = startNum;
             stopNum.compareTo(candidate) > 0;
             candidate = candidate.add(BigInteger.ONE)) {
            if (candidate.mod(theT).compareTo(BigInteger.ZERO) == 0) {
                return candidate.toString();
            }
        }
        return "-1";
    }

    public static void main(String[] args) {

        Problem584AFinal solver = new Problem584AFinal();

        Scanner in = new Scanner(System.in);
        int n = in.nextInt();
        int t = in.nextInt();

        System.out.println(solver.findSolution(n, t));
    }
}
