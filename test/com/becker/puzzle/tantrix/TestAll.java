// Copyright by Barry G. Becker, 2012. Licensed under MIT License: http://www.opensource.org/licenses/MIT
package com.becker.puzzle.tantrix;

import com.becker.puzzle.sudoku.TestSudokuGenerator;
import com.becker.puzzle.sudoku.TestSudokuSolver;
import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

/**
 * @author Barry Becker
 */
public class TestAll extends TestCase {

    public static Test suite() {

        TestSuite suite =  new TestSuite("All Tantrix Tests");

        //suite.addTestSuite(TestSudokuSolver.class);
        //suite.addTestSuite(TestSudokuGenerator.class);

        suite.addTest(com.becker.puzzle.tantrix.model.TestAll.suite());

        return suite;
    }
}