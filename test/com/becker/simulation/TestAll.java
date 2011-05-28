package com.becker.simulation;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

/**
 *
 * @author Barry Becker
 */
public class TestAll extends TestCase {

    public static Test suite() {

        TestSuite suite =  new TestSuite("All Simulation Tests");

        suite.addTest(com.becker.simulation.liquid.TestAll.suite());


        return suite;
    }
}