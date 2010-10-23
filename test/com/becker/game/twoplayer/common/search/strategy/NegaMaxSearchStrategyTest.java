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
    protected SearchResult getPruneTwoLevelWithoutABResult() {
        return new SearchResult("0", -5, 6);
    }

    @Override
    protected SearchResult getPruneTwoLevelWithABSearchPlayer1() {
        return new SearchResult("0", -5, 5);
    }

    @Override
    protected SearchResult getPruneTwoLevelWithABSearchPlayer2() {
        return new SearchResult("0", 9, 5);
    }

}