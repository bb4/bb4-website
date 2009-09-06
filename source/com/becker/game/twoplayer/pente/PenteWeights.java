package com.becker.game.twoplayer.pente;

import com.becker.game.common.*;

/**
 * These weights determine how the computer values each pattern
 * if only one computer is playing, then only one of the weights arrays is used.
 *
 * These weights determine how the computer values features of the board
 * if only one computer is playing, then only one of the weights arrays is used.
 * use these weights if no others are provided.
 *
 * @author Barry Becker Date: Feb 10, 2007
 */
public class PenteWeights extends GameWeights {

    /** If greater than this threshold, then opponent is in jeopardy. */
    public static final int JEOPARDY_WEIGHT = 80;

    /** These defaults may be overriden in by the user in the UI. */
    private static final double[] DEFAULT_WEIGHTS = {
        0.0,    0.0,    0.0,    0.0,     2.0,     6.0,      JEOPARDY_WEIGHT/2.0,     JEOPARDY_WEIGHT/2 + 2,    JEOPARDY_WEIGHT *2 + 30,
        2*ASSUMED_WINNING_VALUE,  2*ASSUMED_WINNING_VALUE,  2*ASSUMED_WINNING_VALUE
    };

    /** Don't allow the weights to exceed these maximum values. Upper limit. */
    private static final double[] MAX_WEIGHTS = {
        10.0,  10.0,  10.0,  20.0,  40.0,  40.0,  200.0,  200.0,  2 * ASSUMED_WINNING_VALUE,
        4 * ASSUMED_WINNING_VALUE,  6 * ASSUMED_WINNING_VALUE,  8 * ASSUMED_WINNING_VALUE
    };

     /** Don't allow the weights to go below these minimum values. Upper limit. */
    private static final double[] MIN_WEIGHTS = {
        0.0,    0.0,    0.0,    0.0,     0.0,     1.0,       1.0,        1.0,        10.0,
        ASSUMED_WINNING_VALUE,  ASSUMED_WINNING_VALUE,  ASSUMED_WINNING_VALUE
    };

    private static final String[] WEIGHT_SHORT_DESCRIPTIONS = {
        "1a weight", "1b weight", "1c weight", "2a weight",
        "2b weight", "3a weight", "3b weight", "4a  weight",
        "4b weight", "5 weight", "6 weight", "7 weight"};

    private static final String[] WEIGHT_DESCRIPTIONS = {
        "1a in a row weight",
        "1b in a row weight",
        "options with 1c in a row weight",
        "options 2a in a row weight",
        "options of 2b in a row weight",
        "open ended 3a in a row weight",
        "open ended 3b in a row weight",
        "4a in a row weight",
        "open ended 4b in a row (with options) weight",
        "arrangements of 5 in a row weight",
        "arrangements of 6 in a row weight",
        "arrangements of 7 in a row weight"
    };

    public PenteWeights() {
        super( DEFAULT_WEIGHTS,  MIN_WEIGHTS, MAX_WEIGHTS, WEIGHT_SHORT_DESCRIPTIONS, WEIGHT_DESCRIPTIONS );
    }

}
