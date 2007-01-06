package com.becker.game.twoplayer.go;



/**
 * Go constraint parameters that can be optimized.
 * Some of these could be moved to GoOptions.
 *
 * @author Barry Becker    Date: Aug 21, 2005
 */
public final class GoControllerConstants {

    private GoControllerConstants() {}

    // if greater than this at the end of the game, then the stone is considered alive, else dead.
    public static final float ALIVE_THRESHOLD = 0.5f;

    // if true use an additional heuristic to get more accurate scoring of group health in a second pass.
    public static final boolean USE_RELATIVE_GROUP_SCORING = true;

    // these weights determine how the computer values each term of the
    // polynomial evaluation function.
    // if only one computer is playing, then only one of the weights arrays is used.
    // use these if no others are provided
    static final double[] DEFAULT_WEIGHTS = {1.0, 0.5, 0.1, 20.0};

    // don't allow the weights to exceed these maximum values
    static final double[] MAX_WEIGHTS = {4.0, 1.0, 4.0, 10.0};
    static final String[] WEIGHT_SHORT_DESCRIPTIONS = {"Health", "Position", "Bad shape", "Captures"};
    static final String[] WEIGHT_DESCRIPTIONS = {
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

    // default num row and columns for a default square go board.
    static final int DEFAULT_NUM_ROWS = 5;

    static final int WIN_THRESHOLD = 1000;

    // we assign a value to a stone based on the line on which it falls when calculating worth
    static final float[] LINE_VALS = {-0.5f, 0.1f, 1.0f, 0.9f, 0.1f};
}
