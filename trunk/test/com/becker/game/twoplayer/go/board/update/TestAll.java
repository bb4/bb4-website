package com.becker.game.twoplayer.go.board.update;

import junit.framework.Test;
import junit.framework.TestSuite;

/**
 * Test out all of my testable methods in my go related classes.
 *
 * @author Barry Becker
 */
public class TestAll {

    private TestAll() {}

    /**
     * @return all the junit test caes to run (in this class)
     */
    public static Test suite() {

        TestSuite suite =  new TestSuite("Board update Tests");

        suite.addTestSuite(TestCaptures.class);
        suite.addTestSuite(TestDeadStones.class);
        //suite.addTestSuite(TestCaptures.class);

        return suite;
    }
}
