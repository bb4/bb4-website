package com.becker.puzzle.sudoku.model;

import ca.dj.jigo.sgf.tokens.SourceToken;
import com.becker.common.math.MathUtil;
import com.becker.puzzle.sudoku.SudokuGenerator;
import com.becker.puzzle.sudoku.SudokuSolver;
import com.becker.puzzle.sudoku.data.TestData;
import com.sun.rowset.internal.Row;
import junit.framework.Assert;
import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

import java.util.Arrays;
import java.util.List;

/**
 * @author Barry Becker Date: Jul 3, 2006
 */
public class TestBoard extends TestCase {

    /** instance under test */
    Board board;

    public void testFindCellCandidatesForFirstCell() {

        board = new Board(TestData.SIMPLE_4);
        Candidates cands = board.findCellCandidates(0, 0);

        Candidates expCands = new Candidates(1, 2, 3);  // everything but 4.
        Assert.assertEquals("Did find correct candidates",
                expCands, cands);
    }

    public void testFindCellCandidatesForAll() {

        board = new Board(TestData.SIMPLE_4);
        Candidates[][] expCands = {
                {new Candidates(1, 2, 3), null,                 new Candidates(1, 3),   new Candidates(1, 3)},
                {new Candidates(1, 3),    new Candidates(1),    null,                   new Candidates(1, 3, 4)},
                {null                ,    null,                 new Candidates(1),   new Candidates(1, 2)},
                {new Candidates(1, 2),    new Candidates(1, 2), new Candidates(1, 3, 4), new Candidates(1, 2, 3, 4)}
        };
        for (int i=0; i<board.getEdgeLength(); i++) {
            for (int j=0; j<board.getEdgeLength(); j++) {
                Candidates cands = board.findCellCandidates(i, j);
                Assert.assertEquals( "Did find correct candidates for cell row=" + i + " j="+ j,
                expCands[i][j], cands);
            }
        }
    }

    public void testFindShuffledCellCandidates() {

        board = new Board(TestData.SIMPLE_4);
        MathUtil.RANDOM.setSeed(1);
        List<Integer> cands = board.getShuffledCellCandidates(0);

        // 1, 2, 3 in random order;
        List<Integer> expShuffledCands = Arrays.asList(2, 3, 1);
        Assert.assertEquals("Did find correct candidates",
                expShuffledCands, cands);
    }

    public void testNotSolved() {
        board = new Board(TestData.SIMPLE_4);
        assertFalse("Unexpectedly solved", board.solved());
    }


    public void testSolved() {
        board = new Board(TestData.SIMPLE_4_SOLVED);
        assertTrue("Unexpectedly not solved", board.solved());
    }

    public void testUpdateAndSet() {
        board = new Board(TestData.SIMPLE_4);
        board.updateAndSet();

        int[][] expectedSetValues = {
            {0, 4,    0, 0},
            {0, 1,    2, 0},   // 1 set at 1, 2
            {4, 3,    1, 2},   // 1 set at 2, 2
            {0, 2,    0, 0}
        };
        verifySetValues(expectedSetValues, board);
    }

    /*
    public void testCheckAndSetUniqueValues() {
        board = new Board(TestData.SIMPLE_4);

        Candidates[] rowCandsData = new Candidates[] {
            new Candidates(1, 2, 3), new Candidates(1, 3, 4),  new Candidates(1, 2), new Candidates(1, 2, 3, 4)
        };
        Candidates[] colCandsData = new Candidates[] {
            new Candidates(1, 2, 3), new Candidates(1, 2),  new Candidates(1, 3, 4), new Candidates(1, 2, 3, 4)
        };

        CandidatesArray rowCands = new CandidatesArray(rowCandsData);
        CandidatesArray colCands = new CandidatesArray(colCandsData);


        board.checkAndSetUniqueValues(rowCands, colCands);

        int[][] expectedSetValues = {
            {0, 4,    0, 0},
            {0, 1,    2, 0},   // 1 set at 1, 2
            {4, 3,    1, 0},   // 1 set at 2, 2
            {0, 2,    0, 0}
        };

        verifySetValues(expectedSetValues, board);
    } */


    private void verifySetValues(int[][] expectedSetValues, Board board) {
        System.out.println("board=\n"+ board);
        for (int i=0; i<board.getEdgeLength(); i++) {
            for (int j=0; j<board.getEdgeLength(); j++) {
                assertEquals("Unexpected set value at row="+ i +" col="+j,
                        expectedSetValues[i][j], board.getCell(i, j).getValue());
            }
        }
    }


    /**
     * @return all the junit test cases to run (in this class).
     */
    public static Test suite() {
        return new TestSuite(TestBoard.class);
    }
}
