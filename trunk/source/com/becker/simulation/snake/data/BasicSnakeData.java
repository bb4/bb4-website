package com.becker.simulation.snake.data;


/**
 * Snake geometry data
 * it is defined by the width of the transverse cross-sectional edges (of which there are num segments+1)
 * the length of each segment is the same as its longer width
 *
 *  @author Barry Becker
 */
public final class BasicSnakeData implements ISnakeData {

    /** The widths starting at the nose and edging at the tip of the tail  */
    private static final double[] WIDTHS = {
        9.0, 18.0, 12.0, 13.0, 15.0, 17.0, 18.0, 19.0,
        20.0, 20.5, 21.0, 21.0, 21.0, 21.0, 21.0, 21.0,
        21.0, 21.0, 21.0, 21.0, 21.0, 20.0, 19.5, 19.0,
        18.0, 17.0, 16.0, 15.0, 14.0, 13.0, 12.0, 10.0,
        8.0, 6.0, 4.0
    };

    public int getNumSegments() {
       return 34;
    }

    public double getSegmentLength() {
       return 20;
    }

    public double[] getWidths() {
        return WIDTHS;
    }
}
