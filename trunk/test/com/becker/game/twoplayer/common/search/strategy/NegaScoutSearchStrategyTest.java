package com.becker.game.twoplayer.common.search.strategy;

import com.becker.game.twoplayer.common.search.Searchable;
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

}