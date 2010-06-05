package com.becker.game.twoplayer.go.board;

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
public enum EyeShape
{
    E0(false, 1),
    E11(false, 1),
    E121(false, 2),

    /* E4 shapes */
    E1122(false, 3),
    E1113(false, 1),
    E2222(false, 1),

    /* E5 shapes */
    E11222(true, 7),
    E11123(false, 1),
    E11114(false, 1),
    E12223(false, 1),

    /* E6 shapes */
    E112222(true, 13),
    E111223(true, 12),
    E111133(true, 1),
    E112233(false, 4),
    E122223(false, 2),
    E112224(false, 1),
    E111124(false, 1),
    E222233(false, 1),

    /* E7 shapes */
    E1122222(true, 30),
    E1112223(true, 40),
    E1122233(true, 11),
    E1111233(true, 8),
    E1222223(true, 5),
    E1111224(true, 4),
    E1112333(true, 2),
    E1222333(true, 2),
    E1112234(false, 2),
    E1222234(false, 1),
    E1122224(false, 1),
    E2222224(false, 1);



    private boolean life;
    private byte numPatterns;


    /**
     * constructor
     *
     */
    EyeShape(boolean life, int numPatterns) {
        this.life = life;
        this.numPatterns = (byte)numPatterns;
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
    public byte numPatterns() {
        return numPatterns;
    }
}