// Copyright by Barry G. Becker, 2016. Licensed under MIT License: http://www.opensource.org/licenses/MIT
package com.barrybecker4.apps.misc.codeforces.problem584A;

import java.math.BigInteger;

/**
 * @author Barry Becker
 */
public class Tester {

   /** See requirements at http://codeforces.com/contest/584/problem/A */
   private boolean validResult(String result, int places, int divisor) {
       int numDigits = result.length();
       BigInteger bigResult = new BigInteger(result);
       BigInteger bigDivisor = BigInteger.valueOf((long) divisor);
       boolean divisible = bigResult.mod(bigDivisor).compareTo(BigInteger.ZERO) == 0;
       if (Integer.toString(divisor).length() > places) {
           return result.equals("-1");
       }
       return numDigits == places && divisible;
   }

   /** Tests http://codeforces.com/contest/584/problem/A */
   private void testCase(int places, int divisor, ProblemSolver solver) {
       try {
           String result = solver.findSolution(places, divisor);
           if (validResult(result, places, divisor)) {
               System.out.println("success");
           }
           else {
               System.out.println("Fail! result " + result + " did not meet the requirements "
                       + " for solve(" + places + ", " +divisor + ")");
           }
       }
       catch (IllegalArgumentException e) {
           System.out.println("Encountered IllegalArgumentException :" + e.getLocalizedMessage());
       }
    }

    public static void main(String[] args) {
        Tester tester = new Tester();
        ProblemSolver solver = new Problem584A();
        tester.testCase(1, 2, solver);
        tester.testCase(1, 3, solver);
        tester.testCase(1, 10, solver);    // no solution possible for this!
        tester.testCase(2, 2, solver);
        tester.testCase(2, 10, solver);
        tester.testCase(2, 9, solver);
        tester.testCase(3, 4, solver);
        tester.testCase(4, 5, solver);
        tester.testCase(5, 7, solver);
        tester.testCase(9, 3, solver);
        tester.testCase(10, 4, solver);
        tester.testCase(11, 4, solver);
        tester.testCase(12, 4, solver);
        tester.testCase(14, 4, solver);
        tester.testCase(16, 4, solver);
        tester.testCase(20, 5, solver);   // big number from here on
        tester.testCase(30, 2, solver);
        tester.testCase(40, 7, solver);
        tester.testCase(50, 8, solver);
        tester.testCase(60, 9, solver);
        tester.testCase(70, 9, solver);
        tester.testCase(80, 9, solver);
        tester.testCase(90, 9, solver);
        tester.testCase(90, 10, solver);
        tester.testCase(100, 2, solver);
        tester.testCase(100, 3, solver);
        tester.testCase(100, 7, solver);
        tester.testCase(100, 9, solver);
        tester.testCase(100, 10, solver);
        System.out.println("Done testing.");
    }
}
