/** Copyright by Barry G. Becker, 2000-2011. Licensed under MIT License: http://www.opensource.org/licenses/MIT  */
package com.becker.game.twoplayer.pente;


import com.becker.game.twoplayer.pente.analysis.LineTest;
import com.becker.game.twoplayer.pente.analysis.MoveEvaluatorTest;
import com.becker.game.twoplayer.pente.analysis.PenteLineTest;
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
        suite.addTestSuite(MoveEvaluatorTest.class);
        suite.addTestSuite(PenteLineTest.class);
        suite.addTestSuite(PenteSearchableTest.class);
        
        return suite;
    }
}