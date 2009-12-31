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

        suite.addTestSuite(BlockadeBoardTest.class);
        suite.addTestSuite(BlockadeControllerTest.class);
        suite.addTestSuite(MoveGeneratorTest.class);
        suite.addTestSuite(BlockadeSearchableTest.class);

        return suite;
    }
}