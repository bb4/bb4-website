package com.becker.simulation.liquid;

import com.becker.simulation.liquid.config.TestAllConfig;
import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

/**
 * Master test suire to test all aspects of my go program.
 *
 * @author Barry Becker
 */
public class TestAll extends TestCase {


    public static Test suite() {

        TestSuite suite =  new TestSuite("All Go Tests");

        suite.addTest(TestAllConfig.suite());
        suite.addTestSuite(TestCell.class);

        return suite;
    }
}