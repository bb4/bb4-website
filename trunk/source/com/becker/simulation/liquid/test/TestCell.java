package com.becker.simulation.liquid.test;

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
        cb.setPressure(1.0);
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

    public void testMassConsrvation() {

        CellBlock cb = new CellBlock();
        Cell cell = cb.get(0,0);
        double b = 1.7;

        cell.setVelocityP(1.0, 0);
        cb.setCellParticles(5);
        cb.setPressure(1.0);
        cell.setPressure(2.0);
        cb.updateCenterStatus();
        double divergence = cell.updateMassConservation( b, DT,
                                    cb.get(1,0), cb.get(-1,0), cb.get(0,1), cb.get(0,-1));

        Assert.assertTrue("unexpected div="+divergence, divergence == 1.0);

        cell.setVelocityP(1.0, 0);
        cb.setCellParticles(5);
        cb.setPressure(1.0);
        cb.get(1,0).setPressure(2.0);
        cell.setPressure(1.5);
        cb.updateCenterStatus();
        divergence = cell.updateMassConservation( b, DT,
                                    cb.get(1,0), cb.get(-1,0), cb.get(0,1), cb.get(0,-1));

        Assert.assertTrue("unexpected div="+divergence, divergence == 0.1499999999999999);

    }

}
