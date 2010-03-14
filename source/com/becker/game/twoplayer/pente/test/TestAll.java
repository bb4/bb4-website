package com.becker.game.twoplayer.pente.test;


import com.becker.game.twoplayer.pente.test.analysis.LineTest;
import com.becker.game.twoplayer.pente.test.analysis.MoveEvaluatorTest;
import com.becker.game.twoplayer.pente.test.analysis.PenteLineTest;
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

        suite.addTestSuite(PatternsTest.class);
        suite.addTestSuite(LineTest.class);
        suite.addTest(MoveEvaluatorTest.suite());
        suite.addTestSuite(PenteLineTest.class);
        suite.addTestSuite(PenteSearchableTest.class);
        
        return suite;
    }
}