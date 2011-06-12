package com.becker.game.twoplayer.go.board;

import com.becker.game.common.Move;
import com.becker.game.twoplayer.go.GoTestCase;
import com.becker.game.twoplayer.go.board.GoBoard;
import com.becker.game.twoplayer.go.board.WorthCalculator;
import com.becker.game.twoplayer.go.options.GoWeights;
import junit.framework.Assert;

/**
 * Verify that we calculate the expected worth for a given board position.
 * @author Barry Becker
 */
public abstract class WorthCalculatorBase extends GoTestCase {

    protected static final String PREFIX = "board/";

    protected static final GoWeights WEIGHTS = new GoWeights();

    /** instance under test. */
    protected WorthCalculator calculator;


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


    protected void verifyWorth(String file, int expWorth) {

        restore(PREFIX  + file);
        GoBoard board = (GoBoard)controller_.getBoard();

        Move move = controller_.getLastMove();
        int actWorth = calculator.worth(board, move, WEIGHTS.getDefaultWeights());

        //controller_.undoLastMove();

        Assert.assertEquals("Unexpected worth.", expWorth, actWorth);
    }


    /**
     * If we arrive at the same exact board position from two different paths,
     * we should calculate the same worth value.
     */
    protected void compareWorths(String game1, String game2, int expWorth) {

        restore(PREFIX  + game1);
        GoBoard board = (GoBoard)controller_.getBoard();

        Move move = controller_.getLastMove();
        int actWorthA = calculator.worth(board, move, WEIGHTS.getDefaultWeights());

        controller_.reset();
        restore(PREFIX + game2);

        move = controller_.getLastMove();
        int actWorthB = calculator.worth(board, move, WEIGHTS.getDefaultWeights());

        Assert.assertEquals("Unexpected worth for A.", expWorth, actWorthA);
        Assert.assertEquals("Unexpected worth for B.", expWorth, actWorthB);
    }


}
