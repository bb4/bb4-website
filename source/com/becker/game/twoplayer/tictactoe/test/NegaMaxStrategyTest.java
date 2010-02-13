package com.becker.game.twoplayer.tictactoe.test;

import com.becker.common.Location;
import com.becker.game.common.GamePiece;
import com.becker.game.twoplayer.common.TwoPlayerMove;
import com.becker.game.twoplayer.common.search.test.SearchableHelper;
import com.becker.game.twoplayer.common.search.test.strategy.ExpectedMoveMatrix;
import com.becker.game.twoplayer.common.search.test.strategy.NegaMaxStrategyTst;

/**
 * @author Barry Becker
 */
public class NegaMaxStrategyTest extends NegaMaxStrategyTst {

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
                TwoPlayerMove.createMove(new Location(2, 3), -48, new GamePiece(false)));
    }


    @Override
    protected ExpectedMoveMatrix getExpectedOneLevelLookAheadMoves() {
        return new ExpectedMoveMatrix(
                TwoPlayerMove.createMove(new Location(2, 1), 4, new GamePiece(false)),
                TwoPlayerMove.createMove(new Location(1, 2), 48, new GamePiece(true)),
                TwoPlayerMove.createMove(new Location(3, 2), 28, new GamePiece(false)),
                TwoPlayerMove.createMove(new Location(3, 2), 0, new GamePiece(true)),
                TwoPlayerMove.createMove(new Location(2, 1), 0, new GamePiece(false)),
                TwoPlayerMove.createMove(new Location(1, 3), -12, new GamePiece(true)));
    }

    @Override
    protected ExpectedMoveMatrix getExpectedOneLevelWithQuiescenceMoves() {
        return new ExpectedMoveMatrix(
                TwoPlayerMove.createMove(new Location(2, 1), 4, new GamePiece(false)),
                TwoPlayerMove.createMove(new Location(1, 2), 48, new GamePiece(true)),
                TwoPlayerMove.createMove(new Location(3, 2), 28, new GamePiece(false)),
                TwoPlayerMove.createMove(new Location(2, 3), -32, new GamePiece(true)),
                TwoPlayerMove.createMove(new Location(2, 1), 0, new GamePiece(false)),
                TwoPlayerMove.createMove(new Location(1, 3), -12, new GamePiece(true)));
    }

    @Override
    protected ExpectedMoveMatrix getExpectedTwoLevelLookAheadMoves() {
        return new ExpectedMoveMatrix(
                TwoPlayerMove.createMove(new Location(2, 1), 4, new GamePiece(false)),
                TwoPlayerMove.createMove(new Location(1, 2), 48, new GamePiece(true)),
                TwoPlayerMove.createMove(new Location(3, 2), 28, new GamePiece(false)),
                TwoPlayerMove.createMove(new Location(1, 2), 0, new GamePiece(true)),
                TwoPlayerMove.createMove(new Location(2, 1), 0, new GamePiece(false)),
                TwoPlayerMove.createMove(new Location(3, 3), -52, new GamePiece(true)));
    }

    @Override
    protected ExpectedMoveMatrix getExpectedFourLevelLookaheadMoves() {
        return new ExpectedMoveMatrix(
                TwoPlayerMove.createMove(new Location(1, 1), 8, new GamePiece(false)),
                TwoPlayerMove.createMove(new Location(1, 2), 48, new GamePiece(true)),
                TwoPlayerMove.createMove(new Location(3, 2), 28, new GamePiece(false)),
                TwoPlayerMove.createMove(new Location(1, 2), 0, new GamePiece(true)),
                TwoPlayerMove.createMove(new Location(2, 1), 0, new GamePiece(false)),
                TwoPlayerMove.createMove(new Location(1, 3), -12, new GamePiece(true)));
    }

    @Override
    protected ExpectedMoveMatrix getExpectedFourLevelBest20PercentMoves() {
        return new ExpectedMoveMatrix(
                TwoPlayerMove.createMove(new Location(2, 1), 4, new GamePiece(false)),
                TwoPlayerMove.createMove(new Location(1, 2), 48, new GamePiece(true)),
                TwoPlayerMove.createMove(new Location(3, 2), 28, new GamePiece(false)),
                TwoPlayerMove.createMove(new Location(1, 2), 0, new GamePiece(true)),
                TwoPlayerMove.createMove(new Location(2, 1), 0, new GamePiece(false)),
                TwoPlayerMove.createMove(new Location(1, 3), -12, new GamePiece(true)));
    }

    @Override
    protected ExpectedMoveMatrix getExpectedFourLevelWithQuiescenceMoves() {
        return new ExpectedMoveMatrix(
                TwoPlayerMove.createMove(new Location(1, 1), 8, new GamePiece(false)),
                TwoPlayerMove.createMove(new Location(1, 2), 48, new GamePiece(true)),
                TwoPlayerMove.createMove(new Location(3, 2), 28, new GamePiece(false)),
                TwoPlayerMove.createMove(new Location(1, 2), 0, new GamePiece(true)),
                TwoPlayerMove.createMove(new Location(2, 1), 0, new GamePiece(false)),
                TwoPlayerMove.createMove(new Location(1, 3), -12, new GamePiece(true)));
    }

    @Override
    protected ExpectedMoveMatrix getExpectedFourLevelNoAlphaBetaMoves() {
        return new ExpectedMoveMatrix(
                TwoPlayerMove.createMove(new Location(1, 1), 8, new GamePiece(false)),
                TwoPlayerMove.createMove(new Location(1, 2), 48, new GamePiece(true)),
                TwoPlayerMove.createMove(new Location(3, 2), 28, new GamePiece(false)),
                TwoPlayerMove.createMove(new Location(1, 2), 0, new GamePiece(true)),
                TwoPlayerMove.createMove(new Location(2, 1), 0, new GamePiece(false)),
                TwoPlayerMove.createMove(new Location(1, 3), -12, new GamePiece(true)));
    }
}