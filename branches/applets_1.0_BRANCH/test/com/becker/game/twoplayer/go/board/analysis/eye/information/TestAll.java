package com.becker.game.twoplayer.go.board.analysis.eye.information;

import com.becker.game.twoplayer.go.GoTestCase;
import com.becker.game.twoplayer.go.board.analysis.eye.TestEyeNeighborMap;
import com.becker.game.twoplayer.go.board.analysis.eye.TestEyeTypeAnalyzer;
import junit.framework.Test;
import junit.framework.TestSuite;

/**
 * @author Barry Becker
 */
public class


        TestAll extends GoTestCase {

    /**
     * @return all the junit test caes to run (in this class)
     */
    public static Test suite() {

        TestSuite suite =  new TestSuite("Eye Information Tests");

        suite.addTestSuite(TestE1Information.class);
        suite.addTestSuite(TestE2Information.class);
        suite.addTestSuite(TestE3Information.class);
        suite.addTestSuite(TestE4Information.class);
        suite.addTestSuite(TestE5Information.class);
        suite.addTestSuite(TestE6Information.class);
        suite.addTestSuite(TestE7Information.class);

        return suite;
    }

}