/** Copyright by Barry G. Becker, 2000-2011. Licensed under MIT License: http://www.opensource.org/licenses/MIT  */
package com.becker.game.twoplayer.go.board.analysis.group;

import com.becker.game.twoplayer.go.GoTestCase;
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

        suite.addTestSuite(TestRun.class);
        suite.addTestSuite(TestEyePotentialAnalyzer.class);
        suite.addTestSuite(TestEyeSpaceAnalyzer.class);
        suite.addTestSuite(TestAbsoluteHealthAnalyzer.class);
        suite.addTestSuite(TestAbsoluteHealthCalculator.class);
        suite.addTestSuite(TestLifeAnalyzer.class);
        suite.addTestSuite(TestStoneInGroupAnalyzer.class);
        suite.addTestSuite(TestEyeHealthEvaluator.class);

        return suite;
    }

}