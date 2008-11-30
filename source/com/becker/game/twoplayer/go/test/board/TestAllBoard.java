package com.becker.game.twoplayer.go.test.board;

import com.becker.game.twoplayer.go.test.GoTestCase;
import com.becker.game.twoplayer.go.test.board.analysis.TestAllAnalysis;
import junit.framework.Test;
import junit.framework.TestSuite;

/**
 * Test out all of my testable methods in my go related classes.
 *
 * @author Barry Becker
 */
public class TestAllBoard extends GoTestCase {


    /**
     * @return all the junit test caes to run (in this class)
     */
    public static Test suite() {

        TestSuite suite =  new TestSuite("Board Tests");

        suite.addTest(TestAllAnalysis.suite());
        
        // suite.addTestSuite(TestGoBoard.class);

        return suite;
    }

}
