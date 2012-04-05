/** Copyright by Barry G. Becker, 2000-2011. Licensed under MIT License: http://www.opensource.org/licenses/MIT  */
package com.becker.puzzle;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

/**
 *
 * @author Barry Becker
 */
public class TestAll extends TestCase {

    public static Test suite() {

        TestSuite suite =  new TestSuite("All Puzzle Tests");

        suite.addTest(com.becker.puzzle.sudoku.TestAll.suite());
        suite.addTest(com.becker.puzzle.tantrix.TestAll.suite());


        return suite;
    }
}