package com.becker.game.twoplayer.go.test;

import com.becker.game.twoplayer.common.search.SearchOptions;
import com.becker.game.twoplayer.go.GoMove;
import com.becker.game.twoplayer.common.TwoPlayerOptions;
import com.becker.game.twoplayer.common.search.strategy.SearchStrategyType;
import com.becker.common.Location;
import junit.framework.Test;
import junit.framework.TestSuite;

import java.util.Arrays;

/**
 * @author Barry Becker
 */
public class TestLifeAndDeath extends GoTestCase {

    private static final String PREFIX1 = "lifeanddeath/";

    private static final String PREFIX2 = "problems/sgf/life_death/";

    private static final boolean BLACK_TO_PLAY = true;
    private static final boolean WHITE_TO_PLAY = false;

    /**
     * @param options default options to override
     */
    @Override
    protected void setOptionOverrides(SearchOptions options) {
        options.setAlphaBeta(true);
        options.setLookAhead(3);
        options.setPercentageBestMoves(60);
        options.setQuiescence(true);
        options.setSearchStrategyMethod(SearchStrategyType.MINIMAX);
    }

    /**
     * took 77 seconds with lookahead = 3, bestMoves= 60% and quescence.
     */
    public void testProblem57() {
         doLifeAndDeathTest("problem_life57", 6, 1);  // 6, 1 is the correct move  (common mistakes,  5, 1
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
         doLifeAndDeathTest("problem_life58", 1, 12);   // 1, 12 is the correct move
        //  common mistakes : 4, 6; 5, 13
    }

    public void testProblem59() {
        doLifeAndDeathTest("problem_life59", 12, 1);  // 12, 1 is the correct move
        // common mistakes 13, 5  6, 3;  10, 5 is ok for now.
    }

    // ----------------------------------------

    public void testProblem3() {
        Location[] acceptableMoves = {new Location(5, 18), new Location(11, 18)};
        doLifeAndDeathTest2("life_death.3", acceptableMoves, WHITE_TO_PLAY);  // [E18|K18]
    }
    public void testProblem4() {
        Location[] acceptableMoves = {new Location(11, 18)};
        doLifeAndDeathTest2("life_death.4", acceptableMoves, BLACK_TO_PLAY); // [K18]

    }

    /**
     * @param filename
     * @param row row of expected next move.
     * @param column  column of expected next move.
     */
    private void doLifeAndDeathTest(String filename, int row, int column) {
        GoMove m = getNextMove(PREFIX1 + filename, true);
        verifyExpected(m, row, column);
    }


    /**
     *
     * @param filename
     */
    private void doLifeAndDeathTest2(String filename, Location[] acceptableMoves, boolean blackToPlay) {
        GoMove move = getNextMove(PREFIX2 + filename, blackToPlay);
        verifyAcceptable(move, acceptableMoves);

    }

    private void verifyAcceptable(GoMove move, Location[] acceptableMoves)  {
        boolean pass = false;
        // if the result matches any of the acceptable moves, then pass.
        for (Location loc : acceptableMoves) {
            if (isExpected(move, loc)) {
                pass = true;
            }
        }
        assertTrue("The computed move (" + move +") was not one that we though acceptable ="+
                Arrays.toString(acceptableMoves), pass);
    }

    /**
     * @return all the junit test caes to run (in this class)
     */
    public static Test suite() {
        return new TestSuite(TestLifeAndDeath.class);
    }
}