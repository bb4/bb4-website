/** Copyright by Barry G. Becker, 2000-2011. Licensed under MIT License: http://www.opensource.org/licenses/MIT  */
package com.becker.game.twoplayer.go;

import junit.framework.Test;
import junit.framework.TestSuite;

/**
 * Master test suite to test all aspects of my go program.
 *
 * @author Barry Becker
 */
public class TestAll {

    private TestAll() {}

    public static Test suite() {

        TestSuite suite =  new TestSuite("All Go Tests");

        // mostly integration tests.
        suite.addTest(com.becker.game.twoplayer.go.board.TestAll.suite());
        suite.addTestSuite(TestScoring.class);
        suite.addTestSuite(TestLifeAndDeath.class);

        // these 2 can take really long.
        suite.addTest(TestProblemCollections.suite());
        suite.addTestSuite(TestKiseido2002.class);

        return suite;
    }
}