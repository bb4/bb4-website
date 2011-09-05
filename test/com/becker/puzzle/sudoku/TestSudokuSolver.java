package com.becker.puzzle.sudoku;

import com.becker.puzzle.sudoku.data.TestData;
import com.becker.puzzle.sudoku.model.Board;
import junit.framework.*;

/**
 * @author Barry Becker
 */
public class TestSudokuSolver extends TestCase {

    /** instance under test. */
    SudokuSolver solver;


    /**
     * common initialization for all go test cases.
     */
    @Override
    protected void setUp() throws Exception {
        super.setUp();
        solver = new SudokuSolver();
    }


    @Override
    protected void tearDown() throws Exception {

        super.tearDown();
    }

    public void testCaseSimpleSample() {

        boolean solved = solver.solvePuzzle(new Board(TestData.SIMPLE_9));
        Assert.assertTrue( "Did not solve SIMPLE_9 successfully", solved);
    }

    /** negative test case */
    public void testImpossiblePuzzle() {

        boolean solved = solver.solvePuzzle(new Board(TestData.INCONSISTENT_9));
        Assert.assertFalse( "Solved impossible SIMPLE_9 puzzle. Should not have.", solved);
    }


    /** The large tests takes a long time because of the exponential growth with the size of the puzzle. */
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
     * @return all the junit test cases to run (in this class).
     */
    public static Test suite() {
        return new TestSuite(TestSudokuSolver.class);
    }
}
