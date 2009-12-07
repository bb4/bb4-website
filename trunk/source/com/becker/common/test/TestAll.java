package com.becker.common.test;

import com.becker.common.test.CommandLineOptionsTest;
import com.becker.common.test.util.TestAllUtil;
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
        suite.addTestSuite(LRUCacheTest.class);
        suite.addTestSuite(CommandLineOptionsTest.class);

        suite.addTest(TestAllUtil.suite());

        return suite;
    }
}