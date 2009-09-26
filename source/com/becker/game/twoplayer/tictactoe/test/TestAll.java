package com.becker.game.twoplayer.tictactoe.test;


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

        //suite.addTestSuite(TestTicTacToeSearchable.class);
        suite.addTest(TestMoveEvaluation.suite());
        //suite.addTestSuite(TestShape.class);
        //suite.addTestSuite(TestScoring.class);


        return suite;
    }
}