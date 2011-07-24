package com.becker.game.twoplayer.go.board.analysis;

import com.becker.game.common.GameContext;
import com.becker.game.common.Move;
import com.becker.game.twoplayer.common.TwoPlayerMove;
import com.becker.game.twoplayer.go.board.GoBoard;
import com.becker.game.twoplayer.go.board.GoProfiler;
import com.becker.game.twoplayer.go.board.PositionalScore;
import com.becker.game.twoplayer.go.board.WorthInfo;
import com.becker.game.twoplayer.go.board.move.GoMove;
import com.becker.game.twoplayer.go.options.GoWeights;
import com.becker.optimization.parameter.ParameterArray;

import static com.becker.game.twoplayer.common.search.strategy.SearchStrategy.WINNING_VALUE;
import static com.becker.game.twoplayer.go.GoController.WIN_THRESHOLD;

/**
 * For statically evaluating the current state of a GoBoard.
 *
 * @author Barry Becker
 */
public class WorthCalculator {

    private GoBoard board_;

    /** a lookup table of scores to attribute to the board positions when calculating the worth. */
    private PositionalScoreAnalyzer positionalScorer_;

    private TerritoryAnalyzer territoryAnalyzer;

    private WorthInfo info;

    /**
     * Constructor.
     */
    public WorthCalculator(GoBoard board, TerritoryAnalyzer terrAnalyzer) {
        board_ = board;
        territoryAnalyzer = terrAnalyzer;
        positionalScorer_ = new PositionalScoreAnalyzer(board.getNumRows(), board.getNumCols());
    }

    /**
     * Statically evaluate the board position.
     *
     * @return statically evaluated value of the board.
     *   a positive value means that player1 has the advantage.
     *   A big negative value means a good move for p2.
     */
    public int worth(Move lastMove, ParameterArray weights) {
        GoProfiler.getInstance().startCalcWorth();
        double worth = calculateWorth(board_, lastMove, weights);

        GameContext.log(3, "GoController.worth: worth="+worth);
        if ( worth < -WIN_THRESHOLD ) {
            // then the margin is too great the losing player should resign
            return -WINNING_VALUE;
        }
        else if ( worth > WIN_THRESHOLD ) {
            // then the margin is too great the losing player should resign
            return WINNING_VALUE;
        }
        GoProfiler.getInstance().stopCalcWorth();
        return (int) worth;
    }

    /**
     * Statically evaluate the board position.
     *     The most naive thing we could do here is to simply return the sum of the captures
     * for player1 - sum of the captures for player2. However for go, since search is not likely to
     * be that useful given the huge branch factor, we need to rely heavily on sophisticated evaluation.
     *     We have every space on the board have a score representing
     * how strongly it is controlled by player1 (black).  If the score is 1.00, then that
     * position is inside or part of an unconditionally alive group owned by player1 (black)
     * or it is inside a dead white group.
     * If the score is -1.00 then its player 2's(white) unconditionally alive group
     * or black's dead group. A blank dame might have a score
     * of 0. A white stone might have a positive score if its part of a white group
     * which is considered mostly dead.
     *
     * @return statically evaluated value of the board.
     */
    private double calculateWorth(GoBoard board, Move lastMove, ParameterArray weights) {

        // adjust for board size - so worth will be comparable regardless of board size.
        double scaleFactor = 361.0 / Math.pow(board.getNumRows(), 2);
        GameStageBoostCalculator gameStageBoostCalc_= new GameStageBoostCalculator(board.getNumRows());
        double gameStageBoost = gameStageBoostCalc_.getGameStageBoost(board.getMoveList().getNumMoves());

        // Update status of groups and stones on the board. Expensive. Changes board state.
        territoryAnalyzer.updateTerritory(false);

        PositionalScore totalScore = new PositionalScore();
        PositionalScore[][] positionScores = new PositionalScore[board.getNumRows()][board.getNumCols()];
        for (int row = 1; row <= board.getNumRows(); row++ ) {
            for (int col = 1; col <= board.getNumCols(); col++ ) {

                PositionalScore s =
                    positionalScorer_.determineScoreForPosition(board, row, col, gameStageBoost, weights);
                positionScores[row-1][col-1] = s;
                totalScore.incrementBy(s);
            }
        }

        double territoryDelta = territoryAnalyzer.getTerritoryDelta();
        double captureScore = getCaptureScore(board, weights);
        double positionalScore = totalScore.getPositionScore();
        double worth = scaleFactor * (positionalScore + captureScore + territoryDelta);

        if (GameContext.getDebugMode() > 0)  {
            String desc = totalScore.getDescription(worth, captureScore, territoryDelta, scaleFactor);
            ((TwoPlayerMove) lastMove).setScoreDescription(desc);
        }
        int blackCap = board.getNumCaptures( true );
        int whiteCap = board.getNumCaptures( false );
        this.info =
                new WorthInfo(gameStageBoost, territoryDelta, captureScore, blackCap, whiteCap,
                              positionalScore, positionScores, ((GoMove)lastMove).getCaptures(), worth, board.getMoveList().copy());
        return worth;
    }

    public WorthInfo getWorthInfo() {
        return info;
    }

    /**
     * @return score attributed to captured stones.
     */
    private double getCaptureScore(GoBoard board, ParameterArray weights) {
        double captureWt = weights.get(GoWeights.CAPTURE_WEIGHT_INDEX).getValue();
        return captureWt * (board.getNumCaptures( false ) - board.getNumCaptures( true ));
    }
}