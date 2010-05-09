package com.becker.game.twoplayer.tictactoe;

import com.becker.common.Location;
import com.becker.game.common.GamePiece;
import com.becker.game.twoplayer.common.TwoPlayerMove;
import com.becker.game.twoplayer.common.search.test.strategy.ExpectedMoveMatrix;
import com.becker.game.twoplayer.common.search.test.strategy.MoveInfo;

/**
 * Expected generated moves for search tests for both MiniMax and NegaMax algorthms since
 * the should produce identiacal resutls in all cases (just the implementation is a little different).
 * @author Barry Becker
 */
public class ExpectedSearchStrategyResults {

    private static final GamePiece PLAYER1_PIECE = new GamePiece(true);
    private static final GamePiece PLAYER2_PIECE = new GamePiece(false);

    private ExpectedSearchStrategyResults() {}

    static final ExpectedMoveMatrix EXPECTED_ZERO_LOOKAHEAD_MOVES = new ExpectedMoveMatrix(
            new MoveInfo(TwoPlayerMove.createMove(new Location(2, 2), 16, PLAYER1_PIECE), 0),  // beginningP1
            new MoveInfo(TwoPlayerMove.createMove(new Location(1, 1), -8, PLAYER2_PIECE), 0),  // beginningP2
            new MoveInfo(TwoPlayerMove.createMove(new Location(1, 2), 88, PLAYER1_PIECE), 0),  // middleP1
            new MoveInfo(TwoPlayerMove.createMove(new Location(1, 3), -88, PLAYER2_PIECE), 0), // middleP2
            new MoveInfo(TwoPlayerMove.createMove(new Location(3, 1), 0, PLAYER1_PIECE), 0),   // endP1
            new MoveInfo(TwoPlayerMove.createMove(new Location(2, 3), -48, PLAYER2_PIECE), 0)  // endP2
    );

    static final ExpectedMoveMatrix EXPECTED_ONE_LEVEL_LOOKAHEAD = new ExpectedMoveMatrix(
            new MoveInfo(TwoPlayerMove.createMove(new Location(1, 2), 4, PLAYER2_PIECE),  8),
            new MoveInfo(TwoPlayerMove.createMove(new Location(2, 1), 48, PLAYER1_PIECE), 7),
            new MoveInfo(TwoPlayerMove.createMove(new Location(3, 2), 28, PLAYER2_PIECE), 4),
            new MoveInfo(TwoPlayerMove.createMove(new Location(3, 2), 0, PLAYER1_PIECE),  5), //   "lateMidGameO";   seems wrong. should be  (1,2)
            new MoveInfo(TwoPlayerMove.createMove(new Location(2, 1), 0, PLAYER2_PIECE),  2),
            new MoveInfo(TwoPlayerMove.createMove(new Location(3, 1), -12, PLAYER1_PIECE), 3)
    );

    static final ExpectedMoveMatrix EXPECTED_ONE_LEVEL_WITH_QUIESCENCE = new ExpectedMoveMatrix(

            TwoPlayerMove.createMove(new Location(1, 2), 4, PLAYER2_PIECE),  // beginningP1
            TwoPlayerMove.createMove(new Location(3, 3), -4, PLAYER1_PIECE), //             "midGameCenterO"
            TwoPlayerMove.createMove(new Location(3, 2), 28, PLAYER2_PIECE), // middleP1    "lateMidGameX" :
            TwoPlayerMove.createMove(new Location(1, 2), 0, PLAYER1_PIECE),//             "lateMidGameO";   
            TwoPlayerMove.createMove(new Location(2, 1), 0, PLAYER2_PIECE),  // endP1       "endGameX";
            TwoPlayerMove.createMove(new Location(3, 3), -52, PLAYER1_PIECE) // endP2       "endGame0";
    );

    static final ExpectedMoveMatrix EXPECTED_ONE_LEVEL_WITH_QUIESCENCE_AND_AB = new ExpectedMoveMatrix(

            TwoPlayerMove.createMove(new Location(1, 2), 4, PLAYER2_PIECE),  // beginningP1
            TwoPlayerMove.createMove(new Location(3, 3), -4, PLAYER1_PIECE), //             "midGameCenterO"
            TwoPlayerMove.createMove(new Location(3, 2), 28, PLAYER2_PIECE), // middleP1    "lateMidGameX" :
            TwoPlayerMove.createMove(new Location(1, 2), 0, PLAYER1_PIECE),//             "lateMidGameO";
            TwoPlayerMove.createMove(new Location(2, 1), 0, PLAYER2_PIECE),  // endP1       "endGameX";
            TwoPlayerMove.createMove(new Location(3, 3), -52, PLAYER1_PIECE) // endP2       "endGame0";
    );

    static final ExpectedMoveMatrix EXPECTED_TWO_LEVEL_WITH_QUIESCENCE = new ExpectedMoveMatrix(
            TwoPlayerMove.createMove(new Location(1, 2), 4, new GamePiece(false)),
            TwoPlayerMove.createMove(new Location(2, 1), 48, new GamePiece(true)),
            TwoPlayerMove.createMove(new Location(3, 2), 28, new GamePiece(false)),
            TwoPlayerMove.createMove(new Location(1, 2), 0, new GamePiece(true)),
            TwoPlayerMove.createMove(new Location(2, 1), 0, new GamePiece(false)),
            TwoPlayerMove.createMove(new Location(3, 3), -52, new GamePiece(true))
    );

    static final ExpectedMoveMatrix EXPECTED_TWO_LEVEL_WITH_QUIESCENCE_AND_AB = new ExpectedMoveMatrix(

            TwoPlayerMove.createMove(new Location(1, 2), 4, PLAYER2_PIECE),  // beginningP1
            TwoPlayerMove.createMove(new Location(3, 3), -4, PLAYER1_PIECE), //             "midGameCenterO"
            TwoPlayerMove.createMove(new Location(3, 2), 28, PLAYER2_PIECE), // middleP1    "lateMidGameX" :
            TwoPlayerMove.createMove(new Location(1, 2), 0, PLAYER1_PIECE),  //             "lateMidGameO";   
            TwoPlayerMove.createMove(new Location(2, 1), 0, PLAYER2_PIECE),  // endP1       "endGameX";
            TwoPlayerMove.createMove(new Location(3, 3), -52, PLAYER1_PIECE) // endP2       "endGame0";
    );

    static final ExpectedMoveMatrix EXPECTED_THREE_LEVEL_WITH_QUIESCENCE = new ExpectedMoveMatrix(

            TwoPlayerMove.createMove(new Location(1, 2), 4, new GamePiece(false)),
            TwoPlayerMove.createMove(new Location(2, 1), 48, new GamePiece(true)),
            TwoPlayerMove.createMove(new Location(3, 2), 28, new GamePiece(false)),
            TwoPlayerMove.createMove(new Location(1, 2), 0, new GamePiece(true)),
            TwoPlayerMove.createMove(new Location(2, 1), 0, new GamePiece(false)),
            TwoPlayerMove.createMove(new Location(3, 1), -12, new GamePiece(true))
    );

    static final ExpectedMoveMatrix EXPECTED_TWO_LEVEL_LOOKAHEAD = new ExpectedMoveMatrix(
            TwoPlayerMove.createMove(new Location(1, 2), 4, PLAYER2_PIECE),
            TwoPlayerMove.createMove(new Location(2, 1), 48, PLAYER1_PIECE),
            TwoPlayerMove.createMove(new Location(3, 2), 28, PLAYER2_PIECE),
            TwoPlayerMove.createMove(new Location(1, 2), 0, PLAYER1_PIECE),
            TwoPlayerMove.createMove(new Location(2, 1), 0, PLAYER2_PIECE),
            TwoPlayerMove.createMove(new Location(3, 3), -52, PLAYER1_PIECE)
        );

    //static final ExpectedMoveMatrix EXPECTED_FOUR_LEVEL_LOOKAHEAD = new ExpectedMoveMatrix(
    //);

    static final ExpectedMoveMatrix EXPECTED_FOUR_LEVEL_BEST_20_PERCENT = new ExpectedMoveMatrix(
            TwoPlayerMove.createMove(new Location(1, 2), 4, PLAYER2_PIECE),
            TwoPlayerMove.createMove(new Location(2, 1), 48, PLAYER1_PIECE),
            TwoPlayerMove.createMove(new Location(3, 2), 28, PLAYER2_PIECE),
            TwoPlayerMove.createMove(new Location(1, 2), 0, PLAYER1_PIECE),
            TwoPlayerMove.createMove(new Location(2, 1), 0, PLAYER2_PIECE),
            TwoPlayerMove.createMove(new Location(3, 1), -12, PLAYER1_PIECE)
    );

    static final ExpectedMoveMatrix EXPECTED_FOUR_LEVEL_WITH_QUIESCENCE = new ExpectedMoveMatrix(
            TwoPlayerMove.createMove(new Location(1, 2), 4, PLAYER2_PIECE),  // beginningP1
            TwoPlayerMove.createMove(new Location(2, 1), 48, PLAYER1_PIECE), //             "midGameCenterO"
            TwoPlayerMove.createMove(new Location(3, 2), 28, PLAYER2_PIECE), // middleP1    "lateMidGameX"
            TwoPlayerMove.createMove(new Location(1, 2), 0, PLAYER1_PIECE),  //             "lateMidGameO"
            TwoPlayerMove.createMove(new Location(2, 1), 0, PLAYER2_PIECE),  // endP1       "endGameX"
            TwoPlayerMove.createMove(new Location(3, 1), -12, PLAYER1_PIECE) // endP2       "endGame0"
    );


    static final ExpectedMoveMatrix EXPECTED_FOUR_LEVEL_NO_ALPHA_BETA = new ExpectedMoveMatrix(
            new MoveInfo(TwoPlayerMove.createMove(new Location(1, 1), 8, PLAYER2_PIECE), 2080),
            new MoveInfo(TwoPlayerMove.createMove(new Location(2, 1), 48, PLAYER1_PIECE), 979),
            new MoveInfo(TwoPlayerMove.createMove(new Location(3, 2), 28, PLAYER2_PIECE), 50),
            new MoveInfo(TwoPlayerMove.createMove(new Location(1, 2), 0, PLAYER1_PIECE), 141),
            new MoveInfo(TwoPlayerMove.createMove(new Location(2, 1), 0, PLAYER2_PIECE), 4),
            new MoveInfo(TwoPlayerMove.createMove(new Location(3, 1), -12, PLAYER1_PIECE), 15)
    );
}