package com.becker.optimization.test;

import junit.framework.*;


/**
 * Master test suire to test all aspects the optimization package.
 *
 * @author Barry Becker
 */

public class TestAll extends TestCase {



    public static Test suite() {
        TestSuite suite =  new TestSuite("All Optimization Strategy Tests");


        //suite.addTest(TestAnalyticFunctionProblem.suite());
        suite.addTestSuite(TestAnalyticFunctionProblem.class);
        suite.addTestSuite(TestSevenElevenProblem.class);


        return suite;
    }
}