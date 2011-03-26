package com.becker.simulation.liquid.model;

/**
 *  The cells x and y dimensions. Should be square.
 *
 *  @author Barry Becker
 */
public class CellDimensions {

    public static final double CELL_SIZE = 10.0;

    /** size of a cell */
    public final double dx;
    public final double dy;

    /** squares of edge lengths */
    public final double dxSq;
    public final double dySq;

    /**
     * constructor
     */
    public CellDimensions()  {

        // cell dimensions
        dx = dy = CELL_SIZE;
        dxSq = dx * dx;
        dySq = dy * dy;
    }
}