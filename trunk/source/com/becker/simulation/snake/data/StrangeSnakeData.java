package com.becker.simulation.snake.data;


/**
 * Snake geometry data
 * it is defined by the width of the transverse cross-sectional edges (of which there are num segments+1)
 * the length of each segment is the same as its longer width
 *
 *  @author Barry Becker
 */
public final class StrangeSnakeData implements ISnakeData {

    /** The widths starting at the nose and edging at the tip of the tail  */
    private static final double[] WIDTHS = {
        9.0, 20.0, 10.0, 11.0, 12.0, 14.0, 17.0, 20.0,
        23.0, 26.0, 28.0, 30.0, 29.0, 27.0, 26.0, 24.0,
        22.0, 21.0, 20.0, 21.0, 22.0, 24.0, 26.0, 27.0, 26.0,
        25.0, 23.0, 21.0, 19.0, 17.0, 15.0, 13.0, 11.0,
        9.0, 6.0, 2.0
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
