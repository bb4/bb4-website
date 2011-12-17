/** Copyright by Barry G. Becker, 2000-2011. Licensed under MIT License: http://www.opensource.org/licenses/MIT  */
package com.becker.common;

import com.becker.common.function.TestAllFunction;
import com.becker.common.interpolation.TestAllInterpolation;
import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

/**
 *
 * @author Barry Becker
 */
public class TestAll extends TestCase {

    public static Test suite() {

        TestSuite suite =  new TestSuite("All Common Tests");

        suite.addTestSuite(CommandLineOptionsTest.class);


        suite.addTest(TestAllFunction.suite());
        suite.addTest(TestAllInterpolation.suite());
        suite.addTest(com.becker.common.math.TestAll.suite());
        suite.addTest(com.becker.common.util.TestAll.suite());

        return suite;
    }
}