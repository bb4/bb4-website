package com.becker.simulation.snake;



/**
 * snake geometry data
 * it is defined by the width of the transvers cross-sectional edges (of which there are num segments+1)
 * the length of each segment is the same as its longer width
 *
 *  @author Barry Becker
 */
public final class SnakeData
{

    public static final int NUM_SEGMENTS = 34;

    public static final double SEGMENT_LENGTH = 20;

    private SnakeData() {};

    // now the widths starting at the nose and edging at the tip of the tail
    public static final double[] WIDTHS = {
        9.0, 18.0, 12.0, 13.0, 15.0, 17.0, 18.0, 19.0,
        20.0, 20.5, 21.0, 21.0, 21.0, 21.0, 21.0, 21.0,
        21.0, 21.0, 21.0, 21.0, 21.0, 20.0, 19.5, 19.0,
        18.0, 17.0, 16.0, 15.0, 14.0, 13.0, 12.0, 10.0,
        8.0, 6.0, 4.0
    };

}
