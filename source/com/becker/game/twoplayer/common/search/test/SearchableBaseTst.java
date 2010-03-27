package com.becker.game.twoplayer.common.search.test;

import com.becker.game.common.GameController;
import com.becker.game.twoplayer.common.TwoPlayerController;
import junit.framework.*;
import com.becker.game.twoplayer.common.search.Searchable;

/**
 * Verify that all the methods in the Searchable interface work as expected.
 * Derived test classes will excersize these methods for specific game instances.
 *
 * @author Barry Becker
 */
public abstract class SearchableBaseTst extends TestCase {

    /** The serachable instance under test. */
    protected Searchable searchable;

    protected ISearchableHelper helper;

    @Override
    protected void setUp() throws Exception {
        super.setUp();
        helper = createSearchableHelper();
    }

    /**
     * @return the helper that will help us create the contorller, options and other related info.
     */
    protected abstract ISearchableHelper createSearchableHelper();

    /** verify that we can retrieve the lookahead value. */
    public abstract void testLookaheadValue();

    /** verify that we can retrieve the lookahead value. */
    public abstract void testAlphaBetaValue();

    /** verify that we can retrieve the quiescence value. */
    public abstract void testQuiescenceValue();
}