package com.becker.simulation.fluid.model;

import com.becker.simulation.common.Profiler;

/**
 *  this is the global space containing all the cells, walls, and particles
 *  Assumes an M*N grid of cells.
 *  X axis increases to the left
 *  Y axis increases downwards to be consistent with java graphics
 *  adapted from work by Nick Foster and Jos Stam.
 *
 *  Improvements:
 *    - increase performance by only keeping track of particles near the surface.
 *    - allow configuring walls from file
 *
 *  @author Jos Stam, ported to java by Barry Becker
 */
public class FluidEnvironment {

    // the dimensions of the space in the X/y and Y/v directions
    private int dimX_;
    private int dimY_;

    /** the cells that the liquid will flow through   */
    private Grid grid_;
    
    public static final double DEFAULT_DIFFUSION_RATE = 0.0f;
    public static final double DEFAULT_VISCOSITY = 0.0f;
    
    private double diffusionRate_ = DEFAULT_DIFFUSION_RATE;
    private double viscosity_ = DEFAULT_VISCOSITY;

    /**
     * Constructor
     */
    public FluidEnvironment(int dimX, int dimY) {
        dimX_ = dimX;
        dimY_ = dimY;
        
        grid_ = new Grid(dimX_, dimY_);
    }

    /** reset to original state */
    public void reset() {
        grid_ = new Grid(dimX_, dimY_);
    }
    
    public Grid getGrid() {
        return grid_;
    }
    
    public int getWidth() {
        return dimX_ + 2;
    }
    public int getHeight() {
        return dimY_ + 2;
    }

    public int getXDim() {
        return dimX_;
    }
    public int getYDim()  {
        return dimY_;
    }
    
    public synchronized void setDiffusionRate(double rate) {
        diffusionRate_ = rate;
    }
    
    public synchronized void setViscosity(double v) {
        viscosity_ = v;
    }

    /**
     * Advance a timeStep
     * @return the new timeStep (does not change in this case)
     */
    public double stepForward( double timeStep) {
        Profiler.getInstance().startCalculationTime();
        velocityStep(grid_.u, grid_.v, (float) viscosity_, (float) timeStep);
        densityStep(grid_.dens, grid_.u[1], grid_.v[1], (float) diffusionRate_, (float) timeStep);
        Profiler.getInstance().stopCalculationTime();
        return timeStep;
    }
 
    
    /**
     * Swap x[0] and x[1]
     */
    private void swap(float[][][] x) {
        float[][] temp = x[0];
        x[0] = x[1];
        x[1] = temp;
    }

    /**
     * Add a fluid source to the environment
     */
    private void addSource(float[][][] x, float dt) {
        
        for (int i = 0 ; i < dimX_+2 ; i++ ) {
            for (int j = 0 ; j < dimY_ + 2 ; j++ ) {
                x[1][i][j] += dt * x[0][i][j];
            }
        }
    }

    /**
     * Set a boundary to contain the liquid.
     * @param b
     * @param x
     */
    private void setBoundary(int b, float[][] x)  {

        for (int i=1 ; i<=dimX_ ; i++ ) {
            x[i][0] = b==2 ? -x[i][1] : x[i][1];
            x[i][dimY_+1] = b==2 ? -x[i][dimY_] : x[i][dimY_];
        }
        for (int i=1 ; i<=dimY_ ; i++ ) {
            x[0][i] = b==1 ? -x[1][i] : x[1][i];
            x[dimX_+1][i] = b==1 ? -x[dimX_][i] : x[dimX_][i];
        }

        x[0 ][ 0] = 0.5f * (x[1][0] + x[0][1]);
        x[0 ][dimY_+1] = 0.5f * (x[1][dimY_+1] + x[0][dimY_]);
        x[dimX_+1][0] = 0.5f * (x[dimX_][0] + x[dimX_+1][1]);
        x[dimX_+1][dimY_+1] = 0.5f*(x[dimX_][dimY_+1] + x[dimX_+1][dimY_]);
    }


    /**
     * Solve the system
     */
    private void linearSolve(int b, float[][] x, float[][] x0, float a, float c) {

       for ( int k=0 ; k<20 ; k++ ) {
            for ( int i=1 ; i<=dimX_ ; i++ ) {
                for ( int j=1 ; j<=dimY_ ; j++ ) {
                    x[i][j] = (x0[i][j] + a*(x[i-1][j]+x0[i+1][j]+x[i][j-1]+x[i][j+1])) / c;
                }
            }
            setBoundary(b, x);
        }
    }

    /** Diffuse the pressure. */
    private void diffuse( int b, float[][][] x, float diff, float dt ) {
            float a = dt * diff * dimX_ * dimY_;
            linearSolve(b, x[1], x[0], a, 1 + 4 * a);
    }

    /**
     * Advect the fluid in the field.
     */
    private void advect( int b, float [][][] d, float[][] u, float[][] v, float dt )  {

        float dt0 = dt * dimX_;
        for ( int i=1 ; i <= dimX_ ; i++ ) {
            for ( int j=1 ; j <= dimY_ ; j++ ) {
                float x = i - dt0 * u[i][j];
                float y = j - dt0 * v[i][j];
                if (x < 0.5f) {
                    x=0.5f;
                }
                if (x > dimX_+0.5f)  {
                    x = dimX_+0.5f;
                }
                int i0=(int)x;
                int i1=i0+1;
                if (y < 0.5f) {
                    y = 0.5f;
                }
                if (y > dimY_+0.5f) {
                    y = dimY_+0.5f;
                }
                int j0=(int)y;
                int j1=j0+1;
                float s1 = x - i0;
                float s0 = 1 - s1;
                float t1 = y - j0;
                float t0 = 1 - t1;
                d[1][i][j] = s0 * (t0 * d[0][i0][j0] + t1 * d[0][i0][j1]) +
                             s1 * (t0 * d[0][i1][j0] + t1 * d[0][i1][j1]);
            }
        }
        setBoundary(b, d[1]);
    }

    /** project the fluid */
    private void project( float[][] u, float[][] v,
                          float[][] p, float[][] div )   {
        int i, j;

        for ( i =1 ; i <= dimX_ ; i++ ) {
            for ( j =1 ; j <= dimY_; j++ ) {
                div[i][j] = -(u[i+1][j] - u[i-1][j] + v[i][j+1] - v[i][j-1]) / (dimX_+ dimY_);
                p[i][j] = 0;
            }
        }
        setBoundary(0, div);
        setBoundary(0, p);

        linearSolve(0, p, div, 1, 4);

        for ( i=1 ; i<=dimX_ ; i++ ) {
            for ( j=1 ; j<=dimY_ ; j++ ) {
                u[i][j] -= 0.5f * dimX_ * (p[i+1][j] - p[i-1][j]);
                v[i][j] -= 0.5f * dimY_  *(p[i][j+1] - p[i][j-1]);
            }
        }
        setBoundary(1, u);
        setBoundary(2, v);
    }

    private void densityStep(float[][][] x, float[][] u, float[][] v, float diff, float dt) {
        //addSource( x, dt );
        swap( x );
        diffuse( 0, x, diff, dt );
        swap( x );
        advect( 0, x, u, v, dt );
    }

    private void velocityStep(float[][][] u, float[][][] v, float visc, float dt) {
        //addSource( u, dt );
        //addSource( v, dt );
        swap( u );
        diffuse( 1, u, visc, dt );
        swap( v );
        diffuse( 2, v, visc, dt );
        project( u[1], v[1], u[0], v[0] );
        swap( u );
        swap( v );
        advect( 1, u, u[0], v[0], dt );
        advect( 2, v, u[0], v[0], dt );
        project( u[1], v[1], u[0], v[0] );
    }

}
