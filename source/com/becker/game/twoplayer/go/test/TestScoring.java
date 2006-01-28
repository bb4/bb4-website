package com.becker.game.twoplayer.go.test;

import com.becker.game.common.GameContext;
import junit.framework.Assert;
import junit.framework.Test;
import junit.framework.TestSuite;

/**
 * @author Barry Becker
 */
public class TestScoring extends GoTestCase {

    // give some leeway on the territory estimate since its a heuristic.
    private static final double TOLERANCE = 5;

    public void testScoring1() {
        checkScoring("problem_score1", 0, 0, 0, 0, 74, 57);
    }



    public void testScoring55a() {
        checkScoring("problem_score55a", 0, 0, 0, 7, 17, 0);
    }

    public void testScoring55b() {
        checkScoring("problem_score55b", 0, 2, 0, 6, 14, 0);
    }

    public void testScoring2() {
        checkScoring("problem_score2", 0, 0, 2, 0, 58, 57);
    }


    /**
     *
     * @param scoringProblem
     * @param expectedBlackCapturesSoFar number of black stones that were captured by white.
     * @param expectedWhiteCapturesSoFar number of white stones that were captured by black.
     * @param expectedDeadBlackOnBoard number of black stones that that are dead on the board at the end.
     * @param expectedDeadWhiteOnBoard number of white stones tthat that are dead on the board at the end.
     * @param expectedBlackTerr amount of black territory (excluding dead white stones in the territory)
     * @param expectedWhiteTerr amount of white territory (excluding dead black stones in the territory)
     */
    private void checkScoring(String scoringProblem,
                        int expectedBlackCapturesSoFar, int expectedWhiteCapturesSoFar,
                        int expectedDeadBlackOnBoard, int expectedDeadWhiteOnBoard,
                        int expectedBlackTerr, int expectedWhiteTerr) {

        updateLifeAndDeath(scoringProblem);

        int blackTerrEst = controller_.getTerritory(true);
        int whiteTerrEst = controller_.getTerritory(false);
        int numBlackCaptures = controller_.getNumCaptures(true);
        int numWhiteCaptures = controller_.getNumCaptures(false);
        int numDeadBlack = controller_.getNumDeadStonesOnBoard(true);
        int numDeadWhite = controller_.getNumDeadStonesOnBoard(false);

        GameContext.log(0, "Captures :          black = " + numBlackCaptures + "   white = "+ numWhiteCaptures);
        GameContext.log(0, "Territory: black = " + blackTerrEst + "   white = "+ whiteTerrEst);
        Assert.assertTrue(
                "Unexpected number of black captures. Expected "+expectedBlackCapturesSoFar+" got "+numBlackCaptures,
                numBlackCaptures == expectedBlackCapturesSoFar);
        Assert.assertTrue(
                "Unexpected number of white captures. Expected "+expectedWhiteCapturesSoFar+" got "+numWhiteCaptures,
                numWhiteCaptures == expectedWhiteCapturesSoFar);
        Assert.assertTrue(
                "Unexpected number of dead black stones on board. Expected "+expectedDeadBlackOnBoard+" got "+numDeadBlack,
                numDeadBlack == expectedDeadBlackOnBoard);
        Assert.assertTrue(
                "Unexpected number of dead white stones on board. Expected "+expectedDeadWhiteOnBoard+" got "+numDeadWhite,
                numDeadWhite == expectedDeadWhiteOnBoard);
        Assert.assertTrue("The black territory estimate ("+ blackTerrEst +") was not close to "+ expectedBlackTerr,
                          withinBounds(blackTerrEst, expectedBlackTerr));
        Assert.assertTrue("The white territory estimate ("+ whiteTerrEst +") was not close to "+ expectedWhiteTerr,
                          withinBounds(whiteTerrEst, expectedWhiteTerr));
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