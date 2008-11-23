package com.becker.game.twoplayer.go;

import com.becker.game.common.*;

/**
 * These weights determine how the computer values each term of the polynomial evaluation function.
 * if only one computer is playing, then only one of the weights arrays is used.
 *
 * @author Barry Becker Date: Feb 11, 2007
 */
public class GoWeights extends GameWeights {


    /** use these if no others are provided. */
    private static final double[] DEFAULT_WEIGHTS = {1.0,  0.5,  0.1,  10.0};

    /** don't allow the weights to exceed these maximum values. */
    private static final double[] MAX_WEIGHTS = {4.0,  1.0,  4.0,  20.0};

    private static final String[] WEIGHT_SHORT_DESCRIPTIONS = {
        "Health",
        "Position",
        "Bad shape",
        "Captures"};

    private static final String[] WEIGHT_DESCRIPTIONS = {
        "Weight to associate with the relative health of groups",
        "Weight to associate with Position",
        "Weight to associate with the Bad Shape Penalty",
        "Weight to give to Captures"
        //"Min Difference between health of two groups for one to be considered dead relative to the other"
    };

    static final int HEALTH_WEIGHT_INDEX = 0;
    static final int POSITIONAL_WEIGHT_INDEX = 1;
    static final int BAD_SHAPE_WEIGHT_INDEX = 2;
    static final int CAPTURE_WEIGHT_INDEX = 3;

    public GoWeights() {
        super( DEFAULT_WEIGHTS, MAX_WEIGHTS, WEIGHT_SHORT_DESCRIPTIONS, WEIGHT_DESCRIPTIONS );

    }
}
