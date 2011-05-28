package com.becker;

import com.becker.common.CommandLineOptionsTest;
import com.becker.common.function.TestAllFunction;
import com.becker.common.interpolation.TestAllInterpolation;
import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

/**
 *
 * @author Barry Becker
 */
public class TestAll extends TestCase {

    public static Test suite() {

        TestSuite suite =  new TestSuite("All Tests");

        suite.addTest(com.becker.game.TestAll.suite());
        suite.addTest(com.becker.optimization.TestAll.suite());
        suite.addTest(com.becker.puzzle.TestAll.suite());
        suite.addTest(com.becker.simulation.TestAll.suite());

        return suite;
    }
}