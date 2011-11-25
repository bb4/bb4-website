// Copyright by Barry G. Becker, 2011. Licensed under MIT License: http://www.opensource.org/licenses/MIT
package com.becker.game.twoplayer.go.board.analysis.group.eye.potential;

import com.becker.game.twoplayer.go.GoTestCase;
import com.becker.game.twoplayer.go.board.analysis.group.TestAbsoluteHealthAnalyzer;
import com.becker.game.twoplayer.go.board.analysis.group.TestAbsoluteHealthCalculator;
import com.becker.game.twoplayer.go.board.analysis.group.TestLifeAnalyzer;
import com.becker.game.twoplayer.go.board.analysis.group.TestStoneInGroupAnalyzer;
import com.becker.game.twoplayer.go.board.analysis.group.eye.TestEyeHealthEvaluator;
import com.becker.game.twoplayer.go.board.analysis.group.eye.TestEyeSpaceAnalyzer;
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

        TestSuite suite =  new TestSuite("Group Run Potential Tests");

        suite.addTestSuite(TestRun.class);
        suite.addTestSuite(TestEyePotentialAnalyzer.class);

        return suite;
    }

}