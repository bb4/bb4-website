/** Copyright by Barry G. Becker, 2000-2011. Licensed under MIT License: http://www.opensource.org/licenses/MIT  */
package com.becker.game.twoplayer.go.board;

import ca.dj.jigo.sgf.tokens.GoodWhiteMoveToken;
import com.becker.common.geometry.Location;
import com.becker.game.common.GameContext;
import com.becker.game.common.Move;
import com.becker.game.twoplayer.common.TwoPlayerOptions;
import com.becker.game.twoplayer.go.GoController;
import com.becker.game.twoplayer.go.GoTestCase;
import com.becker.game.twoplayer.go.board.elements.position.GoBoardPosition;
import com.becker.game.twoplayer.go.board.elements.position.GoStone;
import com.becker.game.twoplayer.go.board.move.GoMove;
import com.becker.game.twoplayer.go.options.GoWeights;
import junit.framework.Assert;

/**
 * Verify that we calculate the expected worth for a given board position.
 * @author Barry Becker
 */
public class TestWorthCalculator5 extends WorthCalculatorBase {

    @Override
    protected int getBoardSize() {
        return 5;
    }


    /** verify that we get the expected worth value. */
    public void testFindSimpleWorth() {
        verifyWorth("worth5x5", 33);
    }


    /** verify that we get the expected worth value after a move redo. */
    public void testFindWorthAfterRedo() {

        restore(PREFIX  + "worth5x5");
        GoBoard board = (GoBoard)controller_.getBoard();

        Move move = controller_.undoLastMove();
        controller_.makeMove(move);

        int actWorth = calculator.worth(move, WEIGHTS.getDefaultWeights());

        Assert.assertEquals("Unexpected worth.", 33, actWorth);
    }

    /**
     * If we arrive at the same exact board position from two different paths,
     * we should calculate the same worth value.
     */
    public void testSamePositionFromDifferentPathsEqual() {

        compareWorths("worth5x5_A", "worth5x5_B", -70); // -61);
    }

}
