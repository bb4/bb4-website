package com.becker.puzzle.sudoku.test;

import junit.framework.*;
import com.becker.puzzle.sudoku.*;

/**
 * @author Barry Becker Date: Jul 3, 2006
 */
public class TestSudokuSolver extends TestCase {

    public void testCase1() {

        SudokuSolver solver = new SudokuSolver();
        boolean solved = solver.solvePuzzle(new Board(Data.SAMPLE1));

        Assert.assertTrue( "Did not solve SAMPLE1 successfully", solved);
    }

    public void testNegativeCase1() {

        SudokuSolver solver = new SudokuSolver();
        boolean solved = solver.solvePuzzle(new Board(Data.SAMPLE2));

        Assert.assertFalse( "Solved impossible SAMPLE1 puzzle. Should not have.", solved);
    }


    public void testGenerateAndSolve() {
        // super exponential run time
        generateAndSolve(2, 1);  // 16  cells       32 ms
        generateAndSolve(3, 1);  // 81  cells      265 ms
        generateAndSolve(4, 1);  // 256 cells    2,077 ms
        //generateAndSolve(5, 1);  // 625 cells  687,600 ms
    }

    public void testGenerateLotsAndSolve() {

        for (int r=0; r<60; r++)
            generateAndSolve(3, r);
    }

    public void generateAndSolve(int baseSize, int seed) {
        SudokuGenerator generator = new SudokuGenerator(baseSize, null);
        generator.setRandomSeed(seed);
        long start = System.currentTimeMillis();
        Board b = generator.generatePuzzleBoard();
        System.out.println("Time to generate size="+baseSize +" was "+ (System.currentTimeMillis() - start));

        SudokuSolver solver = new SudokuSolver();
        start = System.currentTimeMillis();
        boolean solved = solver.solvePuzzle(b);
        System.out.println("Time to solve size="+baseSize +" was "+ (System.currentTimeMillis() - start));
        Assert.assertTrue("", solved);
    }

    /**
     * @return all the junit test caes to run (in this class).
     */
    public static Test suite() {
        return new TestSuite(TestSudokuSolver.class);
    }
}
