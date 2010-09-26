package com.becker.game.twoplayer.go;

import com.becker.game.twoplayer.go.board.TestAllBoard;
import com.becker.game.twoplayer.go.board.analysis.TestStringShapeAnalyzer;
import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

/**
 * Master test suite to test all aspects of my go program.
 *
 * @author Barry Becker
 */
public class TestAll extends TestCase {


    public static Test suite() {

        TestSuite suite =  new TestSuite("All Go Tests");

        suite.addTest(TestAllBoard.suite());
        suite.addTestSuite(TestScoring.class);
        ////suite.addTestSuite(TestLifeAndDeath.class);

        // these 2 can take really long.
        ////suite.addTest(TestProblemCollections.suite());
        ////suite.addTestSuite(TestKiseido2002.class);

        return suite;
    }
}