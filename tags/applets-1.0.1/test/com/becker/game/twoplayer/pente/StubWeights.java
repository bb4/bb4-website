/** Copyright by Barry G. Becker, 2000-2011. Licensed under MIT License: http://www.opensource.org/licenses/MIT  */
package com.becker.game.twoplayer.pente;

import com.becker.game.common.GameWeights;

/**
 * Simple weights for unit testing.
 *
 * @author Barry Becker
 */
public class StubWeights extends GameWeights {

    /** Weights for the different patterns */
    private static final double[] DEFAULT_WEIGHTS = { 1.0,    5.0 };

    /** Don't allow the weights to exceed these maximum values. Upper limit. */
    private static final double[] MAX_WEIGHTS =     { 5.0,   100.0 };

     /** Don't allow the weights to go below these minimum values. Upper limit. */
    private static final double[] MIN_WEIGHTS =     { 0.0,   5.0 };

    private static final String[] WEIGHT_SHORT_DESCRIPTIONS = {
        "weight 1",  "weight 2"};


    public StubWeights() {
        super( DEFAULT_WEIGHTS,  MIN_WEIGHTS, MAX_WEIGHTS, WEIGHT_SHORT_DESCRIPTIONS, WEIGHT_SHORT_DESCRIPTIONS );
    }
}