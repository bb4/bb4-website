package com.becker.game.twoplayer.go.test;

import junit.framework.TestCase;
import junit.framework.TestSuite;
import junit.framework.Test;


public class TestAll extends TestCase {



    public static Test suite() {
        TestSuite suite =  new TestSuite("All Go Tests");

        suite.addTestSuite(TestEyes.class);
        suite.addTestSuite(TestScoring.class);
        suite.addTestSuite(TestLifeAndDeath.class);
        suite.addTestSuite(TestProblemCollections.class);
        suite.addTestSuite(TestKiseido2002.class);

        return suite;
    }
}