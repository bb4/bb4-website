package com.becker.simulation.liquid.compute;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

/**
 * Master test suite.
 *
 * @author Barry Becker
 */
public class TestAll extends TestCase {

    public static Test suite() {

        TestSuite suite =  new TestSuite("All Liquid compute Tests");

        suite.addTestSuite(VelocityInterpolatorTest.class);
        suite.addTestSuite(MassConserverTest.class);
        suite.addTestSuite(PressureUpdaterTest.class);
        suite.addTestSuite(VelocityUpdaterTest.class);
        suite.addTestSuite(SurfaceVelocityUpdaterTest.class);
        suite.addTestSuite(ParticleAdvectorTest.class);

        return suite;
    }
}