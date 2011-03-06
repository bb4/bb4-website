package com.becker.simulation.snake.data;


/**
 * Snake geometry data
 * it is defined by the width of the transverse cross-sectional edges (of which there are num segments+1)
 * the length of each segment is the same as its longer width
 *
 *  @author Barry Becker
 */
public final class BumpySnakeData implements ISnakeData {

    private static final double BUMP_TROUGH = 14;
    private static final double BUMP_1 = 20;
    private static final double BUMP_2 = 40;
    private static final double BUMP_PEAK = 48;

    /** The widths starting at the nose and edging at the tip of the tail  */
    private static final double[] WIDTHS = {
        9.0, 22.0, 10.0, 13.0, 17.0, 22.0, 30.0, BUMP_2, BUMP_PEAK, BUMP_2, BUMP_1,
        BUMP_TROUGH, BUMP_1, BUMP_2, BUMP_PEAK, BUMP_2, BUMP_1,
        BUMP_TROUGH, BUMP_1, BUMP_2, BUMP_PEAK, BUMP_2, BUMP_1,
        BUMP_TROUGH, BUMP_1, BUMP_2, BUMP_PEAK, BUMP_2, BUMP_1,
        BUMP_TROUGH, BUMP_1, BUMP_2, BUMP_PEAK,
        36.0, 31.0, 26.0, 22.0, 18.0, 14.0,
        10.0, 6.0, 2.0
    };

    public int getNumSegments() {
       return 42;
    }

    public double getSegmentLength() {
       return 22;
    }

    public double[] getWidths() {
        return WIDTHS;
    }
}
