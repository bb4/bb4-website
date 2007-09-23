package com.becker.simulation.liquid.test;

import javax.vecmath.Vector2d;
import junit.framework.*;
import com.becker.simulation.liquid.*;

/**
 * @author Barry Becker Date: Aug 12, 2006
 */

public class TestCell extends TestCase {

    private static final double VISCOSITY = 0.001; //0.001;
    private static final  double DT = 0.1;

    public void testCellStatus1() {

        CellBlock cb = new CellBlock();
        Cell cell = cb.get(0,0);

        cb.updateCenterStatus();
        Assert.assertTrue( "unexpected status"+ cell.getStatus(), cell.getStatus() == CellStatus.EMPTY);
        cell.incParticles();
        cb.updateCenterStatus();
        Assert.assertTrue( "unexpected status"+ cell.getStatus(), cell.getStatus() == CellStatus.ISOLATED);
        cell.incParticles();
        cell.incParticles();
        cb.updateCenterStatus();
        Assert.assertTrue( "unexpected status"+ cell.getStatus(), cell.getStatus() == CellStatus.ISOLATED);
        cb.get(1, 0).incParticles();
        cb.updateCenterStatus();
        Assert.assertTrue( "unexpected status"+ cell.getStatus(), cell.getStatus() == CellStatus.SURFACE);
        cb.get(-1, 0).incParticles();
        cb.updateCenterStatus();
        Assert.assertTrue( "unexpected status"+ cell.getStatus(), cell.getStatus() == CellStatus.SURFACE);
        cb.get(0, 1).incParticles();
        cb.updateCenterStatus();
        Assert.assertTrue( "unexpected status"+ cell.getStatus(), cell.getStatus() == CellStatus.SURFACE);
        cb.get(0, -1).incParticles();
        cb.updateCenterStatus();
        Assert.assertTrue( "unexpected status"+ cell.getStatus(), cell.getStatus() == CellStatus.FULL);
    }

    
    
    public void testInterpolateVelocities() {
        CellBlock cb = new CellBlock();
        Particle particle = null;
        cb.setPressures(1.0);
        cb.setCellParticles(5);
  
        Cell cell = cb.get(0,0);
        particle = new Particle(1.1, 1.1, cell);
        //verifyParticleVelocity(particle, cb,  new Vector2d(0.0, 0.0));        
        
        cb.get(-1, 1).setVelocityP(1.0, 0.0);  // upper left
        cb.get(0, 1).setVelocityP(.9, 0.0);     // upper middle
        cb.get(-1, 0).setVelocityP(.91, 1.0);  // middle left
        cb.get(0, 0).setVelocityP(0.7, 0.7);   // center
        cb.get(1, 0).setVelocityP(0.0, 0.4);   // right middle
        cb.get(-1, -1).setVelocityP(0.5, 0.6);   // left bottom
        cb.get(0, -1).setVelocityP(0.3, 0.3);    // middle bottom
        cb.get(1, -1).setVelocityP(.1, 0.0);   // right bottom
        
        particle = new Particle(1.1, 1.1, cell);  // lower left
        verifyParticleVelocity(particle, cb,  new Vector2d(0.7254, 0.46));
        
    
        particle = new Particle(1.9, 1.1, cell);  // lower right
        verifyParticleVelocity(particle, cb,  new Vector2d(0.5606, 0.22));
        
       particle = new Particle(1.1, 1.9, cell); // upper left
        verifyParticleVelocity(particle, cb,  new Vector2d(0.9294, 0.78));
        
        particle = new Particle(1.9, 1.9, cell); // upper right
        verifyParticleVelocity(particle, cb,  new Vector2d(0.7966, 0.54));
        
        particle = new Particle(1.5, 1.5, cell); // center
        verifyParticleVelocity(particle, cb,  new Vector2d(0.805, 0.5));        
        
        cb.setVelocities(0.6, 0.7);
        particle = new Particle(1.1, 1.1, cell);
        verifyParticleVelocity(particle, cb,  new Vector2d(0.6, 0.7));        
    }
    
    private void verifyParticleVelocity(Particle particle, CellBlock cb, Vector2d expectedVelocity) {
        
        Cell cell = cb.getAbsolute(1, 1);
        int i = (int) particle.x;
        int j = (int) particle.y;
        if (i>2 || j>2) System.out.println( "i="+i+" j="+j);
        assert (i<3 && j<3): "i="+i+" j="+j;
        int ii = ((particle.x - i) > 0.5) ? (i + 1) : (i - 1);
        int jj = ((particle.y - j) > 0.5) ? (j + 1) : (j - 1);
         if (ii>2 || jj>2) System.out.println( "ii="+ii+" jj="+jj);
        System.out.println( "i="+i+" j="+j +  "    ii="+ii+" jj="+jj);
        Vector2d  vel = 
                cell.interpolateVelocity( particle,
                        cb.getAbsolute(ii, j), cb.getAbsolute(i, jj),
                        cb.getAbsolute(i - 1, j), cb.getAbsolute(i - 1, jj), // u
                        cb.getAbsolute(i, j - 1), cb.getAbsolute(ii, j - 1));  // v
  
        if (!vel.epsilonEquals(expectedVelocity, 0.00000000001))
            System.out.println("vel for "+particle+" was "+ vel);
        //Assert.assertTrue("vel for particle "+particle +" was "+ vel, vel.epsilonEquals(expectedVelocity, 0.00000000001));
    }
    
    public void testTildeVelocities() {

        CellBlock cb = new CellBlock();
        Cell cell = cb.get(0,0);

        checkTildeVelocities(cb, 0.1, 0.1);

        cell.setVelocityP(1.0, 0);
        checkTildeVelocities(cb, 1.1, 0.1);

        cell.setVelocityP(0.0, 1.0);
        cell.setPressure(1.0);
        checkTildeVelocities(cb, 0.1, 1.1);

        cell.setVelocityP(1.0, 0);
        cb.setCellParticles(5);
        cb.setPressures(1.0);
        cell.setPressure(10.0);
        cb.updateCenterStatus();
        checkTildeVelocities(cb, 2.1996, 1.2000000000000002);

        cell.setVelocityP(1.0, 0);
        cb.setCellParticles(5);
        cb.get(0,1).setPressure(0.8);
        cb.get(1,0).setVelocityP(0.6,0.3);
        cb.updateCenterStatus();
        checkTildeVelocities(cb, 2.15316, 1.17253);
    }

    public void checkTildeVelocities(CellBlock cb, double expectedU, double expectedV) {
        Cell cell = cb.get(0,0);

        double fx = 1;
        double fy = 1;

        cell.updateTildeVelocities( cb.get(1,0),  cb.get(-1,0),
                                    cb.get(0,1),  cb.get(0,-1),
                                    cb.get(1,-1), cb.get(-1,1),
                                    DT, fx, fy, VISCOSITY);
        Cell.swap();

        Assert.assertTrue( "Unxepected value " + cell.getUip() + ", " + cell.getVjp(),
                           (cell.getUip() == expectedU) && (cell.getVjp() == expectedV));
    }

    public void testMassConservation() {

        CellBlock cb = new CellBlock();
        Cell cell = cb.get(0, 0);
        double b = 1.7;

        cell.setVelocityP(1.0, 0);
        cb.setCellParticles(5);
        cb.setPressures(1.0);
        cell.setPressure(2.0);
        cb.updateCenterStatus();
        double divergence = cell.updateMassConservation( b, DT,
                                    cb.get(1,0), cb.get(-1,0), cb.get(0,1), cb.get(0,-1));

        Assert.assertTrue("unexpected div="+divergence, divergence == 1.0);

        cell.setVelocityP(1.0, 0);
        cb.setCellParticles(5);
        cb.setPressures(1.0);
        cb.get(1,0).setPressure(2.0);
        cell.setPressure(1.5);
        cb.updateCenterStatus();
        divergence = cell.updateMassConservation( b, DT,
                                    cb.get(1,0), cb.get(-1,0), cb.get(0,1), cb.get(0,-1));

        Assert.assertTrue("unexpected div="+divergence, divergence == 0.1499999999999999);

    }
    
    public void testDissapateOverflow() {
        
    }

}
