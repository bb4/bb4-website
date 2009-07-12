package com.becker.game.twoplayer.blockade.test;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

/**
 * Master test suire to test all aspects of my blockade program.
 * Created on May 28, 2007, 7:13 AM
 *@author Barry Becker
 */
public class TestAll extends TestCase {


    public static Test suite() {

        TestSuite suite =  new TestSuite("All Go Tests");

        //suite.addTest(TestAllWhiteBox.suite());
        suite.addTestSuite(TestBlockadeBoard.class);
        suite.addTestSuite(TestBlockadeController.class);
        suite.addTestSuite(TestMoveGenerator.class);
        suite.addTestSuite(TestBlockadeSearchable.class);

        return suite;
    }
}