package com.becker.game.twoplayer.go.board.analysis;

import com.becker.game.common.GameContext;
import com.becker.game.twoplayer.go.GoWeights;
import com.becker.game.twoplayer.go.board.GoBoard;
import com.becker.game.twoplayer.go.board.elements.GoBoardPosition;
import com.becker.game.twoplayer.go.board.elements.GoStone;
import com.becker.game.twoplayer.go.board.PositionalScore;
import com.becker.optimization.parameter.ParameterArray;

/**
 * Used to keep track of evaluating a measure of score pased only on values at positions.
 *
 * @author Barry Becker
 */
public final class PositionalScoreAnalyzer
{
    private GoBoard board_;

    /** a lookup table of scores to attribute to the board positions when calculating the worth */
    private float[][] positionalScore_ = null;


    /** we assign a value to a stone based on the line on which it falls when calculating worth. */
    private static final float[] LINE_VALS = {
        -0.5f,  // first line
        0.1f,   // second line
        1.0f,   // third line
        0.9f,   // fourth line
        0.1f    // fifth line
    };


    /**
     * Construct the Go game controller.
     */
    public PositionalScoreAnalyzer(GoBoard board)
    {
        board_ = board;
        initializePositionalScoreArray();
    }

    /**
     * @param gameStageBoost weight differently based on how far into the game we are.
     * @param totalScore accumulated totalScore so far.
     * @param weights game weights
     * @return  accumulated totalScore so far.
     */
    public PositionalScore updateScoreForPosition(int row, int col, double gameStageBoost,
                                       PositionalScore totalScore, ParameterArray weights) {

        GoBoardPosition position = (GoBoardPosition) board_.getPosition(row, col);
        double positionalScore = gameStageBoost * positionalScore_[row][col];
        PositionalScore score = calcPositionalScore(position, weights, positionalScore);
        totalScore.incrementBy(score);
        position.setScoreContribution(score.getPositionScore());
        return totalScore;
    }


    /**
     * initialize the lookup table of scores to attribute to the board positions when calculating the worth.
     * These weights are counted more heavily at te beggiing of the game.
     */
    private void initializePositionalScoreArray()
    {
        int numRows = board_.getNumRows();
        int numCols = board_.getNumCols();
        int row, col, rowmin, colmin;
        positionalScore_ = new float[numRows + 1][numCols + 1];

        for ( row = 1; row <= numRows; row++ ) {    //rows
            rowmin = Math.min( row, numRows - row + 1 );
            for ( col = 1; col <= numCols; col++ ) {  //cols
                colmin = Math.min( col, numCols - col + 1 );
                positionalScore_[row][col] = 0.0f; // default neutral value


                int lineNo = Math.min(rowmin, colmin);
                if (lineNo < LINE_VALS.length) {
                    if (rowmin == colmin)  {
                        // corners get emphasized
                        positionalScore_[row][col] = 1.5f * (LINE_VALS[lineNo - 1]);
                    }
                    else {
                        positionalScore_[row][col] = LINE_VALS[lineNo - 1];
                    }
                }
            }
        }
    }

    /**
     * @return the score contribution from a single point on the board
     */
    private PositionalScore calcPositionalScore(GoBoardPosition position, ParameterArray weights,
                                                       double positionalScore) {

        PositionalScore score = new PositionalScore();

        if (position.isInEye())  {
            if (position.isOccupied()) {
                // a dead enemy stone in the eye counts twice.
                score.deadStoneScore = position.getEye().isOwnedByPlayer1()? 2.0 : -2.0;
            }
            else {
                score.eyeSpaceScore = position.getEye().isOwnedByPlayer1()? 1.0 : -1.0;
            }
        }
        else if ( position.isOccupied() ) {
            GoStone stone = (GoStone)position.getPiece();

            int side = position.getPiece().isOwnedByPlayer1()? 1: -1;
            // penalize bad shape like empty triangles
            StringShapeAnalyzer sa = new StringShapeAnalyzer(board_);
            score.badShapeScore = -(side * sa.formsBadShape(position)
                                   * weights.get(GoWeights.BAD_SHAPE_WEIGHT_INDEX).getValue());

            // Usually a very low weight is assigned to where stone is played unless we are at the start of the game.
            score.posScore = side * weights.get(GoWeights.POSITIONAL_WEIGHT_INDEX).getValue() * positionalScore;
            score.healthScore =  weights.get(GoWeights.HEALTH_WEIGHT_INDEX).getValue() * stone.getHealth();

            if (GameContext.getDebugMode() > 1)  {
                stone.setPositionalScore(score);
            }
        }

        score.calcPositionScore();
        return score;
    }
}