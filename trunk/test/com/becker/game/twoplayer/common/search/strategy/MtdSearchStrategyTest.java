package com.becker.game.twoplayer.common.search.strategy;

import com.becker.game.twoplayer.common.search.Searchable;
import com.becker.game.twoplayer.common.search.TwoPlayerMoveStub;
import com.becker.game.twoplayer.common.search.examples.AlphaPrunePlayer1Example;
import com.becker.game.twoplayer.common.search.examples.AlphaPrunePlayer2Example;
import com.becker.game.twoplayer.common.search.examples.SimpleGameTreeExample;
import com.becker.optimization.parameter.ParameterArray;

/**
 * Test mtd strategy independent of any particular game implementation.
 * 
 * @author Barry Becker
 */
public class MtdSearchStrategyTest extends AbstractSearchStrategyTst {

    @Override
    protected SearchStrategy createSearchStrategy(Searchable searchable, ParameterArray weights) {
        return new MtdStrategy(new NegaMaxStrategy(searchable, weights));
    }

    @Override
    protected boolean negateInheritedValue() {
        return true;
    }

    @Override
    protected SearchResult getOneLevelLookAheadResult() {
        return new SearchResult("0", -8, 4);
    }

    @Override
    protected SearchResult getOneLevelWithQuiescenceAndABResult() {
        return new SearchResult( "0", -8, 3);
    }

    @Override
    protected SearchResult getOneLevelWithQuiescenceResult() {
        return new SearchResult( "0", -8, 4);
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
        return new SearchResult("0", -5, 7);
    }

    @Override
    protected SearchResult getPruneTwoLevelWithABSearchPlayer2() {
        return new SearchResult("0", 9, 12);
    }


    @Override
    protected SearchResult getThreeLevelResult() {
        return new SearchResult("0", -5, 28);
    }


    @Override
    protected SearchResult getThreeLevelBest20PercentResult() {
        return new SearchResult("0", -5, 28);
    }

    @Override
    protected SearchResult getThreeLevelWithABResult() {
        return new SearchResult("0", -5, 15);
    }
}