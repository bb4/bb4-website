package com.becker.game.twoplayer.tictactoe.test;

import com.becker.common.Location;
import com.becker.game.common.GamePiece;
import com.becker.game.twoplayer.common.TwoPlayerMove;
import com.becker.game.twoplayer.common.search.test.SearchableHelper;
import com.becker.game.twoplayer.common.search.test.strategy.ExpectedMoveMatrix;
import com.becker.game.twoplayer.common.search.test.strategy.NegaMaxStrategyTst;

/**
 * These results should be exactly the same as we get from minimax
 * because negamax is equivalent to minimax.
 * @author Barry Becker
 */
public class NegaMaxStrategyTest extends NegaMaxStrategyTst {

    private static final GamePiece PLAYER1_PIECE = new GamePiece(true);
    private static final GamePiece PLAYER2_PIECE = new GamePiece(false);

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

        /* getting
        return new ExpectedMoveMatrix(
            TwoPlayerMove.createMove(new Location(1, 1), 8, PLAYER2_PIECE),   // beginningP1                   // want 1,2
            TwoPlayerMove.createMove(new Location(3, 3), -4, PLAYER1_PIECE),  //             "midGameCenterO"
            TwoPlayerMove.createMove(new Location(2, 1), 80, PLAYER2_PIECE),  // middleP1    "lateMidGameX" :   // want 3,2
            TwoPlayerMove.createMove(new Location(1, 2), 0, PLAYER1_PIECE),   //             "lateMidGameO";
            TwoPlayerMove.createMove(new Location(2, 1), 0, PLAYER2_PIECE),   // endP1       "endGameX";
            TwoPlayerMove.createMove(new Location(3, 3), -52, PLAYER1_PIECE)); // endP2      "endGame0";
         */

        /* want
        TwoPlayerMove.createMove(new Location(1, 2), 4, PLAYER2_PIECE),  // beginningP1
            TwoPlayerMove.createMove(new Location(3, 3), -4, PLAYER1_PIECE), //             "midGameCenterO"
            TwoPlayerMove.createMove(new Location(3, 2), 28, PLAYER2_PIECE), // middleP1    "lateMidGameX" :
            TwoPlayerMove.createMove(new Location(1, 2), 0, PLAYER1_PIECE),//             "lateMidGameO";
            TwoPlayerMove.createMove(new Location(2, 1), 0, PLAYER2_PIECE),  // endP1       "endGameX";
            TwoPlayerMove.createMove(new Location(3, 3), -52, PLAYER1_PIECE) // endP2       "endGame0";   */
       
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