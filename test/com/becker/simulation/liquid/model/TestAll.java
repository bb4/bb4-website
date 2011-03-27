package com.becker.simulation.liquid.model;

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

        TestSuite suite =  new TestSuite("All Liquid model Tests");

        suite.addTestSuite(TestCell.class);

        return suite;
    }
}