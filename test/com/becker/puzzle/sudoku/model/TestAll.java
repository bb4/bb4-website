package com.becker.puzzle.sudoku.model;

import com.becker.puzzle.sudoku.TestSudokuSolver;
import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

/**
 *
 * @author Barry Becker
 */
public class TestAll extends TestCase {

    public static Test suite() {

        TestSuite suite =  new TestSuite("All Sudoku model Tests");

        suite.addTestSuite(TestBoard.class);
        suite.addTestSuite(TestCell.class);
        suite.addTestSuite(TestBoardUpdater.class);

        return suite;
    }
}