package com.becker.game.twoplayer.common.search.strategy;

import com.becker.game.twoplayer.common.search.Searchable;
import com.becker.game.twoplayer.common.search.TwoPlayerMoveStub;
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
    public void testOneLevelLookAheadSearch() {
        searchOptions.setLookAhead(1);
        verifyResult(new SimpleGameTreeExample(), "0", -8, 4);
    }

    @Override
    public void testOneLevelWithQuiescenceAndABSearch() {
        searchOptions.setLookAhead(1);
        searchOptions.setQuiescence(true);
        searchOptions.setAlphaBeta(true);
        verifyResult(new SimpleGameTreeExample(), "0", -8, 4);
    }

    @Override
    public void testOneLevelWithQuiescenceSearch() {
        searchOptions.setLookAhead(1);
        searchOptions.setQuiescence(true);
        verifyResult(new SimpleGameTreeExample(), "0", -8, 4);
    }

    @Override
    public void testTwoLevelSearch() {
        searchOptions.setLookAhead(2);
        verifyResult(new SimpleGameTreeExample(), "0", 7, 12);
    }

    @Override
    public void testThreeLevelSearch() {
        verifyResult(new SimpleGameTreeExample(), "0", -5, 28);
    }

    @Override
    public void testThreeLevelBest20PercentSearch() {
        searchOptions.setLookAhead(3);
        searchOptions.setPercentageBestMoves(20);
        verifyResult(new SimpleGameTreeExample(), "0", -5, 28);
    }

    @Override
    public void testThreeLevelWithABSearch() {
        searchOptions.setLookAhead(3);
        searchOptions.setAlphaBeta(true);
        verifyResult(new SimpleGameTreeExample(), "0", -5, 20);
    }
}