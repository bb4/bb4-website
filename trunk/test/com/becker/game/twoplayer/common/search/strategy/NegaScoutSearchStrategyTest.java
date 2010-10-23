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
    protected SearchResult getTwoLevelResult() {
        return new SearchResult("0", 7, 5);
    }


    /** best percentage ignore by base search algorithm. Only used when generating moves. */
    @Override
    protected SearchResult getThreeLevelBest20PercentResult() {
        return new SearchResult("0", -5, 12);
    }

    @Override
    protected SearchResult getPruneTwoLevelWithABSearchPlayer1() {
        return new SearchResult("0", -5, 5);
    }    

    @Override
    protected SearchResult getThreeLevelResult() {
        return new SearchResult("0", -5, 12);
    }

    @Override
    protected SearchResult getThreeLevelWithABResult() {
        return new SearchResult("0", -5, 12);
    }

    @Override
    protected SearchResult getPruneTwoLevelWithoutABResult() {
        return new SearchResult("0", -5, 5);
    }

}