// Copyright by Barry G. Becker, 2012. Licensed under MIT License: http://www.opensource.org/licenses/MIT
package com.becker.game.common;

import com.becker.game.twoplayer.pente.pattern.SimpleWeights;
import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

/**
 * Verify that we correctly evaluate patterns on the board.
 *
 * @author Barry Becker
 */
public class GameWeightsTest extends TestCase  {

    /** instance under test */
    private GameWeights weights;


    @Override
    protected void setUp() throws Exception {
        super.setUp();
        weights = new SimpleWeights();
    }

    public void testConstruction() {
        assertEquals("unexpected name ",
                "Weight 1", weights.getName(1));
        assertEquals("unexpected description ",
                "The weighting coefficient for the 1th term of the evaluation polynomial", weights.getDescription(1));
        assertEquals("unexpected param 2 value ",
                10, weights.getPlayer1Weights().get(2).getValue());
    }

    /**
     * @return the line
     */
    private StringBuilder createLine(String line) {
        return new StringBuilder(line);
    }

    public static Test suite() {
        return new TestSuite(GameWeightsTest.class);
    }
}