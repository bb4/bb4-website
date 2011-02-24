package com.becker.game.twoplayer.go.board.analysis;

import junit.framework.Test;
import junit.framework.TestSuite;

/**
 * Test out all of my testable methods in the board analysis classes.
 *
 * @author Barry Becker
 */
public class TestAll {

    private TestAll() {}

    /**
     * @return all the junit test caes to run (in this class)
     */
    public static Test suite() {

        TestSuite suite =  new TestSuite("Analysis Tests");
       
        //suite.addTest(suite());
        suite.addTest(com.becker.game.twoplayer.go.board.analysis.eye.TestAll.suite());
        suite.addTest(com.becker.game.twoplayer.go.board.analysis.neighbor.TestAll.suite());

        suite.addTestSuite(TestStringShapeAnalyzer.class);
        suite.addTestSuite(TestShapeAnalyzer.class);
        suite.addTestSuite(TestCandidateMoveAnalyzer.class);
        suite.addTestSuite(TestPositionalScoreAnalyzer.class);
        suite.addTestSuite(TestGoBoardUtil.class);
        
        return suite;
    }

}

