/** Copyright by Barry G. Becker, 2000-2011. Licensed under MIT License: http://www.opensource.org/licenses/MIT  */
package com.becker.common.function;

import junit.framework.Test;
import junit.framework.TestSuite;

/**
 * @author Barry Becker
 */
public class TestAllFunction {

    private TestAllFunction() {}

    public static Test suite() {

        TestSuite suite =  new TestSuite("All Function Tests");

        suite.addTestSuite(ErrorFunctionTest.class);

        return suite;
    }
}