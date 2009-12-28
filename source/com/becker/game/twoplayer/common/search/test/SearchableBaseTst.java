package com.becker.game.twoplayer.common.search.test;

import com.becker.game.common.GameController;
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
    
    protected GameController controller;

    protected static final String PREFIX = "board/";

    @Override
    protected void setUp() throws Exception {
        super.setUp();
        controller = createController();        
    }


    /**
     * Create the controller containing the searchable to test.
     */
    protected abstract GameController createController();

    /** verify that we can retrieve the lookahead value. */
    public abstract void testLookaheadValue();

    /** verify that we can retrieve the lookahead value. */
    public abstract void testAlphaBetaValue();

    /** verify that we can retrieve the quiescence value. */
    public abstract void testQuiescenceValue();
}
