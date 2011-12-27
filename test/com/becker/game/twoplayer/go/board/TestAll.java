/** Copyright by Barry G. Becker, 2000-2011. Licensed under MIT License: http://www.opensource.org/licenses/MIT  */
package com.becker.game.twoplayer.go.board;

import com.becker.game.twoplayer.go.board.analysis.TestWorthCalculator3;
import com.becker.game.twoplayer.go.board.analysis.TestWorthCalculator5;
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
     * @return all the junit test cases to run (in this class)
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
