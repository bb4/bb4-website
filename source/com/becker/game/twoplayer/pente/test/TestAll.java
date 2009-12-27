package com.becker.game.twoplayer.pente.test;


import com.becker.game.twoplayer.pente.test.analysis.TestLine;
import com.becker.game.twoplayer.pente.test.analysis.TestMoveEvaluator;
import com.becker.game.twoplayer.pente.test.analysis.TestPenteLine;
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

        suite.addTestSuite(TestPatterns.class);
        suite.addTestSuite(TestLine.class);
        suite.addTest(TestMoveEvaluator.suite());
        suite.addTestSuite(TestPenteLine.class);
        //suite.addTestSuite(TestPenteSearchable.class);
        
        return suite;
    }
}