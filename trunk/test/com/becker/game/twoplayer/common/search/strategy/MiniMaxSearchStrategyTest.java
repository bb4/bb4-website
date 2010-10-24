package com.becker.game.twoplayer.common.search.strategy;

import com.becker.game.twoplayer.common.search.Searchable;
import com.becker.optimization.parameter.ParameterArray;

/**
 * Test minimax strategy independent of any particular game implementation.
 * 
 * @author Barry Becker
 */
public class MiniMaxSearchStrategyTest extends AbstractSearchStrategyTst {

    @Override
    protected SearchStrategy createSearchStrategy(Searchable searchable, ParameterArray weights) {
        return new MiniMaxStrategy(searchable, weights);
    }


    @Override
    protected boolean negateInheritedValue() {
        return false;
    }
}