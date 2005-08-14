package com.becker.game.twoplayer.go.test;

import junit.framework.Assert;
import junit.framework.TestSuite;
import junit.framework.Test;
import com.becker.game.common.GameContext;

/**
 * @author Barry Becker
 */
public class TestScoring extends GoTestCase {

    // give some leeway on the territory estimate since its a heuristic.
    private static final double TOLERANCE = 5;

    public void testScoring1() {
        checkScoring("problem_score1", 0, 0, 74, 57);
    }


    public void testScoring2() {
        checkScoring("problem_score2", 2, 0, 58, 57);
    }


    /**
     *
     * @param scoringProblem
     * @param expectedBlackCaptures number of black stones that were captured by white.
     * @param expectedWhiteCaptures number of white stones that were captured by black.
     * @param expectedBlackTerr amount of black territory (excluding dead white stones in the territory)
     * @param expectedWhiteTerr amount of white territory (excluding dead black stones in the territory)
     */
    private void checkScoring(String scoringProblem,
                        int expectedBlackCaptures, int expectedWhiteCaptures,
                        int expectedBlackTerr, int expectedWhiteTerr) {

        updateLifeAndDeath(scoringProblem);

        int blackTerrEst = controller_.getTerritory(true);
        int whiteTerrEst = controller_.getTerritory(false);
        int numBlackCaptures = controller_.getNumCaptures(true);
        int numWhiteCaptures = controller_.getNumCaptures(false);

        GameContext.log(0, "Captures :          black = " + numBlackCaptures + "   white = "+ numWhiteCaptures);
        GameContext.log(0, "Territory: black = " + blackTerrEst + "   white = "+ whiteTerrEst);
        Assert.assertTrue(
                "Unexpected number of black captures. Expected "+expectedBlackCaptures+" got "+numBlackCaptures,
                numBlackCaptures == expectedBlackCaptures);
        Assert.assertTrue(
                "Unexpected number of white captures. Expected "+expectedWhiteCaptures+" got "+numWhiteCaptures,
                numWhiteCaptures == expectedWhiteCaptures);
        Assert.assertTrue( withinBounds(blackTerrEst, expectedBlackTerr));
        Assert.assertTrue( withinBounds(-whiteTerrEst, expectedWhiteTerr));
        // see if a given move is in jeopardy
        //controller_.inJeopardy();
    }

    private static boolean withinBounds(int actual, int expected) {
        return (actual > (expected - TOLERANCE) && actual < (expected + TOLERANCE));
    }

    public static Test suite() {
        return new TestSuite(TestScoring.class);
    }
}