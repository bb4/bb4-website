package com.becker.game.twoplayer.pente.test;


import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

/**
 * Master test suire to test all aspects of my pente program.
 *
 * @author Barry Becker
 */
public class TestAll extends TestCase {


    public static Test suite() {

        TestSuite suite =  new TestSuite("All Pente Tests");

        suite.addTestSuite(TestPenteSearchable.class);
        //suite.addTest(TestPenteSearchable.suite());
        //suite.addTestSuite(TestShape.class);
        //suite.addTestSuite(TestScoring.class);


        return suite;
    }
}