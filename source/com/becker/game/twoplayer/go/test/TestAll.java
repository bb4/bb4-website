package com.becker.game.twoplayer.go.test;

import junit.framework.TestCase;
import junit.framework.Assert;
import junit.framework.TestSuite;
import junit.framework.Test;
import com.becker.game.twoplayer.go.test.TestLifeAndDeath;
import com.becker.game.twoplayer.go.test.Test2;


public class TestAll extends TestCase {



    public static Test suite() {
        TestSuite suite =  new TestSuite("All Go Tests");
        suite.addTestSuite(TestLifeAndDeath.class);
        suite.addTestSuite(Test2.class);
        return suite;
    }
}