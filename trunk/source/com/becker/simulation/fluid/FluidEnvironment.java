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

    private Grid grid_;
    
    private static float DIFFUSION_RATE = 0.1f;
    private static float VISCOSITY = 0.0f;

    public FluidEnvironment(int dimX, int dimY) {
        dimX_ = dimX;
        dimY_ = dimY;
        System.out.println("x="+dimX+" y="+ dimY);
        
        grid_ = new Grid(dimX, dimX);
    }
    
    public Grid getGrid() {
        return grid_;
    }
    
    public int getWidth() {
        return dimX_ + 2;
    }
    public int getHeight() {
        return dimX_ + 2;
    }

    public int getXDim() {
        return dimX_;
    }
    public int getYDim()  {
        return dimX_;
    }
    
    
    private int IX(int i, int j) {
        return  i + (dimX_+2) * j;
    }

    /**
     * Advance a timeStep 
     */
    public synchronized double stepForward( double timeStep) {
        vel_step ( grid_.u, grid_.v,  VISCOSITY, (float) timeStep );
	dens_step ( grid_.dens,  grid_.u[1], grid_.v[1], DIFFUSION_RATE, (float) timeStep );
        return timeStep;
    }
 
    
    /**
     *replaces #define SWAP(x0,x) {float * tmp=x0;x0=x;x=tmp;}
     */
    private void swap(float[][] x) {
        float[] temp = x[0];
        x[0] = x[1];
        x[1] = temp;
    }
    
    private void add_source(float[][] x,  float dt )
    {
        
        int size = (dimX_+2)*(dimX_+2);
        for (int i=0 ; i<size ; i++ ) 
            x[1][i] += dt * x[0][i];
    }

    private void set_bnd(int b, float[] x )
    {
            for (int i=1 ; i<=dimX_ ; i++ ) {
                x[IX(0, i)] = b==1 ? -x[IX(1, i)] : x[IX(1, i)];
                x[IX(dimX_+1, i)] = b==1 ? -x[IX(dimX_, i)] : x[IX(dimX_, i)];
                x[IX(i, 0)] = b==2 ? -x[IX(i, 1)] : x[IX(i, 1)];
                x[IX(i, dimX_+1)] = b==2 ? -x[IX(i, dimX_)] : x[IX(i, dimX_)];
            }
            x[IX(0  , 0  )] = 0.5f*(x[IX(1, 0  )]+x[IX(0  , 1)]);
            x[IX(0  , dimX_+1)] = 0.5f*(x[IX(1, dimX_+1)]+x[IX(0  , dimX_)]);
            x[IX(dimX_+1, 0  )] = 0.5f*(x[IX(dimX_, 0  )]+x[IX(dimX_+1, 1)]);
            x[IX(dimX_+1, dimX_+1)] = 0.5f*(x[IX(dimX_, dimX_+1)]+x[IX(dimX_+1, dimX_)]);
    }

    private void lin_solve( int b, float[] x, float[] x0, float a, float c )
    {
           for ( int k=0 ; k<20 ; k++ ) {
                for ( int i=1 ; i<=dimX_ ; i++ ) { 
                    for ( int j=1 ; j<=dimX_ ; j++ ) {                    
                        x[IX(i,j)] = (x0[IX(i,j)] + a*(x[IX(i-1,j)]+x0[IX(i+1,j)]+x[IX(i,j-1)]+x[IX(i,j+1)]))/c;
                    }
                }
                set_bnd ( b, x );
            }
    }

    private void diffuse( int b, float[][] x, float diff, float dt )
    {
            float a = dt * diff * dimX_ * dimX_;
            lin_solve( b, x[1], x[0], a, 1+4*a );
    }

    private void advect( int b, float [][] d, float[] u, float[] v, float dt )
    {
            int i, j, i0, j0, i1, j1;
            float x, y, s0, t0, s1, t1, dt0;

            dt0 = dt * dimX_;
            for ( i=1 ; i<=dimX_ ; i++ ) { 
                for ( j=1 ; j<=dimX_ ; j++ ) {            
                    x = i - dt0 * u[IX(i, j)]; 
                    y = j - dt0 * v[IX(i, j)];
                    if (x<0.5f) x=0.5f; 
                    if (x>dimX_+0.5f) x=dimX_+0.5f; 
                    i0=(int)x; i1=i0+1;
                    if (y<0.5f) y=0.5f;
                    if (y>dimX_+0.5f) y=dimX_+0.5f;
                    j0=(int)y; j1=j0+1;
                    s1 = x - i0; 
                    s0 = 1 - s1;
                    t1 = y - j0; 
                    t0 = 1 - t1;
                    d[1][IX(i,j)] = s0*(t0*d[0][IX(i0,j0)]+t1*d[0][IX(i0,j1)])+
                                             s1*(t0*d[0][IX(i1,j0)]+t1*d[0][IX(i1,j1)]);
                }
            }
            set_bnd ( b, d[1] );
    }

    private void project( float[] u, float[] v, float[] p, float[] div )
    {
            int i, j;

            for ( i=1 ; i<=dimX_ ; i++ ) { 
                for ( j=1 ; j<=dimX_ ; j++ ) {            
                    div[IX(i,j)] = -0.5f*(u[IX(i+1,j)]-u[IX(i-1,j)]+v[IX(i,j+1)]-v[IX(i,j-1)])/dimX_;
                    p[IX(i,j)] = 0;
                }
            }	
            set_bnd ( 0, div ); 
            set_bnd ( 0, p );

            lin_solve ( 0, p, div, 1, 4 );

            for ( i=1 ; i<=dimX_ ; i++ ) { 
                for ( j=1 ; j<=dimX_ ; j++ ) {            
                    u[IX(i,j)] -= 0.5f*dimX_*(p[IX(i+1,j)]-p[IX(i-1,j)]);
                    v[IX(i,j)] -= 0.5f*dimX_*(p[IX(i,j+1)]-p[IX(i,j-1)]);
                }
            }
            set_bnd ( 1, u ); set_bnd ( 2, v );
    }

    private void dens_step( float[][] x, float[] u, float [] v, float diff, float dt )
    {
            //add_source( x, dt );
            swap( x ); 
            diffuse( 0, x, diff, dt );
            swap( x ); 
            advect( 0, x, u, v, dt );
    }

    private void vel_step( float[][] u, float[][] v, float visc, float dt )
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
