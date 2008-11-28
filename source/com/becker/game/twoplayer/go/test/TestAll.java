package com.becker.game.twoplayer.go.test;

import com.becker.game.twoplayer.go.test.board.TestAllBoard;
import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

/**
 * Master test suire to test all aspects of my go program.
 *
 * @author Barry Becker
 */
public class TestAll extends TestCase {


    public static Test suite() {

        TestSuite suite =  new TestSuite("All Go Tests");

        suite.addTest(TestAllBoard.suite());
        suite.addTestSuite(TestShape.class);
        suite.addTestSuite(TestEyes.class);
        suite.addTestSuite(TestScoring.class);     
        //suite.addTest(TestProblemCollections.suite());
        suite.addTestSuite(TestLifeAndDeath.class);
        
        // this one takes really long.
        //suite.addTestSuite(TestKiseido2002.class); 

        return suite;
    }
}