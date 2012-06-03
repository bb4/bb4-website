// Copyright by Barry G. Becker, 2012. Licensed under MIT License: http://www.opensource.org/licenses/MIT
package com.becker.puzzle.tantrix.solver.path.permuting;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

/**
 * @author Barry Becker
 */
public class TestAll extends TestCase {

    public static Test suite() {

        TestSuite suite =  new TestSuite("All Tantrix path permuter Tests");

        suite.addTestSuite(PathPermuterTest.class);
        suite.addTestSuite(SubPathReverserTest.class);
        suite.addTestSuite(SubPathSwapperTest.class);

        //suite.addTest(com.becker.puzzle.tantrix.solver.path.permuting.TestAll.suite());

        return suite;
    }
}