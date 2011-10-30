/** Copyright by Barry G. Becker, 2000-2011. Licensed under MIT License: http://www.opensource.org/licenses/MIT  */
package com.becker.puzzle.sudoku;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

/**
 *
 * @author Barry Becker
 */
public class TestAll extends TestCase {

    public static Test suite() {

        TestSuite suite =  new TestSuite("All Sudoku Tests");

        suite.addTestSuite(TestSudokuSolver.class);
        suite.addTestSuite(TestSudokuGenerator.class);

        suite.addTest(com.becker.puzzle.sudoku.model.TestAll.suite());

        return suite;
    }
}