package com.becker.game.twoplayer.common.search.strategy;

/**
 * Verify that all the methods in the Searchable interface work as expected.
 * Derived test classes will excersize these methods for specific game instances.
 *
 * @author Barry Becker
 */
public abstract class MiniMaxStrategyTst extends AbstractStrategyTst {

    @Override
    protected SearchStrategyType getSearchStrategyToTest() {
        return SearchStrategyType.MINIMAX;
    }

}