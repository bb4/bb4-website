package com.becker.simulation.fluid;

/**
 *
 * Created on September 22, 2007, 8:15 AM
 * @author becker
 */
public class Grid {
    
    private int dimX_;
    private int dimY_;
    
    public float u[][], v[][], dens[][];
    
    private int IX(int i, int j) {
        return  i + (dimX_+2) * j;
    }
    
    /**
     * Creates a new instance of Grid
     */
    public Grid(int dimX, int dimY) {
        
        dimX_ = dimX;
        dimY_ = dimY;
        int size = (dimX_+2)*(dimY_+2);  

	u    =  new float[2][size];   //  (float *) malloc ( size*sizeof(float) );
	v      = new float[2][size];		
	dens  = new float[2][size];	
        
        for ( int i=2; i<dimX_/2; i++) {
            for ( int j=2; j<dimX_/2; j++) {
                u[1][IX(i,j)] = (float)(2.0 + (float)(i+j)/10.0);
                v[1][IX(i,j)] = (float)(3.0 - (float)(i*j)/10.0);
                dens[1][IX(i,j)] = (float)(3.0 -  (float)i/8.0);
            }
        }
    }
    
    public int getXDim() {
        return dimX_;
    }
    
    public int getYDim() {
        return dimY_;
    }
    
    
    public float getU(int i, int j) {
        return u[1][IX(i, j)];
    }
    
     public float getV(int i, int j) {
        return v[1][IX(i, j)];
    }
     
     public float getDensity(int i, int j) {
        return dens[1][IX(i, j)];
    }
    
     public void incrementU(int i, int j, float value) {
        u[0][IX(i, j)] += value;
    }
    
     public void incrementV(int i, int j, float value) {
        v[0][IX(i, j)] += value;
    }
     
     public void incrementDensity(int i, int j, float value) {
        dens[0][IX(i, j)] += value;        
    }
}
