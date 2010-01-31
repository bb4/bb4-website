package com.becker.game.twoplayer.tictactoe.test;

import com.becker.common.Location;
import com.becker.game.common.GamePiece;
import com.becker.game.twoplayer.common.TwoPlayerMove;
import com.becker.game.twoplayer.common.search.test.SearchableHelper;
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
    protected TwoPlayerMove getExpectedZeroLookAheadMove() {
        return TwoPlayerMove.createMove(new Location(2, 2), 16, new GamePiece(true));
    }

    @Override
    protected TwoPlayerMove getExpectedOneLevelLookAheadMove() {
        return TwoPlayerMove.createMove(new Location(2, 1), 4, new GamePiece(false));
    }

    @Override
    protected TwoPlayerMove getExpectedOneLevelWithQuiescenceMove() {
        return TwoPlayerMove.createMove(new Location(2, 1), 4, new GamePiece(false));
    }

    @Override
    protected TwoPlayerMove getExpectedTwoLevelLookAheadMove() {
        return TwoPlayerMove.createMove(new Location(2, 1), 4, new GamePiece(false));
    }

    @Override
    protected TwoPlayerMove getExpectedFourLevelLookaheadMove() {
        return TwoPlayerMove.createMove(new Location(1, 1), 4, new GamePiece(false));
    }

    @Override
    protected TwoPlayerMove getExpectedFourLevelBest20PercentMove() {
        return TwoPlayerMove.createMove(new Location(2, 1), 4, new GamePiece(false));
    }

    @Override
    protected TwoPlayerMove getExpectedFourLevelWithQuiescenceMove() {
        return TwoPlayerMove.createMove(new Location(1, 1), 8, new GamePiece(false));
    }

    @Override
    protected TwoPlayerMove getExpectedFourLevelNoAlphaBetaMove() {
        return TwoPlayerMove.createMove(new Location(1, 1), 4, new GamePiece(false));
    }
}