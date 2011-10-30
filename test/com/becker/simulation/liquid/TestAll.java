/** Copyright by Barry G. Becker, 2000-2011. Licensed under MIT License: http://www.opensource.org/licenses/MIT  */
package com.becker.simulation.liquid;

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

        suite.addTest(com.becker.simulation.liquid.config.TestAll.suite());
        suite.addTest(com.becker.simulation.liquid.model.TestAll.suite());
        suite.addTest(com.becker.simulation.liquid.compute.TestAll.suite());

        return suite;
    }
}