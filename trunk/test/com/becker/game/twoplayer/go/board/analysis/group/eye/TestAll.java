// Copyright by Barry G. Becker, 2011. Licensed under MIT License: http://www.opensource.org/licenses/MIT
package com.becker.game.twoplayer.go.board.analysis.group.eye;

import com.becker.game.twoplayer.go.GoTestCase;
import com.becker.game.twoplayer.go.board.analysis.group.TestAbsoluteHealthAnalyzer;
import com.becker.game.twoplayer.go.board.analysis.group.TestAbsoluteHealthCalculator;
import com.becker.game.twoplayer.go.board.analysis.group.TestLifeAnalyzer;
import com.becker.game.twoplayer.go.board.analysis.group.TestStoneInGroupAnalyzer;
import com.becker.game.twoplayer.go.board.analysis.group.eye.potential.TestEyePotentialAnalyzer;
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

        TestSuite suite =  new TestSuite("Group Eye Tests");

        suite.addTest(com.becker.game.twoplayer.go.board.analysis.group.eye.potential.TestAll.suite());

        suite.addTestSuite(TestEyeSpaceAnalyzer.class);
        suite.addTestSuite(TestEyeHealthEvaluator.class);

        return suite;
    }

}