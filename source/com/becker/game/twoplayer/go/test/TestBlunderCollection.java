package com.becker.game.twoplayer.go.test;

import com.becker.game.twoplayer.go.GoMove;
import junit.framework.Test;
import junit.framework.TestSuite;

/**
 * Test a collection of problems from
 * Martin Mueller (mmueller)
 * Markus Enzenberger (emarkus)
 *email domain: cs.ualberta.ca
 *
 * @author Barry Becker
 */
public class TestBlunderCollection extends GoTestCase {



    public void test1() {
        GoMove m = getNextMove("problems/sgf/blunder/blunder.1", true);
        checkExpected(m, 17, 12);   // Q12
    }

    public void test2() {
        GoMove m = getNextMove("problems/sgf/blunder/blunder.2", true);
        checkExpected(m, 17, 12);  // Q12
    }

    public void test13() {
        GoMove m = getNextMove("problems/sgf/blunder/blunder.13", false);
        checkExpected(m, 13, 5);
    }


    public void test14() {
        GoMove m = getNextMove("problems/sgf/blunder/blunder.14", false);
        checkExpected(m, 2, 12); // B12
    }



    /**
     * @return all the junit test caes to run (in this class)
     */
    public static Test suite() {
        return new TestSuite(TestBlunderCollection.class);
    }


}
