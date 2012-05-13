// Copyright by Barry G. Becker, 2012. Licensed under MIT License: http://www.opensource.org/licenses/MIT
package com.becker.puzzle.tantrix.model;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

/**
 * @author Barry Becker
 */
public class TestAll extends TestCase {

    public static Test suite() {

        TestSuite suite =  new TestSuite("All Tantrix model Tests");

        suite.addTestSuite(TantrixTest.class);
        suite.addTestSuite(TantrixBoardTest.class);
        suite.addTestSuite(BorderFinderTest.class);
        suite.addTestSuite(MoveGeneratorTest.class);

        suite.addTest(com.becker.puzzle.tantrix.model.fitting.TestAll.suite());

        return suite;
    }
}