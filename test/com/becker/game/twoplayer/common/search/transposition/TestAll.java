package com.becker.game.twoplayer.common.search.transposition;


import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

/**
 * Master test suite to test all aspects of my tic-tac-toe program.
 *
 * @author Barry Becker
 */
public class TestAll extends TestCase {


    public static Test suite() {

        TestSuite suite =  new TestSuite("All Transposition Tests");

        suite.addTestSuite(ZobristHashTest.class);
        suite.addTestSuite(HashUniquenessTest.class);
        suite.addTestSuite(HashGo9x9UniquenessTest.class);
        suite.addTestSuite(HashGo5x5UniquenessTest.class);

        return suite;
    }
}