package com.becker.game.twoplayer.go.board.analysis.eye;

/**
 * Scores for various sorts of prototypical eye types.
 * 0 means dead. 1 means unconditionally alive.
 *
 * @author Barry Becker
 */
public final class EyeShapeScores
{
    /** any shape that have a false eye point. */
    public static final float FALSE_EYE = 0.19f;

    /** one or two points, or clump. */
    public static final float SINGLE_EYE = 1.0f;

    /** Can be made one eye if opponent plays first. */
    public static final float BIG_EYE = 1.2f;

    /** Can be made one eye if opponent plays twice. */
    public static final float PROBABLE_TWO_EYES = 1.6f;


    /** We are guaranteed to have 2 eyes. */
    public static final float GUARANTEED_TWO_EYES = 2.0f;

    /** Likely that there is at least 2 eyes, but also possible that there are 0 or one */
    public static final float TERRITORIAL_EYE = 1.9f;


    private EyeShapeScores() {}
}