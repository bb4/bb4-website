package com.becker.simulation.fluid;

import com.becker.ui.*;

import javax.vecmath.*;
import java.util.*;

import static com.becker.simulation.common.PhysicsConstants.*;

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
public class FluidEnvironment
{

    // the dimensions of the space in the X/y and Y/v directions
    private int dimX_;
    private int dimY_;

    // the cells that the liquid will flow through
    private Grid grid_;
    
    public static double DEFAULT_DIFFUSION_RATE = 0.0f;
    public static double DEFAULT_VISCOSITY = 0.0f;
    
    private double diffusionRate_ = DEFAULT_DIFFUSION_RATE;
    private double viscosity_ = DEFAULT_VISCOSITY;

    public FluidEnvironment(int dimX, int dimY) {
        dimX_ = dimX;
        dimY_ = dimY;
        
        grid_ = new Grid(dimX_, dimY_);
    }
    
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
    
    public void setDiffusionRate(double rate) {
        diffusionRate_ = rate;
    }
    
    public synchronized void setViscosity(double v) {
        viscosity_ = v;
    }

    /**
     * Advance a timeStep 
     */
    public synchronized double stepForward( double timeStep) {
        vel_step ( grid_.u, grid_.v,  (float)viscosity_, (float) timeStep );
	dens_step ( grid_.dens,  grid_.u[1], grid_.v[1], (float)diffusionRate_, (float) timeStep );
        return timeStep;
    }
 
    
    /**
     *replaces #define SWAP(x0,x) {float * tmp=x0;x0=x;x=tmp;}
     */
    private void swap(float[][][] x) {
        float[][] temp = x[0];
        x[0] = x[1];
        x[1] = temp;
    }
    
    private void add_source(float[][][] x,  float dt )
    {        
        
        for (int i = 0 ; i < dimX_+2 ; i++ ) {
            for (int j = 0 ; j < dimY_ + 2 ; j++ ) {
                x[1][i][j] += dt * x[0][i][j];
            }
        }
    }

    private void set_bnd(int b, float[][] x )
    {
            for (int i=1 ; i<=dimX_ ; i++ ) {
       
                x[i][0] = b==2 ? -x[i][1] : x[i][1];
                x[i][dimY_+1] = b==2 ? -x[i][dimY_] : x[i][dimY_];
            }
             for (int i=1 ; i<=dimY_ ; i++ ) {
                x[0][i] = b==1 ? -x[1][i] : x[1][i];
                x[dimX_+1][i] = b==1 ? -x[dimX_][i] : x[dimX_][i];          
            }
            
            x[0 ][ 0] = 0.5f*(x[1][ 0]+x[0][1]);
            x[0 ][dimY_+1] = 0.5f*(x[1][dimY_+1]+x[0][dimY_]);
            x[dimX_+1][0] = 0.5f*(x[dimX_][0]+x[dimX_+1][1]);
            x[dimX_+1][dimY_+1] = 0.5f*(x[dimX_][dimY_+1]+x[dimX_+1][dimY_]);
    }

    private void lin_solve( int b, float[][] x, float[][] x0, float a, float c )
    {
           for ( int k=0 ; k<20 ; k++ ) {
                for ( int i=1 ; i<=dimX_ ; i++ ) { 
                    for ( int j=1 ; j<=dimY_ ; j++ ) {                    
                        x[i][j] = (x0[i][j] + a*(x[i-1][j]+x0[i+1][j]+x[i][j-1]+x[i][j+1])) / c;
                    }
                }
                set_bnd ( b, x );
            }
    }

    private void diffuse( int b, float[][][] x, float diff, float dt )
    {
            float a = dt * diff * dimX_ * dimY_;
            lin_solve( b, x[1], x[0], a, 1+4*a );
    }

    private void advect( int b, float [][][] d, float[][] u, float[][] v, float dt )  {
            int i, j, i0, j0, i1, j1;
            float x, y, s0, t0, s1, t1, dt0;

            dt0 = dt * dimX_;
            for ( i=1 ; i <= dimX_ ; i++ ) { 
                for ( j=1 ; j <= dimY_ ; j++ ) {            
                    x = i - dt0 * u[i][j]; 
                    y = j - dt0 * v[i][j];
                    if (x<0.5f) {
                        x=0.5f; 
                    }
                    if (x>dimX_+0.5f)  {
                        x=dimX_+0.5f; 
                    }
                    i0=(int)x; i1=i0+1;
                    if (y<0.5f) {
                        y=0.5f;
                    }
                    if (y>dimY_+0.5f) {
                        y=dimY_+0.5f;
                    }
                    j0=(int)y; j1=j0+1;
                    s1 = x - i0; 
                    s0 = 1 - s1;
                    t1 = y - j0; 
                    t0 = 1 - t1;
                    d[1][i][j] = s0*(t0*d[0][i0][j0] + t1*d[0][i0][j1]) +
                                      s1*(t0*d[0][i1][j0] + t1*d[0][i1][j1]);      
                }
            }
            set_bnd ( b, d[1] );
    }

    private void project( float[][] u, float[][] v,
                                       float[][] p, float[][] div )   {
            int i, j;

            for ( i =1 ; i <= dimX_ ; i++ ) { 
                for ( j =1 ; j <= dimY_; j++ ) {            
                    div[i][j] = -(u[i+1][j] - u[i-1][j] + v[i][j+1] - v[i][j-1]) / (dimX_+ dimY_);
                    p[i][j] = 0;
                }
            }	
            set_bnd ( 0, div ); 
            set_bnd ( 0, p );

            lin_solve ( 0, p, div, 1, 4 );

            for ( i=1 ; i<=dimX_ ; i++ ) { 
                for ( j=1 ; j<=dimY_ ; j++ ) {            
                    u[i][j] -= 0.5f * dimX_ * (p[i+1][j] - p[i-1][j]);
                    v[i][j] -= 0.5f * dimY_  *(p[i][j+1] - p[i][j-1]);
                }
            }
            set_bnd ( 1, u ); set_bnd ( 2, v );
    }

    private void dens_step( float[][][] x, float[][] u, float [][] v, float diff, float dt )
    {
            //add_source( x, dt );
            swap( x ); 
            diffuse( 0, x, diff, dt );
            swap( x ); 
            advect( 0, x, u, v, dt );
    }

    private void vel_step( float[][][] u, float[][][] v, float visc, float dt )
    {
            //add_source( u, dt ); 
            //add_source( v, dt );
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
