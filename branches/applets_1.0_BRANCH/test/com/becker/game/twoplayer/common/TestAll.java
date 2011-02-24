package com.becker.game.twoplayer.common;


import junit.framework.Test;
import junit.framework.TestSuite;

/**
 * Master test suite to test all aspects of my tic-tac-toe program.
 *
 * @author Barry Becker
 */
public class TestAll {

    private TestAll() {}

    public static Test suite() {

        TestSuite suite =  new TestSuite("All Two Player Common Game Tests");

        suite.addTestSuite(BestMoveFinderTest.class);
        suite.addTest(com.becker.game.twoplayer.common.search.TestAll.suite());

        return suite;
    }
}