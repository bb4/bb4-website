package com.becker.game.twoplayer.go.test;

import junit.framework.Assert;
import junit.framework.TestSuite;
import junit.framework.Test;
import com.becker.game.twoplayer.go.GoMove;


public class TestLifeAndDeath extends GoTestCase {


    public void testProblem57() {
        GoMove m = getNextMove("problem_life57", true);
        Assert.assertTrue("Was expecting 6,5, but instead got "+m,m.getToRow() == 6 && m.getToCol() ==5);
        // Assert.assertTrue(m.getToRow() == 6 && m.getToCol() == 1);
    }


     public void testProblem58() {
        GoMove m = getNextMove("problem_life58", true);
        Assert.assertTrue(m.getToRow() == 3 && m.getToCol() == 5);
        // Assert.assertTrue(m.getToRow() == 1 && m.getToCol() == 12);  // answer from book
    }

     public void testProblem59() {
        GoMove m = getNextMove("problem_life59", true);
        Assert.assertTrue(m.getToRow() == 11 && m.getToCol() == 8);
        // Assert.assertTrue(m.getToRow() == 12 && m.getToCol() == 1);   // answer from book
    }


    /**
     * @return all the junit test caes to run (in this class)
     */
    public static Test suite() {
        return new TestSuite(TestLifeAndDeath.class);
    }
}