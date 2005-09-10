package com.becker.game.twoplayer.go.test;

import com.becker.game.twoplayer.go.GoMove;
import junit.framework.Test;
import junit.framework.TestSuite;

/**
 * @author Barry Becker
 */
public class TestLifeAndDeath extends GoTestCase {


    public void testProblem57() {
        GoMove m = getNextMove("problem_life57", true);
        checkExpected(m, 6, 5);    // actually 6, 1
    }


    /**
     * originally took 250 seconds
     * - reduced calls to GoGroup.getStones()  to improve to 214 seconds.  14.4% improvement
     * - avoided boundary check in getBoardPostion by acessing positions_ array directly 197 seconds or   8%
     * - don't calculate the liberties everytime in GoString.getLiberties(),
     *     but instead update them incrementally. 75 seconds or 62%
     * - Don't play the move to determine if a suicide and then undo it. Instead infer suicide from looking at the nobi nbrs.
     *     75 -> 35
     *
     * overall= 250 -> 35    factor of 7 speedup!
     */
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