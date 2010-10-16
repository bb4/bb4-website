package com.becker.game.twoplayer.common.search.strategy;

import com.becker.game.twoplayer.common.search.Searchable;
import com.becker.game.twoplayer.common.search.examples.SimpleGameTreeExample;
import com.becker.optimization.parameter.ParameterArray;

/**
 * Test negamax memory strategy independent of any particular game implementation.
 * 
 * @author Barry Becker
 */
public class NegaMaxMemorySearchStrategyTest extends AbstractSearchStrategyTst {

    @Override
    protected SearchStrategy createSearchStrategy(Searchable searchable, ParameterArray weights) {
        return new NegaMaxMemoryStrategy(searchable, weights);
    }

    @Override
    protected boolean negateInheritedValue() {
        return true;
    }

    @Override
    public void testThreeLevelSearch() {
        verifyResult(new SimpleGameTreeExample(), "0", -5, 8);
    }

    @Override
    public void testThreeLevelBest20PercentSearch() {
        searchOptions.setLookAhead(3);
        searchOptions.setPercentageBestMoves(20);
        verifyResult(new SimpleGameTreeExample(), "0", -5, 8);
    }

    @Override
    public void testThreeLevelWithABSearch() {
        searchOptions.setLookAhead(3);
        searchOptions.setAlphaBeta(true);
        verifyResult(new SimpleGameTreeExample(), "0", -5, 6);
    }


}