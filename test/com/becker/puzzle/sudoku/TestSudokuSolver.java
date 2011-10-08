package com.becker.puzzle.sudoku;

import com.becker.common.math.MathUtil;
import com.becker.puzzle.sudoku.data.TestData;
import com.becker.puzzle.sudoku.model.Board;
import junit.framework.*;

/**
 * @author Barry Becker
 */
public class TestSudokuSolver extends TestCase {

    /** instance under test. */
    SudokuSolver solver;

    SudokuGenerator generator;

    /**
     * common initialization for all go test cases.
     */
    @Override
    protected void setUp() throws Exception {
        super.setUp();
        MathUtil.RANDOM.setSeed(0);
    }

    @Override
    protected void tearDown() throws Exception {

        super.tearDown();
    }

    public void testCaseSimpleSample() {

        solver = new SudokuSolver(new Board(TestData.SIMPLE_9));
        boolean solved = solver.solvePuzzle();
        Assert.assertTrue( "Did not solve SIMPLE_9 successfully", solved);
    }

    /** negative test case */
    public void testImpossiblePuzzle() {

        try {
            solver = new SudokuSolver(new Board(TestData.INCONSISTENT_9));
            //solver.solvePuzzle();
            fail();
        }
        catch (IllegalStateException e) {
            // success
        }
        //Assert.assertFalse( "Solved impossible SIMPLE_9 puzzle. Should not have.", solved);
    }

    public void testGenerateAndSolve2() {

            generateAndSolve(2);
    }

    public void testGenerateAndSolve3() {

            generateAndSolve(3);
    }

    public void testGenerateLotsAndSolveMany() {

        for (int r=0; r < 40; r++)
            MathUtil.RANDOM.setSeed(r);
            generateAndSolve(3);

    }


    /** The large tests takes a long time because of the exponential growth with the size of the puzzle. */
    public void testGenerateAndSolve() {
        // super exponential run time
        generateAndSolve(2);  // 16  cells       32 ms
        generateAndSolve(3);  // 81  cells      265 ms
        generateAndSolve(4);  // 256 cells    2,077 ms
        //generateAndSolve(5);  // 625 cells  687,600 ms
    }


    public void generateAndSolve(int baseSize) {
        Board board = generatePuzzle(baseSize);
        solve(board);
    }

    public Board generatePuzzle(int baseSize) {
        generator = new SudokuGenerator(baseSize, null);
        long start = System.currentTimeMillis();
        Board b = generator.generatePuzzleBoard();
        System.out.println("Time to generate size="+baseSize +" was "+ (System.currentTimeMillis() - start));
        return b;
    }

    public void solve(Board board) {
        SudokuSolver solver = new SudokuSolver(board);
        long start = System.currentTimeMillis();
        boolean solved = solver.solvePuzzle();
        System.out.println("Time to solve was "+ (System.currentTimeMillis() - start));
        Assert.assertTrue("Unexpectedly not solved.", solved);
    }

    /**
     * @return all the junit test cases to run (in this class).
     */
    public static Test suite() {
        return new TestSuite(TestSudokuSolver.class);
    }
}
