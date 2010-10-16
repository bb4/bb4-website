package com.becker.game.twoplayer.common.search.strategy;

import com.becker.game.twoplayer.common.search.Searchable;
import com.becker.game.twoplayer.common.search.examples.AlphaPrunePlayer1Example;
import com.becker.game.twoplayer.common.search.examples.AlphaPrunePlayer2Example;
import com.becker.game.twoplayer.common.search.examples.SimpleGameTreeExample;
import com.becker.optimization.parameter.ParameterArray;

/**
 * Test negamax strategy independent of any particular game implementation.
 * 
 * @author Barry Becker
 */
public class NegaMaxSearchStrategyTest extends AbstractSearchStrategyTst {

    @Override
    protected SearchStrategy createSearchStrategy(Searchable searchable, ParameterArray weights) {
        return new NegaMaxStrategy(searchable, weights);
    }

    @Override
    protected boolean negateInheritedValue() {
        return true;
    }

    @Override
    public void testThreeLevelWithABSearch() {
        searchOptions.setLookAhead(3);
        searchOptions.setAlphaBeta(true);
        verifyResult(new SimpleGameTreeExample(), "0", -5, 14);
    }

    @Override
    public void testPruneTwoLevelWithABSearchPlayer1() {
        searchOptions.setLookAhead(2);
        searchOptions.setAlphaBeta(true);
        verifyResult(new AlphaPrunePlayer1Example(), "0", -5, 5);
    }

    @Override
    public void testPruneTwoLevelWithABSearchPlayer2() {
        searchOptions.setLookAhead(2);
        searchOptions.setAlphaBeta(true);
        verifyResult(new AlphaPrunePlayer2Example(), "1", 4, 5);
    }

}