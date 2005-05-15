package com.becker.game.twoplayer.go.test;

import junit.framework.Assert;
import junit.framework.TestSuite;
import junit.framework.Test;
import com.becker.game.twoplayer.go.GoMove;


public class TestLifeAndDeath extends GoTestCase {


    public void testProblem57() {
        GoMove m = getNextMove("problem_life57", true);
        checkExpected(m, 6, 5);    // actually 6, 1
    }


    public void testProblem58() {
        GoMove m = getNextMove("problem_life58", true);
        checkExpected(m, 1, 12);
    }

    public void testProblem59() {
        GoMove m = getNextMove("problem_life59", true);
        checkExpected(m, 12, 1);
    }


    /**
     * @return all the junit test caes to run (in this class)
     */
    public static Test suite() {
        return new TestSuite(TestLifeAndDeath.class);
    }
}