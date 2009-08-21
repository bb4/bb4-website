package com.becker.game.twoplayer.go.test.board.analysis;

import com.becker.game.twoplayer.go.test.GoTestCase;
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

        suite.addTestSuite(TestLifeAnalyzer.class);
        suite.addTestSuite(TestShapeAnalyzer.class);
        suite.addTestSuite(TestCandidateMoveAnalyzer.class);
        suite.addTestSuite(TestNeighborAnalyzer.class);
        
        suite.addTestSuite(TestEyeAnalyzer.class);
        //suite.addTestSuite(TestGroupeEyeSpaceAnalyzer.class);
        suite.addTestSuite(TestGroupHealthAnalyzer.class);
        //suite.addTestSuite(TestTerritoryAnalyzer.class);

        return suite;
    }

}

