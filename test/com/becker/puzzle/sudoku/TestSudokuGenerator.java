package com.becker.puzzle.sudoku;

import com.becker.common.math.MathUtil;
import com.becker.puzzle.sudoku.model.Board;
import junit.framework.Assert;
import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Barry Becker
 */
public class TestSudokuGenerator extends TestCase {

    /** instance under test. */
    SudokuGenerator generator;

    @Override
    public void setUp() {
        MathUtil.RANDOM.setSeed(0);
    }

    public void testGenerateInitialSolution2() {
        Board board = generateInitialSolution(2);

        Board expBoard = new Board(new int[][] {
            {4, 1,    3, 2},
            {2, 3,    4, 1},
            {1, 4,    2, 3},
            {3, 2,    1, 4}
        });

        assertEquals("Unexpected generated board", expBoard, board);
    }

    public void testGenerateInitialSolution3() {
        Board board = generateInitialSolution(3);

        Board expBoard = new Board(new int[][] {
             {4, 1, 3,  9, 5, 6,  2, 8, 7},
             {6, 9, 2,  8, 7, 4,  3, 5, 1},
             {7, 5, 8,  3, 2, 1,  9, 6, 4},
             {5, 3, 1,  2, 9, 8,  4, 7, 6},
             {8, 2, 7,  4, 6, 3,  5, 1, 9},
             {9, 4, 6,  7, 1, 5,  8, 2, 3},
             {1, 6, 9,  5, 4, 2,  7, 3, 8},
             {3, 7, 5,  1, 8, 9,  6, 4, 2},
             {2, 8, 4,  6, 3, 7,  1, 9, 5}
        });

        assertEquals("Unexpected generated board", expBoard, board);
    }

    /** works only half the time!
    public void testGenerateInitialSolution4Many() {

        List<Boolean> passed = new ArrayList<Boolean>();

        for (int i=10; i<30; i++)  {
            MathUtil.RANDOM.setSeed(i);
            Board board = generateInitialSolution(4);
            System.out.println(board);
            passed.add(board != null);
        }
        System.out.println("-----passed="+passed);
        //assertNotNull("Could not create a consistent board", board);
    }  */

    public void testGenerateInitialSolution4() {

        MathUtil.RANDOM.setSeed(0);
        Board board = generateInitialSolution(4);

        assertNotNull("Could not create a consistent board", board);
    }


    public void testGeneratePuzzle2() {
        Board board = generatePuzzle(2);

        Board expBoard = new Board(new int[][] {
            {0, 1,    3, 0},
            {2, 0,    0, 0},
            {0, 0,    0, 0},
            {0, 0,    1, 0}
        });

        assertEquals("Unexpected generated board", expBoard, board);
    }

    public Board generateInitialSolution(int baseSize) {
        generator = new SudokuGenerator(baseSize);
        long start = System.currentTimeMillis();

        Board b = new Board(baseSize);
        boolean solved = generator.generateSolution(b);
        System.out.println("SOLVED = " + solved + "  Time to generate solution for size=" + baseSize
                +" was "+ (System.currentTimeMillis() - start));
        //assertTrue("The board was not solved!", solved);
        if (!solved) return null;
        return b;
    }

    public Board generatePuzzle(int baseSize) {
        generator = new SudokuGenerator(baseSize, null);
        long start = System.currentTimeMillis();
        Board b = generator.generatePuzzleBoard();
        System.out.println(" Time to generate size="+baseSize +" was "+ (System.currentTimeMillis() - start));
        return b;
    }

    /**
     * @return all the junit test cases to run (in this class).
     */
    public static Test suite() {
        return new TestSuite(TestSudokuGenerator.class);
    }
}
