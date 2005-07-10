package com.becker.game.twoplayer.go.test;

import junit.framework.TestCase;
import junit.framework.TestSuite;
import junit.framework.Test;

/**
 * @author Barry Becker
 */

public class TestAll extends TestCase {



    public static Test suite() {
        TestSuite suite =  new TestSuite("All Go Tests");


        suite.addTestSuite(TestShape.class);
        suite.addTestSuite(TestEyes.class);
        suite.addTestSuite(TestScoring.class);      
        suite.addTestSuite(TestUnconditionalLife.class);
        suite.addTest(TestProblemCollections.suite());
        suite.addTestSuite(TestLifeAndDeath.class);
        suite.addTestSuite(TestKiseido2002.class);



        return suite;
    }
}