package com.becker.game.twoplayer.go.board.analysis.group;

import com.becker.game.twoplayer.go.GoTestCase;
import com.becker.game.twoplayer.go.board.analysis.eye.TestEyeNeighborMap;
import com.becker.game.twoplayer.go.board.analysis.eye.TestEyeTypeAnalyzer;
import junit.framework.Test;
import junit.framework.TestSuite;

/**
 * @author Barry Becker
 */
public class TestAllGroup extends GoTestCase {

    /**
     * @return all the junit test caes to run (in this class)
     */
    public static Test suite() {

        TestSuite suite =  new TestSuite("Group Tests");

        suite.addTestSuite(TestGroupHealthAnalyzer.class);
        suite.addTestSuite(TestLifeAnalyzer.class);

        return suite;
    }

}