// Copyright by Barry G. Becker, 2012. Licensed under MIT License: http://www.opensource.org/licenses/MIT
package com.becker.game.common;


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

        TestSuite suite =  new TestSuite("All Common Game Tests");

        suite.addTestSuite(GameWeightsTest.class);
        //suite.addTest(com.becker.game.common.search.TestAll.suite());

        return suite;
    }
}