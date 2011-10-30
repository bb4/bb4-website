/** Copyright by Barry G. Becker, 2000-2011. Licensed under MIT License: http://www.opensource.org/licenses/MIT  */
package com.becker.game.twoplayer.common.search;


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

        TestSuite suite = new TestSuite("All Search Tests");

        suite.addTest(com.becker.game.twoplayer.common.search.transposition.TestAll.suite());
        suite.addTest(com.becker.game.twoplayer.common.search.strategy.TestAll.suite());

        return suite;
    }
}