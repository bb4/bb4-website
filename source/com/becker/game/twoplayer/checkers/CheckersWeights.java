package com.becker.game.twoplayer.checkers;

import com.becker.game.common.*;

/**
 *  These weights determine how the computer values features of the board
 * if only one computer is playing, then only one of the weights arrays is used.
 *
 * @author Barry Becker Date: Feb 11, 2007
 */
public class CheckersWeights extends GameWeights {

    /** use these weights if no others are provided. */
    private static final double[] DEFAULT_WEIGHTS = {10.0, 19.0, 1.0};

    /** don't allow the weights to exceed these maximum values */
    private static final double[] MAX_WEIGHTS = {50.0, 100.0, 10.0};

    private static final String[] WEIGHT_SHORT_DESCRIPTIONS = {
        "PegMove weight",
        "King weight",
        "Advancement weight"
    };

    private static final String[] WEIGHT_DESCRIPTIONS = {
        "Weight to associate with the number of remaining pieces",
        "Weight to associate with the number of kings that a side has",
        "Weight to give associate with piece advancement"
    };

    static final int PIECE_WEIGHT_INDEX = 0;
    static final int KINGED_WEIGHT_INDEX = 1;
    static final int ADVANCEMENT_WEIGHT_INDEX = 2;


    public CheckersWeights() {
        super( DEFAULT_WEIGHTS, MAX_WEIGHTS, WEIGHT_SHORT_DESCRIPTIONS, WEIGHT_DESCRIPTIONS );
    }
}
