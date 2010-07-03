package com.becker.game.twoplayer.go.board.analysis;

import com.becker.game.twoplayer.go.GoTestCase;
import com.becker.game.twoplayer.go.board.analysis.eye.TestAllEye;
import com.becker.game.twoplayer.go.board.analysis.group.TestAllGroup;
import junit.framework.Test;
import junit.framework.TestSuite;

/**
 * Test out all of my testable methods in the board analysis classes.
 *
 * @author Barry Becker
 */
public class TestAllAnalysis extends GoTestCase {

    /**
     * @return all the junit test caes to run (in this class)
     */
    public static Test suite() {

        TestSuite suite =  new TestSuite("Analysis Tests");

        suite.addTest(TestAllGroup.suite());
        suite.addTest(TestAllEye.suite());

        suite.addTestSuite(TestShapeAnalyzer.class);
        suite.addTestSuite(TestCandidateMoveAnalyzer.class);
        suite.addTestSuite(TestNeighborAnalyzer.class);
        
        return suite;
    }

}

