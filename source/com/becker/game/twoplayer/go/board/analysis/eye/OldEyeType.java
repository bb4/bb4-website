package com.becker.game.twoplayer.go.board.analysis.eye;

import com.becker.game.twoplayer.go.board.GoEye;

import static com.becker.game.twoplayer.go.board.analysis.eye.EyeShapeScores.*;
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
public enum OldEyeType 
{
    /** False eye always have the potential to become no eyes */
    FalseEye(false, 0, 0, FALSE_EYE),

    /* E1, E2, E3 shapes */
    E0(false, 1, 1, SINGLE_EYE),
    E11(false, 2, 1, SINGLE_EYE),
    E112(false, 3, 2, BIG_EYE) {
        @Override
        public float[] getVitalPoints() { return new float[] {2.02f}; }
    },

    /* E4 shapes */
    E1122(false, 4, 3, PROBABLE_TWO_EYES) {
        @Override
        public float[] getVitalPoints() { return new float[] {2.03f, 2.03f}; }
    },
    E1113(false, 4, 1, BIG_EYE) {
        @Override
        public float[] getVitalPoints() { return new float[] {1.03f}; }
    },
    E2222(false, 4, 1, SINGLE_EYE),


    /* E5 shapes */
    E11222(true, 5, 7, GUARANTEED_TWO_EYES),
    E11123(false, 5, 1, PROBABLE_TWO_EYES) {
        @Override
        public float[] getVitalPoints() { return new float[] {3.04f, 2.04f}; }
        @Override
        public float[] getEndPoints() { return new float[] {1.02f}; }
    },
    E11114(false, 5, 1, BIG_EYE) {
        @Override
        public float[] getVitalPoints() { return new float[] {4.04f}; }
    },
    E12223(false, 5, 1, BIG_EYE) {
        @Override
        public float[] getVitalPoints() { return new float[] {3.05f}; }
        @Override
        public float[] getEndPoints() { return new float[] {2.04f}; }
    },


    /* E6 shapes */
    E112222(true, 6, 13, GUARANTEED_TWO_EYES),
    E111223(true, 6, 12, GUARANTEED_TWO_EYES),
    E111133(true, 6, 1, GUARANTEED_TWO_EYES),
    E112233(false, 6, 4, PROBABLE_TWO_EYES),     // has 2 sub-variants a, b
    E112233a(false, 6, 4, PROBABLE_TWO_EYES) {
        @Override
        public float[] getVitalPoints() { return new float[] {3.06f, 3.06f}; }
        @Override
        public float[] getEndPoints() { return new float[] {1.03f, 1.03f}; }
    },
    E112233b(false, 6, 4, PROBABLE_TWO_EYES) {
        @Override
        public float[] getVitalPoints() { return new float[] {3.05f, 3.05f, 2.06f, 2.06f}; }
    },
    E122223(false, 6, 2, PROBABLE_TWO_EYES) {
        @Override
        public float[] getVitalPoints() { return new float[] {2.04f, 3.06f}; }
        @Override
        public float[] getEndPoints() { return new float[] {1.02f}; }
    },
    E112224(false, 6, 1, BIG_EYE) {
        @Override
        public float[] getVitalPoints() { return new float[] {4.06f}; }
        @Override
        public float[] getEndPoints() { return new float[] {2.04f}; }
    },
    E111124(false, 6, 1, PROBABLE_TWO_EYES) {
        @Override
        public float[] getVitalPoints() { return new float[] {2.05f, 4.05f}; }
        @Override
        public float[] getEndPoints() { return new float[] {1.02f}; }
    },
    E222233(false, 6, 1, PROBABLE_TWO_EYES) {
        @Override
        public float[] getVitalPoints() { return new float[] {3.07f, 3.07f}; }
    },


    /* E7 shapes */
    E1122222(true, 7, 30, GUARANTEED_TWO_EYES),
    E1112223(true, 7, 40, GUARANTEED_TWO_EYES),
    E1122233(true, 7, 11, GUARANTEED_TWO_EYES),
    E1111233(true, 7, 8, GUARANTEED_TWO_EYES),
    E1222223(true, 7, 5, GUARANTEED_TWO_EYES),
    E1111224(true, 7, 4, GUARANTEED_TWO_EYES),
    E1112333(true, 7, 2, GUARANTEED_TWO_EYES),
    E1222333(true, 7, 2, GUARANTEED_TWO_EYES),
    E1112234(false, 7, 2, PROBABLE_TWO_EYES),  // has 2 sub-variants a, b
    E1112234a(false, 7, 2, PROBABLE_TWO_EYES) {
        @Override
        public float[] getVitalPoints() { return new float[] {2.07f, 3.05f, 4.06f, 2.07f}; }
        @Override
        public float[] getEndPoints() { return new float[] {1.03f}; }
    },
    E1112234b(false, 7, 2, PROBABLE_TWO_EYES) {
        @Override
        public float[] getVitalPoints() { return new float[] {3.07f, 4.07f}; }
        @Override
        public float[] getEndPoints() { return new float[] {1.03f}; }
    },
    E1222234(false, 7, 1,BIG_EYE ) {
        @Override
        public float[] getVitalPoints() { return new float[] {4.08f}; }
        @Override
        public float[] getEndPoints() { return new float[] {1.04f}; }
    },
    E1122224(false, 7, 1,PROBABLE_TWO_EYES) {
        @Override
        public float[] getVitalPoints() { return new float[] {2.05f, 4.07f}; }
        @Override
        public float[] getEndPoints() { return new float[] {1.02f}; }
    },
    E2222224(false, 7, 1, BIG_EYE) {
        @Override
        public float[] getVitalPoints() { return new float[] {4.10f}; }
        @Override
        public float[] getEndPoints() { return new float[] {1.04f, 1.04f}; }
    },

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
    OldEyeType(boolean life, int eyeSize, int numPatterns, float eyeValue
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
    public boolean hasLifeProperty() {
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

    /**
     * A list of vital points described by indices.
     * @return list of vital points where a point is described by an index created by adding the number of
     * neighbors to (those neighvor's neighbors)/100. so for example, 2.03 means the space has 2 nobi neighbors
     * and those 2 neighbors have a total of 3 neighbors.
     * Returns an empty array if we have no vitals or the type has the life property.
     */
    public float[] getVitalPoints() {
        return new float[] {};
    }

    /**
     * A list of end points described by indices.  End points are bad to play by either side until all the other eye
     * spaces have been played. If the opponent plays them they are not playing in the nakade (big eye) shape, and
     * hence missing a likely opportunity to kill the group. If the same color plays them they are helping to create a
     * nakade eye shape.  End points are the spaces left after the nakade shape of size n-1 fills the eye.
     * @return list of end points where a point is described by an index created by adding the number of
     * neighbors to (those neighvor's neighbors)/100. so for example, 2.03 means the space has 2 nobi neighbors
     * and those 2 neighbors have a total of 3 neighbors.
     * Returns an empty array if we have no vitals or the type has the life property.
     */
    public float[] getEndPoints() {
        return new float[] {};
    }
}