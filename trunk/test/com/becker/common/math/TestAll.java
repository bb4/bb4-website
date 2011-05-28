package com.becker.common.math;

import junit.framework.Test;
import junit.framework.TestSuite;

/**
 * Master test suire to test all aspects of my go program.
 *
 * @author Barry Becker
 */
public class TestAll {


    public static Test suite() {

        TestSuite suite =  new TestSuite("All MAth Tests");

        suite.addTestSuite(MathUtilTest.class);
        suite.addTestSuite(NiceNumbersTest.class);

        return suite;
    }
}