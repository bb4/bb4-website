package com.becker.common.test;

import com.becker.game.twoplayer.go.test.*;
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

        TestSuite suite =  new TestSuite("All Common Tests");
   
        suite.addTestSuite(NiceNumbersTest.class);
        suite.addTestSuite(MathUtilTest.class);

        return suite;
    }
}