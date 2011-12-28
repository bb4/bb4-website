/** Copyright by Barry G. Becker, 2000-2011. Licensed under MIT License: http://www.opensource.org/licenses/MIT  */
package com.becker.game.twoplayer.go.board.analysis;

import com.becker.common.geometry.Location;
import com.becker.game.twoplayer.go.GoTestCase;
import com.becker.game.twoplayer.go.board.GoBoard;
import com.becker.game.twoplayer.go.board.PositionalScore;
import com.becker.game.twoplayer.go.options.GoWeights;
import com.becker.optimization.parameter.ParameterArray;
import junit.framework.Assert;

/**
 * Test positional score analysis.
 *
 * @author Barry Becker
 */
public class TestPositionalScoreAnalyzer extends GoTestCase {

    /** we can just reuse one of the other file sets */
    private static final String PREFIX = "board/analysis/scoring/";

    private GoWeights goWeights;

    private PositionalScoreAnalyzer scoreAnalyzer_;

    private static final double TOLERANCE = 0.001;

    @Override
    protected void setUp() throws Exception {
        super.setUp();
        goWeights = new GoWeights();
        ParameterArray params = goWeights.getPlayer1Weights();
        for (int i=0; i<params.size(); i++) {
            params.get(i).setValue(2.0);
        }
        
        goWeights.setPlayer1Weights(params);
    }
    
    /**
     *  | XO
     *  | X
     *  |
     *  |     O
     */
    public void testOccupiedPositionalScoreNoEye() {

        initializeBoard("positional_score_no_eye");

        PositionalScore totalScore = PositionalScore.createZeroScore();
                                                                            //    badShp  posScore health
        verifyPositionalScore(new Location(2,2), PositionalScore.createOccupiedScore(0.0, 0.025, 0.0), totalScore);
        verifyPositionalScore(new Location(2,3), PositionalScore.createOccupiedScore(0.0, -0.016667, 0.0), totalScore);
        verifyPositionalScore(new Location(3,3), PositionalScore.createOccupiedScore(0.0, 0.0, 0.0), totalScore);
        verifyPositionalScore(new Location(3,2), PositionalScore.createOccupiedScore(0.0, 0.01667, 0.0), totalScore);

        verifyScoresEqual(PositionalScore.createOccupiedScore(0.0, 0.025, 0.0), totalScore);
        Assert.assertEquals("Unexpected final position score.  ",
                0.025, totalScore.getPositionScore(), TOLERANCE);
    }
    
    public void testOverallPositionalScoreNoEye() {

        initializeBoard("positional_score_no_eye");
        verifyExpectedOverallScore(0.025f);
    }

    /**
     *  |   X
     *  |  OX
     *  |XXXX
     *  |
     *  |                  O
     */
    public void testOccupiedPositionalScoreStoneInEye() {

        initializeBoard("positional_score_stone_in_eye");

        PositionalScore totalScore = PositionalScore.createZeroScore();
                                                                         //    badShp  posScore health
        verifyPositionalScore(new Location(2,2), PositionalScore.createOccupiedScore(0.0, 0.0, 0.0), totalScore);
        verifyPositionalScore(new Location(2,3), PositionalScore.createOccupiedScore(0.0, -0.01667, 0.0), totalScore);
        verifyPositionalScore(new Location(3,2), PositionalScore.createOccupiedScore(0.0, 0.01667, 0.0), totalScore);
        verifyPositionalScore(new Location(3,3), PositionalScore.createOccupiedScore(0.0, 0.25, 0.0), totalScore);

        verifyScoresEqual(PositionalScore.createOccupiedScore(0.0, 0.25, 0.0), totalScore);
        Assert.assertEquals("Unexpected final position score.  ",
                0.25, totalScore.getPositionScore(), TOLERANCE);
    }

    public void testOverallPositionalScoreInEye() {

        initializeBoard("positional_score_stone_in_eye");
        verifyExpectedOverallScore(-0.0666667f);
    }

    /**
     *  | X    OO |
     *  |XXX   O O|
     *  |  X   OO |
     *  |XX
     */
    public void testOccupiedPositionalAlive() {

        initializeBoard("positional_score_alive");

        PositionalScore totalScore = PositionalScore.createZeroScore();
                                                                           //       badShp  posScore health
        verifyPositionalScore(new Location(2,2), PositionalScore.createOccupiedScore(-1.0f, 0.025f, 0.0f), totalScore);
        verifyPositionalScore(new Location(2,3), PositionalScore.createOccupiedScore(-0.666667f, .01666667f, 0.0f), totalScore);
        verifyPositionalScore(new Location(3,3), PositionalScore.createOccupiedScore(-0.333333f, 0.25f, 0.0f), totalScore);
        verifyPositionalScore(new Location(3,2), PositionalScore.createOccupiedScore(0.0f, 0.0f, 0.0f), totalScore);

        verifyPositionalScore(new Location(8,2), PositionalScore.createOccupiedScore(0.0f, 0.0f, 0.0f), totalScore);
        verifyPositionalScore(new Location(8,3), PositionalScore.createOccupiedScore(0.0f, 0.0f, 0.0f), totalScore);
        verifyPositionalScore(new Location(9,3), PositionalScore.createOccupiedScore(0.0f, 0.0f, 0.0f), totalScore);
        verifyPositionalScore(new Location(9,2), PositionalScore.createOccupiedScore(0.0f, 0.0f, 0.0f), totalScore);

        verifyScoresEqual(PositionalScore.createOccupiedScore(-2.0f, 0.2917f, 0.0f), totalScore);
        Assert.assertEquals("Unexpected final position score.  ",
                -1.7083f, totalScore.getPositionScore(), TOLERANCE);
    }

    public void testOverallPositionalScoreAlive() {

        initializeBoard("positional_score_alive");
        verifyExpectedOverallScore(-0.975f);
    }

    /**
     * @param file saved sgf game file to load
     * @return the initialized board. Must have 2 groups.
     */
    protected void initializeBoard(String file) {
        restore(PREFIX + file);

        GoBoard board = getBoard();
        scoreAnalyzer_ = new PositionalScoreAnalyzer(board);
    }

    /**
     * Verify candidate move generation.
     */
    private void verifyPositionalScore(Location loc, PositionalScore expScore, PositionalScore totalScore) {

        PositionalScore actScore =
                scoreAnalyzer_.determineScoreForPosition(loc.getRow(), loc.getCol(),
                        goWeights.getPlayer1Weights());
        verifyScoresEqual(expScore, actScore);
        totalScore.incrementBy(actScore);
    }

    private void verifyScoresEqual(PositionalScore expScore, PositionalScore actScore) {
        Assert.assertEquals("Unexpected eye space score. " + actScore,
                expScore.getEyeSpaceScore(), actScore.getEyeSpaceScore(), TOLERANCE);
        Assert.assertEquals("Unexpected posScore. " + actScore,
                expScore.getPosScore(), actScore.getPosScore(), TOLERANCE);
        Assert.assertEquals("Unexpected badShape score. " + actScore,
                expScore.getBadShapeScore(), actScore.getBadShapeScore(), TOLERANCE);
        Assert.assertEquals("Unexpected deadStone score.  " + actScore,
                expScore.getDeadStoneScore(), actScore.getDeadStoneScore(), TOLERANCE);
        Assert.assertEquals("Unexpected health score.  " + actScore,
                expScore.getHealthScore(), actScore.getHealthScore(), TOLERANCE);
    }


    private void verifyExpectedOverallScore(float expectedScore)  {

        int size = getBoard().getNumRows();

        PositionalScore score = PositionalScore.createZeroScore();

        for (int row=1; row<=size; row++) {
            for (int col=1; col<=size; col++) {
               score.incrementBy(scoreAnalyzer_.determineScoreForPosition(row, col, goWeights.getPlayer1Weights()));
            }
        }
        assertEquals(expectedScore, score.getPositionScore(), TOLERANCE);
    }
}
