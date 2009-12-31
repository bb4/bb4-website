package com.becker.game.twoplayer.common.search.test.strategy;

import com.becker.game.twoplayer.common.search.strategy.SearchStrategyType;

/**
 * Verify that all the methods in the Searchable interface work as expected.
 * Derived test classes will excersize these methods for specific game instances.
 *
 * @author Barry Becker
 */
public abstract class NegaMaxStrategyTst extends AbstractSearchStrategyTst {

    @Override
    protected SearchStrategyType getSearchStrategyToTest() {
        return SearchStrategyType.NEGAMAX;
    }

}