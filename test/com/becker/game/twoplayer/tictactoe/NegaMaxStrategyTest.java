package com.becker.game.twoplayer.tictactoe;

import com.becker.common.Location;
import com.becker.game.twoplayer.common.TwoPlayerMove;
import com.becker.game.twoplayer.common.search.SearchableHelper;
import com.becker.game.twoplayer.common.search.strategy.integration.ExpectedMoveMatrix;
import com.becker.game.twoplayer.common.search.strategy.integration.MoveInfo;
import com.becker.game.twoplayer.common.search.strategy.integration.NegaMaxStrategyTst;

/**
 * These results should be exactly the same as we get from minimax
 * because negamax is equivalent to minimax.
 * @author Barry Becker
 */
public class NegaMaxStrategyTest extends NegaMaxStrategyTst {

    @Override
    protected SearchableHelper createSearchableHelper() {
        return new TicTacToeHelper();
    }

    @Override
    protected ExpectedMoveMatrix getExpectedZeroLookAheadMoves() {
        return ExpectedSearchStrategyResults.EXPECTED_ZERO_LOOKAHEAD_MOVES;
    }

    @Override
    protected ExpectedMoveMatrix getExpectedOneLevelLookAheadMoves() {
        return ExpectedSearchStrategyResults.EXPECTED_ONE_LEVEL_LOOKAHEAD;
    }

    @Override
    protected ExpectedMoveMatrix getExpectedOneLevelWithQuiescenceMoves() {
        return ExpectedSearchStrategyResults.EXPECTED_ONE_LEVEL_WITH_QUIESCENCE;
    }

    @Override
    protected ExpectedMoveMatrix getExpectedOneLevelWithQuiescenceAndABMoves() {
        return ExpectedSearchStrategyResults.EXPECTED_ONE_LEVEL_WITH_QUIESCENCE_AND_AB;
    }

    @Override
    protected ExpectedMoveMatrix getExpectedTwoLevelLookAheadMoves() {
        return ExpectedSearchStrategyResults.EXPECTED_TWO_LEVEL_LOOKAHEAD;
    }

    @Override
    protected ExpectedMoveMatrix getExpectedFourLevelLookaheadMoves() {
        return new ExpectedMoveMatrix(   new MoveInfo(TwoPlayerMove.createMove(new Location(1, 1), 8, PLAYER2_PIECE), 782),
            new MoveInfo(TwoPlayerMove.createMove(new Location(2, 1), 48, PLAYER1_PIECE), 249),
            new MoveInfo(TwoPlayerMove.createMove(new Location(3, 2), 28, PLAYER2_PIECE), 26),
            new MoveInfo(TwoPlayerMove.createMove(new Location(1, 2), 0, PLAYER1_PIECE), 52),
            new MoveInfo(TwoPlayerMove.createMove(new Location(2, 1), 0, PLAYER2_PIECE), 4),
            new MoveInfo(TwoPlayerMove.createMove(new Location(3, 1), -12, PLAYER1_PIECE), 13)
        );
    }

    @Override
    protected ExpectedMoveMatrix getExpectedFourLevelBest20PercentMoves() {
        return ExpectedSearchStrategyResults.EXPECTED_FOUR_LEVEL_BEST_20_PERCENT;
    }

    @Override
    protected ExpectedMoveMatrix getExpectedTwoLevelWithQuiescenceMoves() {
        return ExpectedSearchStrategyResults.EXPECTED_TWO_LEVEL_WITH_QUIESCENCE;
    }

    @Override
    protected ExpectedMoveMatrix getExpectedTwoLevelWithQuiescenceAndABMoves() {
        return ExpectedSearchStrategyResults.EXPECTED_TWO_LEVEL_WITH_QUIESCENCE_AND_AB;
    }

    @Override
    protected ExpectedMoveMatrix getExpectedThreeLevelWithQuiescenceMoves() {
        return ExpectedSearchStrategyResults.EXPECTED_THREE_LEVEL_WITH_QUIESCENCE;
    }
    @Override
    protected ExpectedMoveMatrix getExpectedFourLevelWithQuiescenceMoves() {
        return ExpectedSearchStrategyResults.EXPECTED_FOUR_LEVEL_WITH_QUIESCENCE;
    }

    @Override
    protected ExpectedMoveMatrix getExpectedFourLevelNoAlphaBetaMoves() {
        return ExpectedSearchStrategyResults.EXPECTED_FOUR_LEVEL_NO_ALPHA_BETA;
    }
}