package com.becker.simulation.liquid.config;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

/**
 * Test out all of my testable methods in my go related classes.
 *
 * @author Barry Becker
 */
public class TestAllConfig extends TestCase {


    /**
     * @return all the junit test caes to run (in this class)
     */
    public static Test suite() {

        TestSuite suite =  new TestSuite("Config Tests");

        suite.addTestSuite(TestSource.class);
        //suite.addTest(TestSource.suite);

        return suite;
    }

}
