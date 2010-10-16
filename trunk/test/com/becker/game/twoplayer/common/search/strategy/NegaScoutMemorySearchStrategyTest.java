package com.becker.game.twoplayer.common.search.strategy;

import com.becker.game.twoplayer.common.search.Searchable;
import com.becker.optimization.parameter.ParameterArray;

/**
 * Test negascout memory strategy independent of any particular game implementation.
 * 
 * @author Barry Becker
 */
public class NegaScoutMemorySearchStrategyTest extends AbstractSearchStrategyTst {

    @Override
    protected SearchStrategy createSearchStrategy(Searchable searchable, ParameterArray weights) {
        return new NegaScoutMemoryStrategy(searchable, weights);
    }

    @Override
    protected boolean negateInheritedValue() {
        return true;
    }

}