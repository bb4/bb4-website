package com.becker.game.twoplayer.go.test.board.analysis;

import com.becker.game.twoplayer.go.board.GoBoardPosition;
import com.becker.game.twoplayer.go.board.GoBoard;
import com.becker.game.twoplayer.go.board.analysis.ShapeAnalyzer;
import com.becker.game.twoplayer.go.test.*;
import junit.framework.*;

import java.util.*;

/**
 * Verify expected shapes on the board.
 * @author Barry Becker
 */
public class TestShapeAnalyzer extends GoTestCase {

    private static final String PREFIX = "board/badshape/";

    public void testBadShape1() {
        verifyBadShape("badShape1", 4, 4, 3);
    }

    public void testBadShape2() {
        verifyBadShape("badShape2", 4, 4, 1);
    }

    public void testBadShape3() {
        verifyBadShape("badShape3", 4, 4, 8);
    }

    public void verifyBadShape(String file, int row, int col, int expected) {
        restore(PREFIX + file);

        GoBoard board = (GoBoard)controller_.getBoard();
        GoBoardPosition pos = (GoBoardPosition)board.getPosition(row, col);
        ShapeAnalyzer sa = new ShapeAnalyzer(board);
        int badShapeScore = sa.formsBadShape(pos);
        Assert.assertTrue("badShapeScore="+badShapeScore+" expected="+expected, badShapeScore == expected);
    }

}
