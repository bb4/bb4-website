package com.becker.game.twoplayer;


import com.becker.game.twoplayer.tictactoe.*;
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

        TestSuite suite =  new TestSuite("All TwoP layer Game Tests");

        suite.addTest(com.becker.game.twoplayer.blockade.TestAll.suite());
        suite.addTest(com.becker.game.twoplayer.common.search.TestAll.suite());
        suite.addTest(com.becker.game.twoplayer.go.TestAll.suite());
        suite.addTest(com.becker.game.twoplayer.pente.TestAll.suite());
        suite.addTest(com.becker.game.twoplayer.tictactoe.TestAll.suite());

        return suite;
    }
}