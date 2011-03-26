package com.becker.simulation.liquid.model;

import javax.vecmath.Vector2d;

/**
 *  This is the global space containing all the cells, walls, and particles
 *  Assumes an M*N grid of cells.
 *  X axis increases to the left
 *  Y axis increases downwards to be consistent with java graphics.
 *  adapted from work by Nick Foster.
 *  See
 *  http://physbam.stanford.edu/~fedkiw/papers/stanford2001-02.pdf
 *
 *  @author Barry Becker
 */
public class Grid {

    // the dimensions of the space
    private int xDim_;
    private int yDim_;

    /** the grid of cells that make up the environment in x,y (col, row) order */
    private Cell[][] grid_ = null;


    /**
     * Constructor to use if you want the environment based on a config file.
     */
    public Grid(int xDim, int yDim) {

        xDim_ = xDim;
        yDim_ = yDim;
        grid_ = new Cell[xDim][yDim];

        for (int j = 0; j < yDim_; j++ ) {
            for (int i = 0; i < xDim; i++ ) {
                grid_[i][j] = new Cell();
            }
        }
    }

    public Cell getCell(int i, int j) {
        return grid_[i][j];
    }

    public CellNeighbors getNeighbors(int i, int j) {
        return new CellNeighbors(grid_[i + 1][j],   grid_[i - 1][j],
                                 grid_[i][j + 1],   grid_[i][j - 1] );
    }

    public int getXDimension() {
        return xDim_;
    }

    public int getYDimension() {
        return yDim_;
    }

    public void setVelocity(int i, int j, Vector2d velocity) {
        grid_[i][j].initializeU(velocity.x);
        grid_[i][j].initializeV(velocity.y);
    }

    /**
     * Update the cell status for all the cells in the grid.
     */
    public void updateCellStatus() {
        int i, j;
        for ( j = 1; j < yDim_ - 1; j++ ) {
            for ( i = 1; i < xDim_ - 1; i++ ) {
                grid_[i][j].updateStatus(getNeighbors(i,j));
            }
        }
    }

    /**
     * setup the obstacles.
     */
    public void setBoundaries() {

        // right and left
        for ( int j = 0; j < yDim_; j++ ) {
            grid_[0][j].setStatus( CellStatus.OBSTACLE );
            grid_[xDim_ - 1][j].setStatus( CellStatus.OBSTACLE );
        }
        // top and bottom
        for ( int i = 0; i < xDim_; i++ ) {
            grid_[i][0].setStatus( CellStatus.OBSTACLE );
            grid_[i][yDim_ - 1].setStatus( CellStatus.OBSTACLE );
        }
    }

    /**
     * Set OBSTACLE condition of stationary objects, inflow/outflow.
     */
    public void setBoundaryConstraints() {

        // right and left
        for (int j = 0; j < yDim_; j++ ) {
            // left
            Cell n = grid_[1][j];
            grid_[0][j].setPressure( n.getPressure() );
            grid_[0][j].initializeVelocity(0, n.getV());   // -n.getVjP ???
            // right
            n = grid_[xDim_ - 2][j];
            grid_[xDim_ - 1][j].setPressure( n.getPressure() );
            grid_[xDim_ - 1][j].initializeVelocity(0, n.getV());  // -n.getVip()
            grid_[xDim_ - 2][j].initializeU(0);
        }

        // top and bottom
        for (int i = 0; i < xDim_; i++ ) {
            // bottom
            Cell n = grid_[i][1];
            grid_[i][0].setPressure( n.getPressure() );
            grid_[i][0].initializeVelocity(n.getU(), 0); // -n.getUip() ???
            // top
            n = grid_[i][yDim_ - 2];
            grid_[i][yDim_ - 1].setPressure( n.getPressure() );
            grid_[i][yDim_ - 1].initializeVelocity(n.getU(), 0);  // -n.getUip()
            grid_[i][yDim_ - 2].initializeV(0);
        }      
    }
}
