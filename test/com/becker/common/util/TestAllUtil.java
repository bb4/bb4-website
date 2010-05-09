package com.becker.common.util;

import junit.framework.Test;
import junit.framework.TestSuite;

/**
 * Master test suire to test all aspects of my go program.
 *
 * @author Barry Becker
 */
public class TestAllUtil  {


    public static Test suite() {

        TestSuite suite =  new TestSuite("All Util Tests");

        suite.addTestSuite(MathUtilTest.class);

        return suite;
    }
}