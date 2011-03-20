package com.becker.simulation.liquid;

import com.becker.simulation.liquid.config.TestAllConfig;
import com.becker.simulation.liquid.model.TestAllModel;
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

        TestSuite suite =  new TestSuite("All Liquid Tests");

        suite.addTest(TestAllConfig.suite());
        suite.addTest(TestAllModel.suite());

        return suite;
    }
}