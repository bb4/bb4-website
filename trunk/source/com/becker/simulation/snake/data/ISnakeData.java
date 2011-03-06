package com.becker.simulation.snake.data;


/**
 * Snake geometry data
 * it is defined by the width of the transverse cross-sectional edges (of which there are num segments+1)
 * the length of each segment is the same as its longer width
 *
 *  @author Barry Becker
 */
public interface ISnakeData {

    int getNumSegments();

    double getSegmentLength();

    double[] getWidths();
}
