package com.becker.game.twoplayer.go.board.analysis.neighbor;

import com.becker.game.twoplayer.go.GoTestCase;
import com.becker.game.twoplayer.go.board.analysis.group.TestGroupHealthAnalyzer;
import com.becker.game.twoplayer.go.board.analysis.group.TestLifeAnalyzer;
import junit.framework.Test;
import junit.framework.TestSuite;

/**
 * @author Barry Becker
 */
public class TestAllNeighbor extends GoTestCase {

    /**
     * @return all the junit test caes to run (in this class)
     */
    public static Test suite() {

        TestSuite suite =  new TestSuite("Neighbor Tests");

        suite.addTestSuite(TestNeighborAnalyzer.class);
        suite.addTestSuite(TestNobiNeighborAnalyzer.class);
        suite.addTestSuite(TestStringNeighborAnalyzer.class);
        suite.addTestSuite(TestGroupNeighborAnalyzer.class);

        return suite;
    }

}