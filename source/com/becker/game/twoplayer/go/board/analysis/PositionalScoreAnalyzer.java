/** Copyright by Barry G. Becker, 2000-2011. Licensed under MIT License: http://www.opensource.org/licenses/MIT  */
package com.becker.game.twoplayer.go.board.analysis;

import com.becker.game.common.GameContext;
import com.becker.game.twoplayer.go.board.GoBoard;
import com.becker.game.twoplayer.go.board.PositionalScore;
import com.becker.game.twoplayer.go.board.elements.position.GoBoardPosition;
import com.becker.game.twoplayer.go.board.elements.position.GoStone;
import com.becker.game.twoplayer.go.options.GoWeights;
import com.becker.optimization.parameter.ParameterArray;

/**
 * Used to keep track of evaluating a measure of score passed only on values at positions.
 *
 * @author Barry Becker
 */
public final class PositionalScoreAnalyzer {

    /**
     * I used to pass this in as a parameter and have it vary with how far along into the game we are,
     * but I am trying to eliminate scores that might vary for the same position.
     * Identical positions could have different number of moves because of captures and kos.
     */
    private static final double GAME_STAGE_BOOST = 0.5;

    /** a lookup table of scores to attribute to the board positions when calculating the worth */
    private final PositionalScoreArray positionalScores_;


    /**
     * Construct the Go game controller.
     */
    public PositionalScoreAnalyzer(int numRows) {
        positionalScores_ = PositionalScoreArray.getArray(numRows);
    }

    /**
     * @param weights game weights
     * @return accumulated totalScore so far.
     */
    public PositionalScore determineScoreForPosition(GoBoard board, int row, int col, ParameterArray weights) {

        GoBoardPosition position = (GoBoardPosition) board.getPosition(row, col);
        double positionalScore = positionalScores_.getValue(row, col);
        PositionalScore score =
            calcPositionalScore(board, position, weights, positionalScore);

        position.setScoreContribution(score.getPositionScore());
        return score;
    }

    /**
     * @return the score contribution from a single point on the board
     */
    private PositionalScore calcPositionalScore(GoBoard board,
           GoBoardPosition position, ParameterArray weights, double positionalScore) {

        PositionalScore score = new PositionalScore();

        if (position.isInEye())  {
            updateEyePointScore(score, position);
        }
        else if (position.isOccupied()) {
            updateNormalizedOccupiedPositionScore(board, score, position, weights, positionalScore);
        }

        score.calcPositionScore();
        return score;
    }

    /**
     * Score gets updated with value.
     * A dead enemy stone in the eye counts twice.
     */
    private void updateEyePointScore(PositionalScore score, GoBoardPosition position) {

        // was 2, but one works better.
        double scoreForPosition = position.getEye().isOwnedByPlayer1()? 1.0 : -1.0;
        if (position.isOccupied()) {
            score.deadStoneScore = scoreForPosition;
        }
        else {
            score.eyeSpaceScore = scoreForPosition;
        }
    }

    /**
     * Normalize each of the scores by the game weights.
     */
    private void updateNormalizedOccupiedPositionScore(
            GoBoard board, PositionalScore score, GoBoardPosition position,
            ParameterArray weights, double positionalScore) {

        GoStone stone = (GoStone)position.getPiece();

        int side = position.getPiece().isOwnedByPlayer1()? 1: -1;
        double badShapeWt = weights.get(GoWeights.BAD_SHAPE_WEIGHT_INDEX).getValue();
        double positionalWt = weights.get(GoWeights.POSITIONAL_WEIGHT_INDEX).getValue();
        double healthWt = weights.get(GoWeights.HEALTH_WEIGHT_INDEX).getValue();
        double totalWeight = badShapeWt + positionalWt + healthWt;
        StringShapeAnalyzer shapeAnalyzer = new StringShapeAnalyzer(board);

        // penalize bad shape like empty triangles
        score.badShapeScore =
                -(side * shapeAnalyzer.formsBadShape(position) * badShapeWt) / totalWeight;

        // Usually a very low weight is assigned to where stone is played unless we are at the start of the game.
        score.posScore = side * positionalWt * GAME_STAGE_BOOST * positionalScore / totalWeight;
        score.healthScore = healthWt * stone.getHealth() / totalWeight;

        if (GameContext.getDebugMode() > 1)  {
            stone.setPositionalScore(score);
        }
    }
}