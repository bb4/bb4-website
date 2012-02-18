/** Copyright by Barry G. Becker, 2000-2011. Licensed under MIT License: http://www.opensource.org/licenses/MIT  */
package com.becker.game.twoplayer.pente;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

/**
 * Master test suite to test all aspects of my pente program.
 *
 * @author Barry Becker
 */
public class TestAll extends TestCase {


    public static Test suite() {

        TestSuite suite =  new TestSuite("All Pente Tests");

        suite.addTestSuite(PatternsTest.class);
        suite.addTestSuite(PenteSearchableTest.class);

        suite.addTest(com.becker.game.twoplayer.pente.analysis.TestAll.suite());

        return suite;
    }
}