package com.becker.puzzle.sudoku.test;

import junit.framework.*;
import com.becker.puzzle.sudoku.*;

/**
 * @author Barry Becker Date: Jul 3, 2006
 */
public class TestSudokuSolver extends TestCase {

    public void testCase1() {


        PuzzleSolver solver = new PuzzleSolver();

        boolean solved = solver.solvePuzzle(new Board(Data.SAMPLE1));

         Assert.assertTrue( "Did not solve SAMPLE1 successfully", solved);
    }




    /**
     * @return all the junit test caes to run (in this class).
     */
    public static Test suite() {
        return new TestSuite(TestSudokuSolver.class);
    }
}
