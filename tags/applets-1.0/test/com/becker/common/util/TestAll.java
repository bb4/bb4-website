/** Copyright by Barry G. Becker, 2000-2011. Licensed under MIT License: http://www.opensource.org/licenses/MIT  */
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
        suite.addTestSuite(OrderedMapTest.class);
        suite.addTestSuite(LRUCacheTest.class);

        return suite;
    }
}