package com.becker.common.util;

import com.becker.common.math.MathUtilTest;
import junit.framework.Test;
import junit.framework.TestSuite;

/**
 * Master test suire to test all aspects of my go program.
 *
 * @author Barry Becker
 */
public class TestAll {


    public static Test suite() {

        TestSuite suite =  new TestSuite("All FormatUtil Tests");

        suite.addTestSuite(MathUtilTest.class);
        suite.addTestSuite(LRUCacheTest.class);

        return suite;
    }
}