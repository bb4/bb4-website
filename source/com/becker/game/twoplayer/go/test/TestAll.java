package com.becker.game.twoplayer.go.test;

import com.becker.game.twoplayer.go.test.whitebox.TestAllWhiteBox;
import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

/**
 * @author Barry Becker
 */

public class TestAll extends TestCase {



    public static Test suite() {
        TestSuite suite =  new TestSuite("All Go Tests");


        suite.addTest(TestAllWhiteBox.suite());
        suite.addTestSuite(TestShape.class);
        suite.addTestSuite(TestEyes.class);
        suite.addTestSuite(TestScoring.class);
        suite.addTest(TestProblemCollections.suite());
        suite.addTestSuite(TestLifeAndDeath.class);
        suite.addTestSuite(TestKiseido2002.class);


        return suite;
    }
}