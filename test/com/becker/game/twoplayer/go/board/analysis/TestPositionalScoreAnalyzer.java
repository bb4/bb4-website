package com.becker.game.twoplayer.go.board.analysis;

import com.becker.common.Location;
import com.becker.game.twoplayer.go.GoTestCase;
import com.becker.game.twoplayer.go.GoWeights;
import com.becker.game.twoplayer.go.board.GoBoard;
import com.becker.game.twoplayer.go.board.PositionalScore;
import junit.framework.Assert;

/**
 *Test that candidate moves can be generated appropriately.
 *
 * @author Barry Becker
 */
public class TestPositionalScoreAnalyzer extends GoTestCase {

    /** we can just reuse one of the other file sets */
    private static final String PREFIX = "board/analysis/scoring/";

    private static final GoWeights GO_WEIGHTS = new GoWeights();

    private PositionalScoreAnalyzer scoreAnalyzer_;

    private static final double FULL_BOOST = 1.0;
    private static final double NO_BOOST = 0.0;

    private static final double TOLERANCE = 0.01;



    public void testPositionalScoreNoEye() {

        initializeBoard("positional_score_no_eye");

        PositionalScore totalScore = new PositionalScore();
                                                                //     deads  es  badShp  posScore health
        verifyPositionalScore(new Location(2,2), createPositionalScore(0.0f, 0.0f, 0.0f, 0.0435f, 0.0645f), totalScore);
        verifyPositionalScore(new Location(2,3), createPositionalScore(0.0f, 0.0f, 0.0f, -0.029f, -0.0645f), totalScore);
        verifyPositionalScore(new Location(3,3), createPositionalScore(0.0f, 0.0f, 0.0f, 0.0f, 0.0f), totalScore);
        verifyPositionalScore(new Location(3,2), createPositionalScore(0.0f, 0.0f, 0.0f, 0.029f, 0.0645f), totalScore);

        verifyScoresEqual(createPositionalScore(0.0f, 0.0f, 0.0f, 0.0435f, 0.0645f), totalScore);
        Assert.assertEquals("Unexpected final position score.  ",
                0.108, totalScore.getPositionScore(), TOLERANCE);
    }


    public void testPositionalScoreStoneInEye() {

        initializeBoard("positional_score_stone_in_eye");

        PositionalScore totalScore = new PositionalScore();
        verifyPositionalScore(new Location(2,2), createPositionalScore(0.0f, 1.0f, 0.0f, 0.0f, 0.0f), totalScore);
        verifyPositionalScore(new Location(2,3), createPositionalScore(2.0f, 0.0f, 0.0f, 0.0f, 0.0f), totalScore);
        verifyPositionalScore(new Location(3,2), createPositionalScore(0.0f, 0.0f, 0.0f, 0.029f, 0.5875f), totalScore);
        verifyPositionalScore(new Location(3,3), createPositionalScore(0.0f, 0.0f, 0.0f, 0.421875f, 0.5875f), totalScore);

        verifyScoresEqual(createPositionalScore(2.0f, 1.0f, 0.0f, .45f, 1.175f), totalScore);
        Assert.assertEquals("Unexpected final position score.  ",
                4.625, totalScore.getPositionScore(), TOLERANCE);
    }
    

    private PositionalScore createPositionalScore(float deadStoneScore, float eyeSpaceScore,
                                                  float badShapeScore, float posScore, float healthScore) {
        PositionalScore score = new PositionalScore();
        score.badShapeScore = badShapeScore;
        score.deadStoneScore = deadStoneScore;
        score.healthScore = healthScore;
        score.eyeSpaceScore = eyeSpaceScore;
        score.posScore = posScore;
        score.calcPositionScore();
        return score;
    }

    /**
     * @param file saved sgf game file to load
     * @return the initialized board. Must have 2 groups.
     */
    protected void initializeBoard(String file) {
        restore(PREFIX + file);

        GoBoard board = (GoBoard)controller_.getBoard();

        scoreAnalyzer_ = new PositionalScoreAnalyzer(board);
    }

    /**
     * Verify candidate move generation.
     */
    private void  verifyPositionalScore(Location loc, PositionalScore expScore, PositionalScore totalScore) {

        PositionalScore actScore =
                scoreAnalyzer_.determineScoreForPosition(loc.getRow(), loc.getCol(), FULL_BOOST,
                                              GO_WEIGHTS.getDefaultWeights());
        verifyScoresEqual(expScore, actScore);
        totalScore.incrementBy(actScore);
    }

    private void verifyScoresEqual(PositionalScore expScore, PositionalScore actScore) {
        Assert.assertEquals("Unexpected eye space score. " + actScore,
                expScore.eyeSpaceScore, actScore.eyeSpaceScore, TOLERANCE);
        Assert.assertEquals("Unexpected posScore. " + actScore,
                expScore.posScore, actScore.posScore, TOLERANCE);
        Assert.assertEquals("Unexpected badShape score. " + actScore,
                expScore.badShapeScore, actScore.badShapeScore, TOLERANCE);
        Assert.assertEquals("Unexpected deadStone score.  " + actScore,
                expScore.deadStoneScore, actScore.deadStoneScore, TOLERANCE);
        Assert.assertEquals("Unexpected health score.  " + actScore,
                expScore.healthScore, actScore.healthScore, TOLERANCE);
    }
}
