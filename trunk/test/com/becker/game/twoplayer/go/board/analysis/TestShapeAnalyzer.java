package com.becker.game.twoplayer.go.board.analysis;

import com.becker.game.twoplayer.go.GoTestCase;
import com.becker.game.twoplayer.go.board.GoBoard;
import com.becker.game.twoplayer.go.board.elements.GoBoardPosition;
import junit.framework.Assert;

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
        StringShapeAnalyzer sa = new StringShapeAnalyzer(board);
        int badShapeScore = sa.formsBadShape(pos);
        Assert.assertTrue("badShapeScore="+badShapeScore+" expected="+expected, badShapeScore == expected);
    }

}
