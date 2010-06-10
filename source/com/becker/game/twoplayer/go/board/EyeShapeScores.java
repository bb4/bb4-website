package com.becker.game.twoplayer.go.board;

/**
 *
 *
 * @author Barry Becker
 */
public final class EyeShapeScores
{

    public static float FALSE_EYE = 0.19f;

    /** one or two points, or clump. */
    public static float SINGLE_EYE = 1.0f;

    /** Can be made one eye if opponent plays first. */
    public static float BIG_EYE = 1.2f;

    /** Can be made one eye if opponent plays twice. */
    public static float PROBABLE_TWO_EYES = 1.6f;

    /** We are guaranteed to have 2 eyes. */
    public static float GUARANTEED_TWO_EYES = 2.0f;

    /** Likely that there is at least 2 eyes, but also possible that there are 0 or one */
    public static float TERRITORIAL_EYE = 1.9f;

}