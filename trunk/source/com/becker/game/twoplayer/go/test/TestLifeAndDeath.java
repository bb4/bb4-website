package com.becker.game.twoplayer.go.test;

import com.becker.game.twoplayer.go.GoMove;
import junit.framework.Test;
import junit.framework.TestSuite;

/**
 * @author Barry Becker
 */
public class TestLifeAndDeath extends GoTestCase {


    public void testProblem57() {
         doLifeAndDeathTest("problem_life57", 5, 1);
    }

    /**
     * originally took 250 seconds
     * - reduced calls to GoGroup.getStones()  to improve to 214 seconds.  14.4% improvement
     * - avoided boundary check in getBoardPostion by acessing positions_ array directly 197 seconds or   8%
     * - don't calculate the liberties everytime in GoString.getLiberties(),
     *     but instead update them incrementally. 75 seconds or 62%
     * - Don't play the move to determine if a suicide and then undo it. Instead infer suicide from looking at the nobi nbrs.
     *     75 -> 74
     *
     * overall= 250 -> 74    factor of 3 speedup!
     */
    public void testProblem58() {
         doLifeAndDeathTest("problem_life58", 5, 13);  // 1, 12 is the correct move, but 5, 13 is ok.
    }

    public void testProblem59() {
        doLifeAndDeathTest("problem_life59", 13, 5);  // 12, 1 is the correct move, but 13, 5 is ok for now.
    }
    
    /**
     * 
     * @param filename
     * @param row row of expected next move.
     * @param column  column of expected next move.
     */
    private void doLifeAndDeathTest(String filename, int row, int column) {
        GoMove m = getNextMove("lifeanddeath/"+ filename, true);
        checkExpected(m, row, column);
    }


    /**
     * @return all the junit test caes to run (in this class)
     */
    public static Test suite() {
        return new TestSuite(TestLifeAndDeath.class);
    }
}