// Copyright by Barry G. Becker, 2012. Licensed under MIT License: http://www.opensource.org/licenses/MIT
package com.becker.puzzle.tantrix.solver;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

/**
 * @author Barry Becker
 */
public class TestAll extends TestCase {

    public static Test suite() {

        TestSuite suite =  new TestSuite("All Tantrix solver Tests");

        //suite.addTestSuite(PathEvaluatorTest.class);
        suite.addTest(com.becker.puzzle.tantrix.solver.path.TestAll.suite());

        return suite;
    }
}