package com.becker.simulation.liquid.model;

import com.becker.simulation.liquid.config.TestAllConfig;
import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

/**
 * Master test suite.
 *
 * @author Barry Becker
 */
public class TestAllModel extends TestCase {

    public static Test suite() {

        TestSuite suite =  new TestSuite("All Liquid model Tests");

        suite.addTest(TestAllConfig.suite());
        suite.addTestSuite(TestCell.class);

        return suite;
    }
}