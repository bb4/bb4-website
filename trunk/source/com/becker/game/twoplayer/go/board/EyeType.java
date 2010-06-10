package com.becker.game.twoplayer.go.board;

import static com.becker.game.twoplayer.go.board.EyeShapeScores.*;
/**
 * Enum for the different possible Eye shapes.
 * See http://www.ai.univ-paris8.fr/~cazenave/eyeLabelling.pdf
 * @see GoEye
 *
 *  We define the Neighbour Classification of  an eye as a number
 * of  digits sorted from low to high, where every intersection in the eye space is associated
 * to a digit that indicates the number of neighbors (adjacent intersections)
 * to that intersection that belong to the eye space.
 *
 * For example, here are all the possible pentomino classifications (independent of symmetry).
 *
 * E11222: XXXXX      XXXX      XXX      XX     XX    X       X
 *                       X        XX      X      X    X      XX
 * E11123:  XX        X       X          XX      XX   XXX   XX
 *         XX        XX       X
 *          X         X      XXX
 * E11114:       X    X
 *              XXX
 *               X
 * E12223:  XX
 *          XXX
 *
 * @author Barry Becker
 */
public enum EyeType
{
    /** False eye always have the potential to become no eyes */
    FalseEye(false, 0, 0, FALSE_EYE),

    /* E1, E2, E3 shapes */
    E0(false, 1, 1, SINGLE_EYE),
    E11(false, 2, 1, SINGLE_EYE),
    E112(false, 3, 2, BIG_EYE),

    /* E4 shapes */
    E1122(false, 4, 3, PROBABLE_TWO_EYES),
    E1113(false, 4, 1, BIG_EYE),
    E2222(false, 4, 1, SINGLE_EYE),

    /* E5 shapes */
    E11222(true, 5, 7, GUARANTEED_TWO_EYES),
    E11123(false, 5, 1, PROBABLE_TWO_EYES),
    E11114(false, 5, 1, BIG_EYE),
    E12223(false, 5, 1, BIG_EYE),

    /* E6 shapes */
    E112222(true, 6, 13, GUARANTEED_TWO_EYES),
    E111223(true, 6, 12, GUARANTEED_TWO_EYES),
    E111133(true, 6, 1, GUARANTEED_TWO_EYES),
    E112233(false, 6, 4, PROBABLE_TWO_EYES),  // a,b
    E122223(false, 6, 2, PROBABLE_TWO_EYES),
    E112224(false, 6, 1, BIG_EYE),
    E111124(false, 6, 1, PROBABLE_TWO_EYES),
    E222233(false, 6, 1, PROBABLE_TWO_EYES),

    /* E7 shapes */
    E1122222(true, 7, 30, GUARANTEED_TWO_EYES),
    E1112223(true, 7, 40, GUARANTEED_TWO_EYES),
    E1122233(true, 7, 11, GUARANTEED_TWO_EYES),
    E1111233(true, 7, 8, GUARANTEED_TWO_EYES),
    E1222223(true, 7, 5, GUARANTEED_TWO_EYES),
    E1111224(true, 7, 4, GUARANTEED_TWO_EYES),
    E1112333(true, 7, 2, GUARANTEED_TWO_EYES),
    E1222333(true, 7, 2, GUARANTEED_TWO_EYES),
    E1112234(false, 7, 2, PROBABLE_TWO_EYES),   // a,b
    E1222234(false, 7, 1,BIG_EYE ),
    E1122224(false, 7, 1,PROBABLE_TWO_EYES),
    E2222224(false, 7, 1, BIG_EYE),

    /** Usually 2 or more eyes, but may be none or one in some rare cases. */
    TerritorialEye(true, 8, 0, TERRITORIAL_EYE);

    private boolean life;
    private byte size;
    private byte numPatterns;
    private float eyeValue;
    private boolean canBeSideEye;
    private boolean canBeCornerEye;
    private byte numVitalPoints;
    private byte numEndPoints;


    /**
     * constructor
     */
    EyeType(boolean life, int eyeSize, int numPatterns, float eyeValue
             /*, boolean canBeSideEye, boolean canBeCornerEye*/) {
        this.life = life;
        this.size = (byte)eyeSize;
        this.numPatterns = (byte)numPatterns;
        this.eyeValue = eyeValue;
    }

    /**
     * @return the number of spaces in they eye (maybe be filled with some enemy stones).
     */
    public byte getSize()    {
        return size;
    }

    /**
     * The life property should be regarded as a property slightly below Benson’s definition
     * of unconditional life, because if we have an AliveInAtari status for an eye, it might be
     * necessary to play inside the eye, but with the great advantage that detecting it
     * is just a matter of counting neighbours as it will be shown in Section 4.
     * @return true if the shape has the life property
     */
    public boolean hasLife() {
        return life;
    }

    /**
     * @return The number of different ways this eye pattern can occur (independent of symmetries)
     */
    public byte getNumPatterns() {
        return numPatterns;
    }

    /**
     * @return score contribution for eye.   About 1 for single eye, 2 for 2 real eyes.
     */
    public float getEyeValue() {
        return eyeValue;
    }
}