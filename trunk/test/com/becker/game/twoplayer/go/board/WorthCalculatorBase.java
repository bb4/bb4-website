package com.becker.game.twoplayer.go.board;

import com.becker.game.common.Move;
import com.becker.game.twoplayer.go.GoTestCase;
import com.becker.game.twoplayer.go.board.analysis.TerritoryAnalyzer;
import com.becker.game.twoplayer.go.board.analysis.WorthCalculator;
import com.becker.game.twoplayer.go.board.analysis.group.GroupAnalyzerMap;
import com.becker.game.twoplayer.go.options.GoWeights;
import gui.Board;
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

        GoBoard board = (GoBoard) controller_.getBoard();
        GroupAnalyzerMap analyzerMap = new GroupAnalyzerMap();
        TerritoryAnalyzer terrAnalyzer = new TerritoryAnalyzer(board, analyzerMap);
        calculator = new WorthCalculator(board, terrAnalyzer);
    }


    protected void verifyWorth(String file, int expWorth) {

        restore(PREFIX  + file);

        Move move = controller_.getLastMove();
        int actWorth = calculator.worth(move, WEIGHTS.getDefaultWeights());


        Assert.assertEquals("Unexpected worth.", expWorth, actWorth);
    }


    /**
     * If we arrive at the same exact board position from two different paths,
     * we should calculate the same worth value.
     */
    protected void compareWorths(String game1, String game2, int expWorth) {

        restore(PREFIX  + game1);

        Move move = controller_.getLastMove();
        int actWorthA = calculator.worth(move, WEIGHTS.getDefaultWeights());

        controller_.reset();
        restore(PREFIX + game2);

        move = controller_.getLastMove();
        int actWorthB = calculator.worth(move, WEIGHTS.getDefaultWeights());

        Assert.assertEquals("Unexpected worth for A.", expWorth, actWorthA);
        Assert.assertEquals("Unexpected worth for B.", expWorth, actWorthB);
    }


}
