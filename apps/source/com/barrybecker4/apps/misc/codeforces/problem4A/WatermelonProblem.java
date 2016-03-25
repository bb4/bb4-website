// Copyright by Barry G. Becker, 2016. Licensed under MIT License: http://www.opensource.org/licenses/MIT
package com.barrybecker4.apps.misc.codeforces.problem4A;

import java.math.BigInteger;
import java.util.Scanner;



import java.math.BigInteger;
import java.util.Scanner;

/**
 * @author Barry Becker
 */
public class WatermelonProblem {

    //@Override
    public boolean findSolution(int n) {
        return ((n > 2) && ((n - 2) % 2 == 0));
    }

    public static void main(String[] args) {

        WatermelonProblem solver = new WatermelonProblem();

        Scanner in = new Scanner(System.in);
        int n = in.nextInt();


        String result = solver.findSolution(n) ? "YES" : "NO";
        System.out.println(result);
    }
}
