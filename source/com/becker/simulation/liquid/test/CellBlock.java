package com.becker.simulation.liquid.test;

import com.becker.simulation.liquid.*;

/**
 * A 3x3 block of cells for testing purposes
 * @author Barry Becker Date: Aug 12, 2006
 */
public class CellBlock {

    private static final int DIM = 3;
    private Cell[][] block_;

    public CellBlock() {
        block_ = new Cell[DIM][DIM];

        for (int i = 0; i<DIM; i++)  {
           for (int j = 0; j<DIM; j++) {
               block_[i][j] = new Cell();
           }
        }
        updateCenterStatus();
    }

    /**
     *Gets a relative cell. Relative from center
     * e.g. get(0,0) returns the center cell.
     * @return cell relative to center of the block
     */
    public Cell get(int offsetX, int offsetY) {
        return block_[offsetX + 1][offsetY + 1];
    }
    
    public Cell getAbsolute(int x, int y) {
        return block_[x][y];
    }

    public void setPressures(double p) {
        for (int i = 0; i<DIM; i++)
           for (int j = 0; j<DIM; j++)
               block_[i][j].setPressure(p);
    }
    
    public void setVelocities(double u, double v) {
        for (int i = 0; i<DIM; i++)
           for (int j = 0; j<DIM; j++)
               block_[i][j].setVelocityP(u, v);
    }

    public void setCellParticles(int numParticlesPerCell) {
        for (int i = 0; i<DIM; i++){
           for (int j = 0; j<DIM; j++) {
               Cell cell = block_[i][j];
               int n = cell.getNumParticles();
               for (int k = 0; k<n; k++) {
                   cell.decParticles();
               }
               for (int k = 0; k<numParticlesPerCell; k++) {
                   cell.incParticles();
               }
           }
        }
    }

    public void updateCenterStatus() {
        block_[1][1].updateStatus(get(1,0), get(-1,0), get(0,1), get(0,-1));
    }
}

