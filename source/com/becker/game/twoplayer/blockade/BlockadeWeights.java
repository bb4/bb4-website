package com.becker.game.twoplayer.blockade;

import com.becker.game.common.*;

/**
 * These weights determine how the computer values features of the board.
 * if only one computer is playing, then only one of the weights arrays is used.
 *
 * @author Barry Becker Date: Feb 11, 2007
 */
public class BlockadeWeights extends GameWeights {


    /** Use these weights if no others are provided.   */
    private static final double[] DEFAULT_WEIGHTS = {8.0, 7.0, 4.0};

    /** don't allow the weights to exceed these maximum values   */
    private static final double[] MAX_WEIGHTS = {50.0, 50.0, 50.0};

    private static final String[] WEIGHT_SHORT_DESCRIPTIONS = {
        "Shorter path weight",
        "Second shortest path weight",
        "Furthest path weight"
    };

    private static final String[] WEIGHT_DESCRIPTIONS = {
        "Weight to associate with the shortest path to the closest opponent home",
        "Weight to associate with the shortest path to the second closest opponent home",
        "Weight to associate with the shortest path to the furthest opponent home"
    };

    static final int CLOSEST_WEIGHT_INDEX = 0;
    static final int SECOND_CLOSEST_WEIGHT_INDEX = 1;
    static final int FURTHEST_WEIGHT_INDEX = 2;


    public BlockadeWeights() {
        super( DEFAULT_WEIGHTS, MAX_WEIGHTS, WEIGHT_SHORT_DESCRIPTIONS, WEIGHT_DESCRIPTIONS );
    }
}
