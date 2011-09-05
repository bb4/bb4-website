package com.becker.puzzle.sudoku;

import com.becker.puzzle.sudoku.data.TestData;
import com.becker.puzzle.sudoku.model.Board;
import junit.framework.Assert;
import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

/**
 * @author Barry Becker
 */
public class TestSudokuGenerator extends TestCase {

    /** instance under test. */
    SudokuGenerator generator;


    public void testGenerateLotsAndSolve() {

        for (int r=0; r<60; r++)
            generateAndSolve(3, r);
    }

    public void generateAndSolve(int baseSize, int seed) {
        generator = new SudokuGenerator(baseSize, null);
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
     * @return all the junit test cases to run (in this class).
     */
    public static Test suite() {
        return new TestSuite(TestSudokuGenerator.class);
    }
}
