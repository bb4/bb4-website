package com.becker.simulation.fluid;

/**
 * Data behind the Fluid.
 * @author Barry Becker
 */
public class Grid {
    
    private int dimX_;
    private int dimY_;
    
    public float u[][][];
    public float v[][][];
    public float dens[][][];
    
    /**
     * Creates a new instance of Grid
     */
    public Grid(int dimX, int dimY) {
        
        dimX_ = dimX;
        dimY_ = dimY;
 
	u   =  new float[2][dimX_ + 2][dimY_ + 2];  
	v     =  new float[2][dimX_ + 2][dimY_ + 2];  
	dens = new float[2][dimX_ + 2][dimY_ + 2];  
        
        addInitialInkDensity();                
    }

    private void addInitialInkDensity() {
        for ( int i=2; i<dimX_/2; i++) {
            for ( int j=2; j<dimY_/2; j++) {
                u[1][i][j] = (float)(0.01 + (float)(Math.cos(0.4*i)+Math.sin(0.3*j))/10.0);
                v[1][i][j] = (float)(.1 - (float)(Math.sin(.2*i) + Math.sin(0.1 * j)/10.0));
                dens[1][i][j] = (float)(0.1 -  (float)Math.sin((i + j)/4.0)/10.0);
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
        return u[1][i][j];
    }
    
     public float getV(int i, int j) {
        return v[1][i][j];
    }
     
     public float getDensity(int i, int j) {
        return dens[1][i][j];
    }
    
     public void incrementU(int i, int j, float value) {
        u[0][i][j] += value;
    }
    
     public void incrementV(int i, int j, float value) {
        v[0][i][j] += value;
    }
     
     public void incrementDensity(int i, int j, float value) {
        dens[0][i][j]+= value;        
    }
}
