package com.becker.game.twoplayer.tictactoe.test;

import com.becker.common.Location;
import com.becker.game.common.GamePiece;
import com.becker.game.twoplayer.common.TwoPlayerMove;
import com.becker.game.twoplayer.common.search.test.SearchableHelper;
import com.becker.game.twoplayer.common.search.test.strategy.ExpectedMoveMatrix;
import com.becker.game.twoplayer.common.search.test.strategy.MoveInfo;
import com.becker.game.twoplayer.common.search.test.strategy.NegaMaxStrategyTst;
import com.becker.game.twoplayer.common.search.test.strategy.NegaScoutStrategyTst;

/**
 * These results should be exactly the same as we get from minimax
 * because negamax is equivalent to minimax.
 * @author Barry Becker
 */
public class NegaScoutStrategyTest extends NegaScoutStrategyTst {

    private static final GamePiece PLAYER1_PIECE = new GamePiece(true);
    private static final GamePiece PLAYER2_PIECE = new GamePiece(false);

    @Override
    protected SearchableHelper createSearchableHelper() {
        return new TicTacToeHelper();
    }

    @Override
    protected ExpectedMoveMatrix getExpectedZeroLookAheadMoves() {
        return new ExpectedMoveMatrix(
            TwoPlayerMove.createMove(new Location(2, 2), 16, new GamePiece(true)),
            TwoPlayerMove.createMove(new Location(1, 1), -8, new GamePiece(false)),
            TwoPlayerMove.createMove(new Location(1, 2), 88, new GamePiece(true)),
            TwoPlayerMove.createMove(new Location(1, 3), -88, new GamePiece(false)),
            TwoPlayerMove.createMove(new Location(3, 1), 0, new GamePiece(true)),
            TwoPlayerMove.createMove(new Location(2, 3), -48, new GamePiece(false))
        );
    }

    @Override
    protected ExpectedMoveMatrix getExpectedOneLevelLookAheadMoves() {
        return new ExpectedMoveMatrix(
            new MoveInfo(TwoPlayerMove.createMove(new Location(1, 2), 4, PLAYER2_PIECE), 8),
            new MoveInfo(TwoPlayerMove.createMove(new Location(2, 1), 48, PLAYER1_PIECE), 7),
            new MoveInfo(TwoPlayerMove.createMove(new Location(3, 2), 28, PLAYER2_PIECE), 4),
            new MoveInfo(TwoPlayerMove.createMove(new Location(3, 2), 0, PLAYER1_PIECE), 5),
            new MoveInfo(TwoPlayerMove.createMove(new Location(2, 1), 0, PLAYER2_PIECE), 2),
            new MoveInfo(TwoPlayerMove.createMove(new Location(3, 1), -12, PLAYER1_PIECE), 3)
        );
    }

    @Override
    protected ExpectedMoveMatrix getExpectedOneLevelWithQuiescenceMoves() {
        return new ExpectedMoveMatrix(
            new MoveInfo(TwoPlayerMove.createMove(new Location(1, 2), 4, PLAYER2_PIECE), 8),
            new MoveInfo(TwoPlayerMove.createMove(new Location(3, 3), -4, PLAYER1_PIECE), 53),
            new MoveInfo(TwoPlayerMove.createMove(new Location(3, 2), 28, PLAYER2_PIECE), 4),
            new MoveInfo(TwoPlayerMove.createMove(new Location(1, 2), 0, PLAYER1_PIECE), 29),
            new MoveInfo(TwoPlayerMove.createMove(new Location(2, 1), 0, PLAYER2_PIECE), 2),
            new MoveInfo(TwoPlayerMove.createMove(new Location(3, 3), -52, PLAYER1_PIECE), 10)
        );
    }

    @Override
    protected ExpectedMoveMatrix getExpectedOneLevelWithQuiescenceAndABMoves() {
        return new ExpectedMoveMatrix(
            new MoveInfo(TwoPlayerMove.createMove(new Location(1, 2), 4, PLAYER2_PIECE), 8),
            new MoveInfo(TwoPlayerMove.createMove(new Location(3, 3), -4, PLAYER1_PIECE), 53),
            new MoveInfo(TwoPlayerMove.createMove(new Location(3, 2), 28, PLAYER2_PIECE), 4),
            new MoveInfo(TwoPlayerMove.createMove(new Location(1, 2), 0, PLAYER1_PIECE), 29),
            new MoveInfo(TwoPlayerMove.createMove(new Location(2, 1), 0, PLAYER2_PIECE), 2),
            new MoveInfo(TwoPlayerMove.createMove(new Location(3, 3), -52, PLAYER1_PIECE), 10)
        );
    }

    @Override
    protected ExpectedMoveMatrix getExpectedTwoLevelLookAheadMoves() {
        return new ExpectedMoveMatrix(
            new MoveInfo(TwoPlayerMove.createMove(new Location(1, 2), 4, PLAYER2_PIECE), 64),
            new MoveInfo(TwoPlayerMove.createMove(new Location(2, 1), 48, PLAYER1_PIECE), 49),
            new MoveInfo(TwoPlayerMove.createMove(new Location(3, 2), 28, PLAYER2_PIECE), 16),
            new MoveInfo(TwoPlayerMove.createMove(new Location(1, 2), 0, PLAYER1_PIECE), 29),
            new MoveInfo(TwoPlayerMove.createMove(new Location(2, 1), 0, PLAYER2_PIECE), 4),
            new MoveInfo(TwoPlayerMove.createMove(new Location(3, 3), -52, PLAYER1_PIECE), 11)
        );
    }

    @Override
    protected ExpectedMoveMatrix getExpectedFourLevelLookaheadMoves() {
        return new ExpectedMoveMatrix(
            new MoveInfo(TwoPlayerMove.createMove(new Location(1, 1), 8, PLAYER2_PIECE), 502),
            new MoveInfo(TwoPlayerMove.createMove(new Location(2, 1), 48, PLAYER1_PIECE), 249),
            new MoveInfo(TwoPlayerMove.createMove(new Location(3, 2), 28, PLAYER2_PIECE), 26),
            new MoveInfo(TwoPlayerMove.createMove(new Location(1, 2), 0, PLAYER1_PIECE), 61),
            new MoveInfo(TwoPlayerMove.createMove(new Location(2, 1), 0, PLAYER2_PIECE), 4),
            new MoveInfo(TwoPlayerMove.createMove(new Location(3, 1), -12, PLAYER1_PIECE), 13)
        );
    }

    @Override
    protected ExpectedMoveMatrix getExpectedFourLevelBest20PercentMoves() {
        return new ExpectedMoveMatrix(
             new MoveInfo(TwoPlayerMove.createMove(new Location(1, 2), 4, PLAYER2_PIECE), 37),
             new MoveInfo(TwoPlayerMove.createMove(new Location(2, 1), 48, PLAYER1_PIECE), 61),
             new MoveInfo(TwoPlayerMove.createMove(new Location(3, 2), 28, PLAYER2_PIECE), 26),
             new MoveInfo(TwoPlayerMove.createMove(new Location(1, 2), 0, PLAYER1_PIECE), 61),
             new MoveInfo(TwoPlayerMove.createMove(new Location(2, 1), 0, PLAYER2_PIECE), 4),
             new MoveInfo(TwoPlayerMove.createMove(new Location(3, 1), -12, PLAYER1_PIECE), 13)
        );
    }

    @Override
    protected ExpectedMoveMatrix getExpectedTwoLevelWithQuiescenceMoves() {
        return new ExpectedMoveMatrix(
            new MoveInfo(TwoPlayerMove.createMove(new Location(1, 2), 4, PLAYER2_PIECE), 59),
            new MoveInfo(TwoPlayerMove.createMove(new Location(2, 1), 48, PLAYER1_PIECE), 54),
            new MoveInfo(TwoPlayerMove.createMove(new Location(3, 2), 28, PLAYER2_PIECE), 14),
            new MoveInfo(TwoPlayerMove.createMove(new Location(1, 2), 0, PLAYER1_PIECE), 29),
            new MoveInfo(TwoPlayerMove.createMove(new Location(2, 1), 0, PLAYER2_PIECE), 4),
            new MoveInfo(TwoPlayerMove.createMove(new Location(3, 3), -52, PLAYER1_PIECE), 13)
        );
    }

    @Override
    protected ExpectedMoveMatrix getExpectedTwoLevelWithQuiescenceAndABMoves() {
        return new ExpectedMoveMatrix(
            new MoveInfo(TwoPlayerMove.createMove(new Location(1, 2), 4, PLAYER2_PIECE), 8),
            new MoveInfo(TwoPlayerMove.createMove(new Location(3, 3), -4, PLAYER1_PIECE), 53),
            new MoveInfo(TwoPlayerMove.createMove(new Location(3, 2), 28, PLAYER2_PIECE), 4),
            new MoveInfo(TwoPlayerMove.createMove(new Location(1, 2), 0, PLAYER1_PIECE), 29),
            new MoveInfo(TwoPlayerMove.createMove(new Location(2, 1), 4, PLAYER2_PIECE), 2),
            new MoveInfo(TwoPlayerMove.createMove(new Location(3, 3), -52, PLAYER1_PIECE), 10)
        );
    }

    @Override
    protected ExpectedMoveMatrix getExpectedThreeLevelWithQuiescenceMoves() {
        return new ExpectedMoveMatrix(
            new MoveInfo(TwoPlayerMove.createMove(new Location(1, 2), 4, PLAYER2_PIECE), 151),
            new MoveInfo(TwoPlayerMove.createMove(new Location(2, 1), 48, PLAYER1_PIECE), 259),
            new MoveInfo(TwoPlayerMove.createMove(new Location(3, 2), 28, PLAYER2_PIECE), 23),
            new MoveInfo(TwoPlayerMove.createMove(new Location(1, 2), 0, PLAYER1_PIECE), 69),
            new MoveInfo(TwoPlayerMove.createMove(new Location(2, 1), 0, PLAYER2_PIECE), 4),
            new MoveInfo(TwoPlayerMove.createMove(new Location(3, 1), -12, PLAYER1_PIECE), 13)
        );
    }
    @Override
    protected ExpectedMoveMatrix getExpectedFourLevelWithQuiescenceMoves() {
        return new ExpectedMoveMatrix(
            new MoveInfo(TwoPlayerMove.createMove(new Location(1, 2), 4, PLAYER2_PIECE), 327),
            new MoveInfo(TwoPlayerMove.createMove(new Location(2, 1), 48, PLAYER1_PIECE), 261),
            new MoveInfo(TwoPlayerMove.createMove(new Location(3, 2), 28, PLAYER2_PIECE), 26),
            new MoveInfo(TwoPlayerMove.createMove(new Location(1, 2), 0, PLAYER1_PIECE), 62),
            new MoveInfo(TwoPlayerMove.createMove(new Location(2, 1), 0, PLAYER2_PIECE), 4),
            new MoveInfo(TwoPlayerMove.createMove(new Location(3, 1), -12, PLAYER1_PIECE), 13)
        );
    }

    @Override
    protected ExpectedMoveMatrix getExpectedFourLevelNoAlphaBetaMoves() {
        return new ExpectedMoveMatrix(
            new MoveInfo(TwoPlayerMove.createMove(new Location(1, 1), 8, PLAYER2_PIECE), 502),
            new MoveInfo(TwoPlayerMove.createMove(new Location(2, 1), 48, PLAYER1_PIECE), 249),
            new MoveInfo(TwoPlayerMove.createMove(new Location(3, 2), 28, PLAYER2_PIECE), 26),
            new MoveInfo(TwoPlayerMove.createMove(new Location(1, 2), 0, PLAYER1_PIECE), 61),
            new MoveInfo(TwoPlayerMove.createMove(new Location(2, 1), 0, PLAYER2_PIECE), 4),
            new MoveInfo(TwoPlayerMove.createMove(new Location(3, 1), -12, PLAYER1_PIECE), 13)
        );
    }
}