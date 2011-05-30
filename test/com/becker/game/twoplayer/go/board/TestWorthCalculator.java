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
 * Verify that all the methods in GoBoard work as expected
 * @author Barry Becker
 */
public class TestWorthCalculator extends GoTestCase {

    private static final String PREFIX = "board/";

    private static final GoWeights WEIGHTS = new GoWeights();

    /** instance under test. */
    private WorthCalculator calculator;


    /**
     * common initialization for all go test cases.
     * Override setOptionOverrides if you want different search parameters.
     */
    @Override
    protected void setUp() throws Exception {
        super.setUp();

        int size = controller_.getBoard().getNumRows();
        calculator = new WorthCalculator(size, size);
    }


    /** verify that we get the expected worth value. */
    public void testFindSimpleWorth() {
        verifyWorth("worth5x5", 34);
    }


    /** verify that we get the expected worth value. */
    public void testFindWorthAfterRedo() {

        restore(PREFIX  + "worth5x5");
        GoBoard board = (GoBoard)controller_.getBoard();

        Move move = controller_.undoLastMove();
        controller_.makeMove(move);

        int actWorth = calculator.worth(board, move, WEIGHTS.getDefaultWeights());

        Assert.assertEquals("Unexpected worth.", 34, actWorth);
    }


    private void verifyWorth(String file, int expWorth) {

        restore(PREFIX  + file);
        GoBoard board = (GoBoard)controller_.getBoard();

        Move move = controller_.getLastMove();
        int actWorth = calculator.worth(board, move, WEIGHTS.getDefaultWeights());

        //controller_.undoLastMove();

        Assert.assertEquals("Unexpected worth.", expWorth, actWorth);
    }

}
