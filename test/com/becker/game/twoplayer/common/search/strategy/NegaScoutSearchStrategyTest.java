package com.becker.game.twoplayer.common.search.strategy;

import com.becker.game.twoplayer.common.search.Searchable;
import com.becker.game.twoplayer.common.search.examples.AlphaPrunePlayer1Example;
import com.becker.game.twoplayer.common.search.examples.SimpleGameTreeExample;
import com.becker.optimization.parameter.ParameterArray;

/**
 * Test negascout strategy independent of any particular game implementation.
 * 
 * @author Barry Becker
 */
public class NegaScoutSearchStrategyTest extends AbstractSearchStrategyTst {

    @Override
    protected SearchStrategy createSearchStrategy(Searchable searchable, ParameterArray weights) {
        return new NegaScoutStrategy(searchable, weights);
    }

    @Override
    protected boolean negateInheritedValue() {
        return true;
    }

    @Override
    public void testTwoLevelSearch() {
        searchOptions.setLookAhead(2);
        verifyResult(new SimpleGameTreeExample(), "0", 7, 5);
    }

    @Override
    public void testThreeLevelSearch() {
        verifyResult(new SimpleGameTreeExample(), "0", -5, 12);
    }

    /** best percentage ignore by base search algorithm. Only used when generating moves. */
    @Override
    public void testThreeLevelBest20PercentSearch() {
        searchOptions.setLookAhead(3);
        searchOptions.setPercentageBestMoves(20);
        verifyResult(new SimpleGameTreeExample(), "0", -5, 12);
    }

    @Override
    public void testPruneTwoLevelWithABSearchPlayer1() {
        searchOptions.setLookAhead(2);
        searchOptions.setAlphaBeta(true);
        verifyResult(new AlphaPrunePlayer1Example(), "0", -5, 5);
    }


    @Override
    public void testThreeLevelWithABSearch() {
        searchOptions.setLookAhead(3);
        searchOptions.setAlphaBeta(true);
        verifyResult(new SimpleGameTreeExample(), "0", -5, 12);
    }

    @Override
    public void testPruneTwoLevelWithoutABSearch() {
        searchOptions.setLookAhead(2);
        searchOptions.setAlphaBeta(false);
        verifyResult(new AlphaPrunePlayer1Example(), "0", -5, 5);
    }

}