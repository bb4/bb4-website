package com.becker.game.twoplayer.go.board;

import ca.dj.jigo.sgf.tokens.SourceToken;
import com.becker.game.common.GameContext;
import com.becker.game.common.Move;
import com.becker.game.common.MoveList;
import com.becker.game.common.board.CaptureList;
import com.becker.game.twoplayer.common.TwoPlayerMove;
import com.becker.game.twoplayer.go.board.analysis.GameStageBoostCalculator;
import com.becker.game.twoplayer.go.board.analysis.PositionalScoreAnalyzer;
import com.becker.game.twoplayer.go.options.GoWeights;
import com.becker.optimization.parameter.ParameterArray;

import java.util.Arrays;

import static com.becker.game.twoplayer.common.search.strategy.SearchStrategy.WINNING_VALUE;
import static com.becker.game.twoplayer.go.GoController.WIN_THRESHOLD;

/**
 * All the stuff used to compute the worth.
 * temp class used for debugging.
 *
 * @author Barry Becker
 */
public class WorthInfo {

    private double gameStageBoost;
    private double territoryDelta;
    private double captureScore;
    private int blackCap;
    private int whiteCap;
    private double positionalScore;
    private PositionalScore[][] positionalScores;
    private CaptureList captures;
    private double worth;
    private MoveList moves;

    /**
     * Constructor.
     */
    public WorthInfo(double gameStageBoost, double territoryDelta,
                     double captureScore, int blackCap, int whiteCap, double positionalScore, PositionalScore[][] positionalScores,
                     CaptureList captures, double worth, MoveList moves) {
        this.gameStageBoost = gameStageBoost;
        this.territoryDelta = territoryDelta;
        this.captureScore = captureScore;
        this.blackCap = blackCap;
        this.whiteCap = whiteCap;
        this.positionalScore = positionalScore;
        this.positionalScores  = positionalScores;
        this.captures = captures;
        this.worth = worth;
        this.moves = moves;
    }

    /**
     *  Show all the worth information
     *  @return string form.
     */
    public String toString() {
        StringBuilder bldr = new StringBuilder();
        bldr.append("gameStageBoost=" + gameStageBoost  +" territoryDelta=" + territoryDelta
                + " captureScore=" + captureScore + "(b="+ blackCap + " w="+ whiteCap + ") positionalScore="+positionalScore
                + (captures!=null? " captures=" + captures  : "")
                + " worth=" + worth + " \nposScores:\n");
        for (PositionalScore[] posScoreRow : positionalScores ) {
            for (PositionalScore pscore : posScoreRow) {
                bldr.append(pscore);
            }
            bldr.append("\n");
        }
        bldr.append("moves=" + moves);
        bldr.append("\n");

        return bldr.toString();
    }
}