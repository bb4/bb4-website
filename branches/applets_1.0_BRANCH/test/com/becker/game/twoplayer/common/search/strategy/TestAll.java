package com.becker.game.twoplayer.common.search.strategy;

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

        TestSuite suite =  new TestSuite("All Search Strategy Tests");

        suite.addTestSuite(MiniMaxSearchStrategyTest.class);
        suite.addTestSuite(NegaMaxSearchStrategyTest.class);
        suite.addTestSuite(NegaScoutSearchStrategyTest.class);

        suite.addTestSuite(UctNodeTest.class);
        suite.addTestSuite(UctSearchStrategyTest.class);

        // these still need work.
        suite.addTestSuite(NegaMaxMemorySearchStrategyTest.class);
        suite.addTestSuite(NegaScoutMemorySearchStrategyTest.class);
        suite.addTestSuite(MtdNegaMaxSearchStrategyTest.class);
        //suite.addTestSuite(MtdNegaScoutSearchStrategyTest.class);

        return suite;
    }
}