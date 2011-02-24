package com.jhlabs.vecmath;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

/**
 *
 * @author Barry Becker
 */
public class TestAll extends TestCase {


    public static Test suite() {

        TestSuite suite =  new TestSuite("All Common Tests");

        suite.addTestSuite(Color4fTest.class);



        return suite;
    }
}