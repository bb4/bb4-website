/** Copyright by Barry G. Becker, 2000-2011. Licensed under MIT License: http://www.opensource.org/licenses/MIT  */
package com.becker.game.twoplayer.go.board.analysis.group;

import com.becker.game.twoplayer.go.GoTestCase;
import com.becker.game.twoplayer.go.board.analysis.group.eye.TestEyeHealthEvaluator;
import com.becker.game.twoplayer.go.board.analysis.group.eye.potential.TestEyePotentialAnalyzer;
import com.becker.game.twoplayer.go.board.analysis.group.eye.TestEyeSpaceAnalyzer;
import com.becker.game.twoplayer.go.board.analysis.group.eye.potential.TestRun;
import junit.framework.Test;
import junit.framework.TestSuite;

/**
 * @author Barry Becker
 */
public class TestAll extends GoTestCase {

    private TestAll() {}
    
    /**
     * @return all the junit test caes to run (in this class)
     */
    public static Test suite() {

        TestSuite suite =  new TestSuite("Group Tests");

        suite.addTest(com.becker.game.twoplayer.go.board.analysis.group.eye.TestAll.suite());

        suite.addTestSuite(TestAbsoluteHealthAnalyzer.class);
        suite.addTestSuite(TestAbsoluteHealthCalculator.class);
        suite.addTestSuite(TestLifeAnalyzer.class);
        suite.addTestSuite(TestStoneInGroupAnalyzer.class);

        return suite;
    }

}