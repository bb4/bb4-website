package com.becker.game.twoplayer.go.board;

import com.becker.game.twoplayer.go.board.elements.TestGoString;
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

        TestSuite suite =  new TestSuite("Board Tests");

        suite.addTest(com.becker.game.twoplayer.go.board.analysis.TestAll.suite());
        suite.addTest(com.becker.game.twoplayer.go.board.elements.TestAll.suite());
        suite.addTest(com.becker.game.twoplayer.go.board.update.TestAll.suite());

        suite.addTestSuite(TestGoBoard.class);
        suite.addTestSuite(TestGroupFinding.class);


        return suite;
    }
}
