package com.becker.game.twoplayer.go;

import com.becker.game.twoplayer.go.board.GoBoardPosition;
import com.becker.game.twoplayer.go.board.GoBoard;
import com.becker.game.twoplayer.go.board.analysis.StringShapeAnalyzer;
import junit.framework.Test;
import junit.framework.TestSuite;


/**
 * @author Barry Becker
 */
public class TestShape extends GoTestCase {


    public void testShape1() {
        restore("shape/problem_shape1");

        checkShape(4, 4, 0);
        checkShape(9, 4, 0);

        checkShape(10, 8, 1);
        checkShape(11, 8, 1);
        checkShape(11, 9, 1);

        checkShape(4, 9, 3);
        checkShape(5, 9, 6);
        checkShape(5, 10, 6);
        checkShape(6, 10, 3);
    }

    private void checkShape(int r, int c, int expectedShapeScore) {
        GoBoard board = (GoBoard)controller_.getBoard();
        GoBoardPosition position = (GoBoardPosition)board.getPosition(r, c);
        StringShapeAnalyzer sa = new StringShapeAnalyzer(board);
        int n = sa.formsBadShape(position);
        assertTrue("Expected "+expectedShapeScore+" but got "+n+" for "+position, n == expectedShapeScore);
    }


    public static Test suite() {
        return new TestSuite(TestShape.class);
    }
}