package com.becker.game.twoplayer.blockade.test;

import com.becker.game.twoplayer.go.test.TestEyes;
import com.becker.game.twoplayer.go.test.TestKiseido2002;
import com.becker.game.twoplayer.go.test.TestLifeAndDeath;
import com.becker.game.twoplayer.go.test.TestProblemCollections;
import com.becker.game.twoplayer.go.test.TestScoring;
import com.becker.game.twoplayer.go.test.TestShape;
import com.becker.game.twoplayer.go.test.whitebox.TestAllWhiteBox;
import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

/**
 * Master test suire to test all aspects of my blockade program.
 * Created on May 28, 2007, 7:13 AM
 *@author Barry Becker
 */
public class TestAll extends TestCase {


    public static Test suite() {

        TestSuite suite =  new TestSuite("All Go Tests");

        //suite.addTest(TestAllWhiteBox.suite());
        suite.addTestSuite(TestBlockadeBoard.class);
        //suite.addTestSuite(TestEyes.class);
        //suite.addTest(TestProblemCollections.suite());
        
        return suite;
    }
}