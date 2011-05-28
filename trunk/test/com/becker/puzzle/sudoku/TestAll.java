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

        return suite;
    }
}