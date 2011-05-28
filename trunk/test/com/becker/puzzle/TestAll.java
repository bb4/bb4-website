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


        return suite;
    }
}