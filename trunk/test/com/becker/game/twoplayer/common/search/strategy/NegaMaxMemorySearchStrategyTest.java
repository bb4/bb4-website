package com.becker.game.twoplayer.common.search.strategy;

import com.becker.game.twoplayer.common.search.Searchable;
import com.becker.game.twoplayer.common.search.examples.AlphaPrunePlayer1Example;
import com.becker.game.twoplayer.common.search.examples.AlphaPrunePlayer2Example;
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
    
    /**
     * Look ahead one level and get the best move.
     */
    @Override
    protected SearchResult getOneLevelLookAheadResult() {
        return new SearchResult("0", -8, 4);
    }

    @Override
    protected SearchResult getOneLevelWithQuiescenceAndABResult() {
        return new SearchResult("0", -8, 3);
    }

    @Override
    protected SearchResult getOneLevelWithQuiescenceResult() {
        return new SearchResult("0", -8, 4);
    }


    @Override
    protected SearchResult getTwoLevelResult() {
        return new SearchResult("0", 7, 12);
    }

    @Override
    protected SearchResult getPruneTwoLevelWithoutABResult() {
        return new SearchResult("0", -5, 12);
    }

    @Override
    protected SearchResult getPruneTwoLevelWithABSearchPlayer1() {
        return new SearchResult("0", -5, 6);
    }

    @Override
    protected SearchResult getPruneTwoLevelWithABSearchPlayer2() {
        return new SearchResult("0", 5, 6);
    }



    @Override
    protected SearchResult getThreeLevelResult() {
        return new SearchResult("0", -5, 16);
    }

    @Override
    protected SearchResult getThreeLevelBest20PercentResult() {
        return new SearchResult("0", -5, 16);
    }

    @Override
    protected SearchResult getThreeLevelWithABResult() {
        return new SearchResult("0", -5, 9);
    }
}