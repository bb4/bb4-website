package com.becker.game.twoplayer.go.board.analysis.group;

import com.becker.game.twoplayer.go.GoTestCase;
import com.becker.game.twoplayer.go.board.GoBoard;
import com.becker.game.twoplayer.go.board.elements.group.IGoGroup;
import com.becker.puzzle.sudoku.Board;
import junit.framework.Assert;

/**
 * Verify that we come up with reasonable eye potential values (likelihood of making eyes in the group).
 * Needs more cases.
 *
 * @author Barry Becker
 */
public class TestEyeHealthEvaluator extends GoTestCase {

    private static final String PREFIX = "board/analysis/group/potential/";

    private static final double TOLERANCE = 0.001;


    public void testZeroEyes_OneLiberty_OneStone() {

        verifyEyePotential(0, 1, 1, -0.7f);
    }

    public void testZeroEyes_TwoLiberties_OneStone() {

        verifyEyePotential(0, 2, 1, 0.02f);
    }

    public void testOneEyes_TwoLiberties() {

        int numStones = 0; // not used;
        verifyEyePotential(1, 2, numStones, -0.3f);
    }

    public void testOneEyes_ThreeLiberties() {

        int numStones = 0; // not used;
        verifyEyePotential(1, 3, numStones, -0.1f);
    }

    public void testOneEyes_FourLiberties() {

            int numStones = 0; // not used;
            verifyEyePotential(1, 4, numStones, 0.01f);
    }

    public void testOneEyes_FiveLiberties() {

            int numStones = 0; // not used;
            verifyEyePotential(1, 5, numStones, 0.05f);
    }


    private void restoreGame(String file) {
        restore(PREFIX + file);
    }

    private void verifyEyePotential(int numEyes, int numLiberties, int numStones, float expEyeHealth) {
        verifyEyeHealth(1.0f, numEyes, numLiberties, numStones, expEyeHealth);
        verifyEyeHealth(-1.0f, numEyes, numLiberties, numStones, -expEyeHealth);
    }


    /**
     * Use EyePotentialAnalyzer to find potential of making eyes.
     */
    private void verifyEyeHealth(float forBlackGroup, int numEyes, int numLiberties, int numStones,
                                 float expEyeHealth) {

        boolean isUnconditionallyAlive = false;
        LifeAnalyzer lifeAnalyzer = new StubLifeAnalyzer(isUnconditionallyAlive);
        EyeHealthEvaluator evaluator = new EyeHealthEvaluator(lifeAnalyzer);

        float actEyeHealth = evaluator.determineHealth(forBlackGroup, numEyes, numLiberties, numStones);

        Assert.assertEquals("Unexpected eyeHealth", expEyeHealth, actEyeHealth, TOLERANCE);
    }

}