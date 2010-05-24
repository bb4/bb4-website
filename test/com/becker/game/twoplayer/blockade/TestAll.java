package com.becker.game.twoplayer.blockade;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

/**
 * Master test suire to test all aspects of my com.becker.game.twoplayer.blockade program.
 * @author Barry Becker
 */
public class TestAll extends TestCase {

    public static Test suite() {

        TestSuite suite =  new TestSuite("All Blockade Tests");

        suite.addTestSuite(BlockadeBoardTest.class);
        suite.addTestSuite(BlockadeControllerTest.class);
        suite.addTestSuite(MoveGeneratorTest.class);
        suite.addTestSuite(BlockadeSearchableTest.class);

        return suite;
    }
}