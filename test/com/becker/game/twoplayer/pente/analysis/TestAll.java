// Copyright by Barry G. Becker, 2012. Licensed under MIT License: http://www.opensource.org/licenses/MIT
package com.becker.game.twoplayer.pente.analysis;


import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

/**
 * Master test suite to test all aspects of my pente program.
 *
 * @author Barry Becker
 */
public class TestAll extends TestCase {

    public static Test suite() {

        TestSuite suite =  new TestSuite("All Pente Analysis Tests");

        suite.addTestSuite(LineTest.class);
        suite.addTestSuite(LineEvaluatorTest.class);
        suite.addTestSuite(MoveEvaluatorTest.class);
        suite.addTestSuite(PenteLineTest.class);
        
        return suite;
    }
}