package com.becker.game.twoplayer.go.test.whitebox;

import com.becker.game.twoplayer.go.test.GoTestCase;
import com.becker.game.twoplayer.go.test.TestEyes;
import com.becker.game.twoplayer.go.GoMove;
import com.becker.game.twoplayer.go.GoStone;
import com.becker.game.twoplayer.go.GoBoard;
import com.becker.game.common.Player;
import junit.framework.Assert;

/**
 * Verify that all the methods in GoBaord work as expected
 * @author Barry Becker
 */
public class TestGoBoard extends GoTestCase {


    // verify that the right stones are captured by a move
    public void testFindCaptures1() {

        verifyCaptures("whitebox/findCaptures1", 5, 6, 6);
    }

    // verify that the right stones are captured by a move
    public void testFindCaptures2() {

        verifyCaptures("whitebox/findCaptures2", 6, 6, 9);
    }


    private void verifyCaptures(String file, int row, int col, int numCaptures) {

        restore(file);

        GoMove move = new GoMove(row, col, null, 0, new GoStone(true));

        GoBoard board = (GoBoard)controller_.getBoard();

        int numWhiteStonesBefore = board.getNumStones(false);

        controller_.makeMove(move);

        int numWhiteStonesAfter = board.getNumStones(false);

        Assert.assertTrue(move.captureList != null);
        if (move.captureList!=null) {
            Assert.assertTrue("move.captureList.size()="+move.captureList.size()+" expected "+numCaptures,
                              move.captureList.size() == numCaptures);
            Assert.assertTrue(numWhiteStonesBefore - numWhiteStonesAfter == numCaptures);
        }

        controller_.undoLastMove();
        // verify that all the captured stones get restored to the board
        numWhiteStonesAfter = board.getNumStones(false);
        Assert.assertTrue(numWhiteStonesBefore == numWhiteStonesAfter );
    }
}
