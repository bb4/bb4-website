package com.becker.game.twoplayer.tictactoe.test;

import com.becker.common.Location;
import com.becker.game.common.GameController;
import com.becker.game.common.GamePiece;
import com.becker.game.twoplayer.common.TwoPlayerController;
import com.becker.game.twoplayer.common.TwoPlayerMove;
import com.becker.game.twoplayer.common.TwoPlayerOptions;
import com.becker.game.twoplayer.common.search.test.SearchableHelper;
import com.becker.game.twoplayer.common.search.test.strategy.ExpectedMoveMatrix;
import com.becker.game.twoplayer.common.search.test.strategy.MiniMaxStrategyTst;
import com.becker.game.twoplayer.tictactoe.TicTacToeController;
import com.becker.game.twoplayer.tictactoe.TicTacToeOptions;

/**
 * @author Barry Becker
 */
public class MiniMaxStrategyTest extends MiniMaxStrategyTst {

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
    protected ExpectedMoveMatrix getExpectedTwoLevelLookAheadMoves() {
        return ExpectedSearchStrategyResults.EXPECTED_TWO_LEVEL_LOOKAHEAD;

    }

    @Override
    protected ExpectedMoveMatrix getExpectedFourLevelLookaheadMoves() {
        return ExpectedSearchStrategyResults.EXPECTED_FOUR_LEVEL_LOOKAHEAD;
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