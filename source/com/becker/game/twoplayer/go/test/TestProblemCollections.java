package com.becker.game.twoplayer.go.test;

import com.becker.game.twoplayer.go.GoMove;
import junit.framework.Assert;
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
public class TestProblemCollections extends GoTestCase {


    public void testProblem57() {
        GoMove m = getNextMove("problem_life57", true);
        Assert.assertTrue("Was expecting 6, 5, but instead got "+m, m.getToRow() == 6 && m.getToCol() ==5);
        // Assert.assertTrue(m.getToRow() == 6 && m.getToCol() == 1);
    }


    public void testProblem58() {
        GoMove m = getNextMove("problem_life58", true);
        // getting 2, 11
        Assert.assertTrue("Was expecting 1, 12, but instead got "+m, m.getToRow() == 1 && m.getToCol() == 12);
        // Assert.assertTrue(m.getToRow() == 1 && m.getToCol() == 12);  // answer from book
    }

    public void testProblem59() {
        GoMove m = getNextMove("problem_life59", true);
        Assert.assertTrue("Was expecting 12, 1, but instead got "+m, m.getToRow() == 12 && m.getToCol() == 1);
        // Assert.assertTrue(m.getToRow() == 12 && m.getToCol() == 1);   // answer from book
    }


    /**
     * @return all the junit test caes to run (in this class)
     */
    public static Test suite() {
        return new TestSuite(TestProblemCollections.class);
    }
}
