package com.becker.simulation.snake.data;


/**
 * Snake geometry data
 * it is defined by the width of the transverse cross-sectional edges (of which there are num segments+1)
 * the length of each segment is the same as its longer width
 *
 *  @author Barry Becker
 */
public final class FatSnakeData implements ISnakeData {

    /** The widths starting at the nose and edging at the tip of the tail  */
    private static final double[] WIDTHS = {
        9.0, 20.0, 10.0, 15.0, 20.0, 25.0, 30.0, 34.0,
        37.0, 40.0, 42.0, 44.0, 45.0, 46.0, 47.0, 48.0,
        48.0, 48.0, 48.0, 47.0, 46.0, 45.0, 44.0, 42.0, 40.0,
        38.0, 36.0, 34.0, 31.0, 28.0, 24.0, 18.0, 14.0,
        10.0, 6.0, 2.0
    };

    public int getNumSegments() {
       return 35;
    }

    public double getSegmentLength() {
       return 22;
    }

    public double[] getWidths() {
        return WIDTHS;
    }
}
