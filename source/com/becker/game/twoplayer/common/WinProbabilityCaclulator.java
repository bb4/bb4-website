package com.becker.game.twoplayer.common;

import com.becker.game.common.GameContext;
import com.becker.game.common.MoveList;

import static com.becker.game.twoplayer.common.search.strategy.SearchStrategy.WINNING_VALUE;

/**
 * Determines the approximate likelihood of a player winning the game.
 *
 * @author Barry Becker
 */
public class WinProbabilityCaclulator {

    /**
     * Constructor.
     */
    public WinProbabilityCaclulator() {
    }

    /**
     * Returns a number between 0 and 1 representing the estimated probability of player 1 winning the game.
     * The chance of player2 winning = 1 - chance of p1 winning.
     *
     * @param moveList list of moves made so far.
     * @return estimated chance of player one winning the game
     */
    public final double getChanceOfPlayer1Winning(MoveList moveList) {
        // if true then too early in the game to tell.
        TwoPlayerMove lastMove = (TwoPlayerMove) moveList.getLastMove();

        // at the beginning of the game its anybody's guess : 50-50
        if (moveList.getNumMoves() < 4 ) {
            return 0.5f;
        }

        assert(lastMove != null);

        // we can use this formula to estimate the outcome:       
        double inherVal = lastMove.getInheritedValue();
        if ( Math.abs( inherVal ) > WINNING_VALUE )
            GameContext.log( 1, "TwoPlayerController: warning: the score for p1 is greater than WINNING_VALUE(" +
                    WINNING_VALUE + ")  inheritedVal=" + inherVal );

        return computeChanceOfWinning(inherVal);
    }


    /**
     * @param lastMoveValue inherited value for last move played.
     * @return Something close to 1 if the chance of winning for player 1 is really good. Close to -1 if bad.
     */
    private double computeChanceOfWinning(double lastMoveValue) {
        double val = lastMoveValue + WINNING_VALUE;
        return val / (2.0 * WINNING_VALUE);
    }
}