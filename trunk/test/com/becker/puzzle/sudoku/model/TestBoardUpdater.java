package com.becker.puzzle.sudoku.model;

import com.becker.common.math.MathUtil;
import com.becker.puzzle.sudoku.data.TestData;
import com.becker.puzzle.sudoku.model.board.Board;
import com.becker.puzzle.sudoku.model.update.LoneRangerUpdater;
import com.becker.puzzle.sudoku.model.update.StandardCRBUpdater;
import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

/**
 * @author Barry Becker
 */
public class TestBoardUpdater extends TestCase {

    /** instance under test */
    BoardUpdater updater;
    Board board;

    @Override
    public void setUp() {
        MathUtil.RANDOM.setSeed(1);
        board = new Board(TestData.SIMPLE_4);
    }


    public void testUpdateAndSetStandardCRB() {

        updater = new BoardUpdater(StandardCRBUpdater.class);
        updater.updateAndSet(board);

        int[][] expectedSetValues = {
            {0, 4,    0, 0},
            {0, 1,    2, 0},
            {4, 3,    1, 2},
            {0, 2,    0, 0}
        };
        verifySetValues(expectedSetValues, board);
    }


    public void testUpdateAndSetStandardCRBAndLoneRanger() {

        updater = new BoardUpdater(StandardCRBUpdater.class, LoneRangerUpdater.class);
        updater.updateAndSet(board);

        int[][] expectedSetValues = {
            {2, 4,    0, 1},
            {3, 1,    2, 4},
            {4, 3,    1, 2},
            {1, 2,    4, 3}
        };
        verifySetValues(expectedSetValues, board);
    }

    public void testUpdateAndSetLoneRangerAndStandardCRB() {

        updater = new BoardUpdater(LoneRangerUpdater.class, StandardCRBUpdater.class);
        updater.updateAndSet(board);

        int[][] expectedSetValues = {
            {2, 4,    0, 1},
            {3, 1,    2, 4},
            {4, 3,    1, 2},
            {1, 2,    4, 3}
        };
        verifySetValues(expectedSetValues, board);
    }

    public void testUpdateAndSetLoneRangerOnly() {

        updater = new BoardUpdater(LoneRangerUpdater.class);
        updater.updateAndSet(board);

        int[][] expectedSetValues = {
            {2, 4,    0, 0},
            {3, 1,    2, 4},
            {4, 3,    0, 2},
            {1, 2,    4, 3}
        };
        verifySetValues(expectedSetValues, board);
    }


    private void verifySetValues(int[][] expectedSetValues, Board board) {
        System.out.println("board="+ board);
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
        return new TestSuite(TestBoardUpdater.class);
    }
}
