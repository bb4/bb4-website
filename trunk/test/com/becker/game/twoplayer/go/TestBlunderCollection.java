package com.becker.game.twoplayer.go;

import com.becker.game.twoplayer.common.search.SearchOptions;
import com.becker.game.twoplayer.common.search.strategy.SearchStrategyType;
import junit.framework.Test;
import junit.framework.TestSuite;

/**
 * Test a collection of problems from
 * Martin Mueller (mmueller)
 * Markus Enzenberger (emarkus)
 * email domain: cs.ualberta.ca
 *
 * @author Barry Becker
 */
public class TestBlunderCollection extends GoTestCase {

    private static final String PREFIX = "problems/sgf/blunder/";

    /**
     * @param options default options to override
     */
    @Override
    protected void setOptionOverrides(SearchOptions options) { 
        options.setAlphaBeta(true);
        options.setLookAhead(2);
        options.setPercentageBestMoves(80);
        options.setQuiescence(true);
        options.setSearchStrategyMethod(SearchStrategyType.NEGASCOUT_W_MEMORY);
    }

    public void test1() {

        GoMove m = getNextMove(PREFIX + "blunder.1", true);
        verifyExpected(m, 4, 10); // 17, 8); // 17, 12);   // Q12 why?
    }

    public void test2() {
        GoMove m = getNextMove(PREFIX + "blunder.2", true);
        verifyExpected(m, 4, 10);  //17, 12);  // Q12
    }

    public void test13() {
        GoMove m = getNextMove(PREFIX + "blunder.13", false);
        verifyExpected(m, 17, 10); // 13, 5);   // A13
    }


    public void test14() {
        GoMove m = getNextMove(PREFIX + "blunder.14", false);
        verifyExpected(m, 17, 10); //  2, 12); // B12
    }

    /**
     * @return all the junit test caes to run (in this class)
     */
    public static Test suite() {
        return new TestSuite(TestBlunderCollection.class);
    }


}
