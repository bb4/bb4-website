package com.becker.game.twoplayer.tictactoe;


import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

/**
 * Master test suire to test all aspects of my tic-tac-toe program.
 *
 * @author Barry Becker
 */
public class TestAll extends TestCase {


    public static Test suite() {

        TestSuite suite =  new TestSuite("All TicTacToe Tests");

        suite.addTest(MoveEvaluationTest.suite());
        suite.addTestSuite(TicTacToeSearchableTest.class);
        suite.addTestSuite(MiniMaxStrategyTest.class);
        suite.addTestSuite(NegaMaxStrategyTest.class);
        suite.addTestSuite(NegaScoutStrategyTest.class);

        return suite;
    }
}