package com.becker.game.twoplayer.pente;

import com.becker.common.Location;
import com.becker.game.common.board.GamePiece;
import com.becker.game.twoplayer.common.TwoPlayerMove;

/**
 * Expected generated moves for search tests.
 * @author Barry Becker
 */
public class ExpectedSearchableResults {

    private static final GamePiece PLAYER1_PIECE = new GamePiece(true);
    private static final GamePiece PLAYER2_PIECE = new GamePiece(false);

    static final TwoPlayerMove[] EXPECTED_ALL_MIDDLE_GAME_MOVES_P1 = {
        TwoPlayerMove.createMove(new Location(4, 6), 208, PLAYER1_PIECE),
        TwoPlayerMove.createMove(new Location(7, 3), 184, PLAYER1_PIECE),
        TwoPlayerMove.createMove(new Location(3, 6), 184, PLAYER1_PIECE),
        TwoPlayerMove.createMove(new Location(3, 2), 184, PLAYER1_PIECE),
        TwoPlayerMove.createMove(new Location(6, 3), 160, PLAYER1_PIECE),
        TwoPlayerMove.createMove(new Location(3, 3), 160, PLAYER1_PIECE),
        TwoPlayerMove.createMove(new Location(7, 6), 72, PLAYER1_PIECE),
        TwoPlayerMove.createMove(new Location(2, 4), 72, PLAYER1_PIECE),
        TwoPlayerMove.createMove(new Location(5, 7), 48, PLAYER1_PIECE),
        TwoPlayerMove.createMove(new Location(2, 6), 40, PLAYER1_PIECE),
        TwoPlayerMove.createMove(new Location(2, 2), 40, PLAYER1_PIECE),
        TwoPlayerMove.createMove(new Location(4, 7), 32, PLAYER1_PIECE),
        TwoPlayerMove.createMove(new Location(1, 3), 32, PLAYER1_PIECE),
        TwoPlayerMove.createMove(new Location(8, 6), 24, PLAYER1_PIECE),
        TwoPlayerMove.createMove(new Location(8, 5), 24, PLAYER1_PIECE),
        TwoPlayerMove.createMove(new Location(8, 4), 24, PLAYER1_PIECE),
        TwoPlayerMove.createMove(new Location(8, 3), 24, PLAYER1_PIECE),
        TwoPlayerMove.createMove(new Location(7, 8), 24, PLAYER1_PIECE),
        TwoPlayerMove.createMove(new Location(6, 8), 24, PLAYER1_PIECE),
        TwoPlayerMove.createMove(new Location(6, 6), 24, PLAYER1_PIECE),
        TwoPlayerMove.createMove(new Location(5, 8), 24, PLAYER1_PIECE),
        TwoPlayerMove.createMove(new Location(5, 3), 24, PLAYER1_PIECE),
        TwoPlayerMove.createMove(new Location(4, 3), 24, PLAYER1_PIECE),
        TwoPlayerMove.createMove(new Location(1, 6), 24, PLAYER1_PIECE),
        TwoPlayerMove.createMove(new Location(1, 5), 24, PLAYER1_PIECE),
        TwoPlayerMove.createMove(new Location(1, 4), 24, PLAYER1_PIECE),
        TwoPlayerMove.createMove(new Location(1, 2), 24, PLAYER1_PIECE),
        TwoPlayerMove.createMove(new Location(7, 7), 8, PLAYER1_PIECE),
    };


    static final TwoPlayerMove[] EXPECTED_TOP_MIDDLE_GAME_MOVES_P1 =  {
        TwoPlayerMove.createMove(new Location(4, 6), 208, PLAYER1_PIECE),
        TwoPlayerMove.createMove(new Location(7, 3), 184, PLAYER1_PIECE),
        TwoPlayerMove.createMove(new Location(3, 6), 184, PLAYER1_PIECE),
        TwoPlayerMove.createMove(new Location(3, 2), 184, PLAYER1_PIECE),
        TwoPlayerMove.createMove(new Location(6, 3), 160, PLAYER1_PIECE),
        TwoPlayerMove.createMove(new Location(3, 3), 160, PLAYER1_PIECE),
    };

    static final TwoPlayerMove[] EXPECTED_ALL_END_GAME_MOVES_P1 = {
        TwoPlayerMove.createMove(new Location(3, 2), 168, PLAYER1_PIECE),
        TwoPlayerMove.createMove(new Location(3, 6), 144, PLAYER1_PIECE),
        TwoPlayerMove.createMove(new Location(3, 3), 144, PLAYER1_PIECE),
        TwoPlayerMove.createMove(new Location(3, 7), 32, PLAYER1_PIECE),
        TwoPlayerMove.createMove(new Location(1, 6), 32, PLAYER1_PIECE),
        TwoPlayerMove.createMove(new Location(1, 5), 32, PLAYER1_PIECE),
        TwoPlayerMove.createMove(new Location(1, 3), 32, PLAYER1_PIECE),
        TwoPlayerMove.createMove(new Location(1, 2), 32, PLAYER1_PIECE),
        TwoPlayerMove.createMove(new Location(2, 8), 24, PLAYER1_PIECE),
        TwoPlayerMove.createMove(new Location(2, 6), 24, PLAYER1_PIECE),
        TwoPlayerMove.createMove(new Location(2, 4), 24, PLAYER1_PIECE),
        TwoPlayerMove.createMove(new Location(2, 2), 24, PLAYER1_PIECE),
        TwoPlayerMove.createMove(new Location(8, 7), 8, PLAYER1_PIECE),
        TwoPlayerMove.createMove(new Location(8, 6), 8, PLAYER1_PIECE),
        TwoPlayerMove.createMove(new Location(8, 5), 8, PLAYER1_PIECE),
        TwoPlayerMove.createMove(new Location(8, 4), 8, PLAYER1_PIECE),
        TwoPlayerMove.createMove(new Location(8, 3), 8, PLAYER1_PIECE),
        TwoPlayerMove.createMove(new Location(8, 2), 8, PLAYER1_PIECE),
        TwoPlayerMove.createMove(new Location(8, 1), 8, PLAYER1_PIECE),
        TwoPlayerMove.createMove(new Location(7, 8), 8, PLAYER1_PIECE),
        TwoPlayerMove.createMove(new Location(7, 7), 8, PLAYER1_PIECE),
        TwoPlayerMove.createMove(new Location(7, 3), 8, PLAYER1_PIECE),
        TwoPlayerMove.createMove(new Location(7, 1), 8, PLAYER1_PIECE),
        TwoPlayerMove.createMove(new Location(6, 8), 8, PLAYER1_PIECE),
        TwoPlayerMove.createMove(new Location(6, 1), 8, PLAYER1_PIECE),
        TwoPlayerMove.createMove(new Location(5, 8), 8, PLAYER1_PIECE),
        TwoPlayerMove.createMove(new Location(5, 7), 8, PLAYER1_PIECE),
        TwoPlayerMove.createMove(new Location(5, 1), 8, PLAYER1_PIECE),
        TwoPlayerMove.createMove(new Location(4, 8), 8, PLAYER1_PIECE),
        TwoPlayerMove.createMove(new Location(4, 1), 8, PLAYER1_PIECE),
        TwoPlayerMove.createMove(new Location(3, 1), 8, PLAYER1_PIECE),
        TwoPlayerMove.createMove(new Location(1, 8), 8, PLAYER1_PIECE),
        TwoPlayerMove.createMove(new Location(1, 7), 8, PLAYER1_PIECE),
    };

    static final TwoPlayerMove[] EXPECTED_TOP_END_GAME_MOVES_P1 = {
        TwoPlayerMove.createMove(new Location(3, 2), 168, PLAYER1_PIECE),
        TwoPlayerMove.createMove(new Location(3, 6), 144, PLAYER1_PIECE),
        TwoPlayerMove.createMove(new Location(3, 3), 144, PLAYER1_PIECE),
        TwoPlayerMove.createMove(new Location(3, 7), 32, PLAYER1_PIECE),
        TwoPlayerMove.createMove(new Location(1, 6), 32, PLAYER1_PIECE),
        TwoPlayerMove.createMove(new Location(1, 5), 32, PLAYER1_PIECE),
        TwoPlayerMove.createMove(new Location(1, 3), 32, PLAYER1_PIECE)
    };

    static final TwoPlayerMove[] EXPECTED_ALL_MIDDLE_GAME_MOVES_P2 = {
        TwoPlayerMove.createMove(new Location(11, 10), 304, PLAYER2_PIECE),
        TwoPlayerMove.createMove(new Location(10, 13), -120, PLAYER2_PIECE),
        TwoPlayerMove.createMove(new Location(7, 12), -168, PLAYER2_PIECE),
        TwoPlayerMove.createMove(new Location(6, 12), -168, PLAYER2_PIECE),
        TwoPlayerMove.createMove(new Location(13, 10), -184, PLAYER2_PIECE),
        TwoPlayerMove.createMove(new Location(7, 11), -184, PLAYER2_PIECE),
        TwoPlayerMove.createMove(new Location(10, 14), -192, PLAYER2_PIECE),
        TwoPlayerMove.createMove(new Location(8, 15), -192, PLAYER2_PIECE),
        TwoPlayerMove.createMove(new Location(11, 13), -256, PLAYER2_PIECE),
        TwoPlayerMove.createMove(new Location(8, 11), -256, PLAYER2_PIECE),
        TwoPlayerMove.createMove(new Location(8, 10), -272, PLAYER2_PIECE),
        TwoPlayerMove.createMove(new Location(14, 10), -280, PLAYER2_PIECE),
        TwoPlayerMove.createMove(new Location(11, 8), -280, PLAYER2_PIECE),
        TwoPlayerMove.createMove(new Location(8, 14), -280, PLAYER2_PIECE),
        TwoPlayerMove.createMove(new Location(15, 12), -304, PLAYER2_PIECE),
        TwoPlayerMove.createMove(new Location(15, 10), -304, PLAYER2_PIECE),
        TwoPlayerMove.createMove(new Location(14, 12), -304, PLAYER2_PIECE),
        TwoPlayerMove.createMove(new Location(13, 12), -304, PLAYER2_PIECE),
        TwoPlayerMove.createMove(new Location(12, 13), -304, PLAYER2_PIECE),
        TwoPlayerMove.createMove(new Location(11, 7), -304, PLAYER2_PIECE),
        TwoPlayerMove.createMove(new Location(10, 9), -304, PLAYER2_PIECE),
        TwoPlayerMove.createMove(new Location(8, 13), -304, PLAYER2_PIECE),
        TwoPlayerMove.createMove(new Location(13, 8), -320, PLAYER2_PIECE),
        TwoPlayerMove.createMove(new Location(10, 8), -320, PLAYER2_PIECE),
        TwoPlayerMove.createMove(new Location(7, 14), -320, PLAYER2_PIECE),
        TwoPlayerMove.createMove(new Location(15, 11), -328, PLAYER2_PIECE),
        TwoPlayerMove.createMove(new Location(13, 9), -328, PLAYER2_PIECE),
        TwoPlayerMove.createMove(new Location(13, 7), -328, PLAYER2_PIECE),
        TwoPlayerMove.createMove(new Location(12, 12), -328, PLAYER2_PIECE),
        TwoPlayerMove.createMove(new Location(12, 7), -328, PLAYER2_PIECE),
        TwoPlayerMove.createMove(new Location(10, 15), -328, PLAYER2_PIECE),
        TwoPlayerMove.createMove(new Location(10, 12), -328, PLAYER2_PIECE),
        TwoPlayerMove.createMove(new Location(9, 15), -328, PLAYER2_PIECE),
        TwoPlayerMove.createMove(new Location(9, 8), -328, PLAYER2_PIECE),
        TwoPlayerMove.createMove(new Location(8, 9), -328, PLAYER2_PIECE),
        TwoPlayerMove.createMove(new Location(8, 8), -328, PLAYER2_PIECE),
        TwoPlayerMove.createMove(new Location(6, 14), -328, PLAYER2_PIECE),
        TwoPlayerMove.createMove(new Location(6, 13), -328, PLAYER2_PIECE),
    };


    static final TwoPlayerMove[] EXPECTED_TOP_MIDDLE_GAME_MOVES_P2 =  {
        TwoPlayerMove.createMove(new Location(11, 10), 304, PLAYER2_PIECE),
        TwoPlayerMove.createMove(new Location(10, 13), -120, PLAYER2_PIECE),
        TwoPlayerMove.createMove(new Location(7, 12), -168, PLAYER2_PIECE),
        TwoPlayerMove.createMove(new Location(6, 12), -168, PLAYER2_PIECE),
        TwoPlayerMove.createMove(new Location(13, 10), -184, PLAYER2_PIECE),
        TwoPlayerMove.createMove(new Location(7, 11), -184, PLAYER2_PIECE),
        TwoPlayerMove.createMove(new Location(10, 14), -192, PLAYER2_PIECE),
        TwoPlayerMove.createMove(new Location(8, 15), -192, PLAYER2_PIECE),
    };

    static final TwoPlayerMove[] EXPECTED_ALL_END_GAME_MOVES_P2 = {
        TwoPlayerMove.createMove(new Location(2, 2), 176, PLAYER2_PIECE),
        TwoPlayerMove.createMove(new Location(3, 3), 48, PLAYER2_PIECE),
        TwoPlayerMove.createMove(new Location(2, 4), 32, PLAYER2_PIECE),
        TwoPlayerMove.createMove(new Location(3, 7), 24, PLAYER2_PIECE),
        TwoPlayerMove.createMove(new Location(2, 6), 8, PLAYER2_PIECE),
        TwoPlayerMove.createMove(new Location(7, 7), -112, PLAYER2_PIECE),
        TwoPlayerMove.createMove(new Location(7, 3), -112, PLAYER2_PIECE),
        TwoPlayerMove.createMove(new Location(5, 8), -112, PLAYER2_PIECE),
        TwoPlayerMove.createMove(new Location(4, 8), -112, PLAYER2_PIECE),
        TwoPlayerMove.createMove(new Location(2, 8), -112, PLAYER2_PIECE),
        TwoPlayerMove.createMove(new Location(8, 7), -128, PLAYER2_PIECE),
        TwoPlayerMove.createMove(new Location(8, 4), -128, PLAYER2_PIECE),
        TwoPlayerMove.createMove(new Location(3, 2), -128, PLAYER2_PIECE),
        TwoPlayerMove.createMove(new Location(1, 8), -128, PLAYER2_PIECE),
        TwoPlayerMove.createMove(new Location(8, 6), -136, PLAYER2_PIECE),
        TwoPlayerMove.createMove(new Location(8, 5), -136, PLAYER2_PIECE),
        TwoPlayerMove.createMove(new Location(8, 3), -136, PLAYER2_PIECE),
        TwoPlayerMove.createMove(new Location(8, 2), -136, PLAYER2_PIECE),
        TwoPlayerMove.createMove(new Location(8, 1), -136, PLAYER2_PIECE),
        TwoPlayerMove.createMove(new Location(7, 8), -136, PLAYER2_PIECE),
        TwoPlayerMove.createMove(new Location(7, 1), -136, PLAYER2_PIECE),
        TwoPlayerMove.createMove(new Location(6, 8), -136, PLAYER2_PIECE),
        TwoPlayerMove.createMove(new Location(6, 1), -136, PLAYER2_PIECE),
        TwoPlayerMove.createMove(new Location(5, 7), -136, PLAYER2_PIECE),
        TwoPlayerMove.createMove(new Location(5, 1), -136, PLAYER2_PIECE),
        TwoPlayerMove.createMove(new Location(4, 1), -136, PLAYER2_PIECE),
        TwoPlayerMove.createMove(new Location(3, 1), -136, PLAYER2_PIECE),
        TwoPlayerMove.createMove(new Location(1, 7), -136, PLAYER2_PIECE),
        TwoPlayerMove.createMove(new Location(1, 6), -136, PLAYER2_PIECE),
        TwoPlayerMove.createMove(new Location(1, 5), -136, PLAYER2_PIECE),
        TwoPlayerMove.createMove(new Location(1, 3), -136, PLAYER2_PIECE),
        TwoPlayerMove.createMove(new Location(1, 2), -136, PLAYER2_PIECE),
    };

    static final TwoPlayerMove[] EXPECTED_TOP_END_GAME_MOVES_P2 = {
        TwoPlayerMove.createMove(new Location(2, 2), 176, PLAYER2_PIECE),
        TwoPlayerMove.createMove(new Location(3, 3), 48, PLAYER2_PIECE),
        TwoPlayerMove.createMove(new Location(2, 4), 32, PLAYER2_PIECE),
        TwoPlayerMove.createMove(new Location(3, 7), 24, PLAYER2_PIECE),
        TwoPlayerMove.createMove(new Location(2, 6), 8, PLAYER2_PIECE),
        TwoPlayerMove.createMove(new Location(7, 7), -112, PLAYER2_PIECE),
        TwoPlayerMove.createMove(new Location(7, 3), -112, PLAYER2_PIECE),
    };

    static final TwoPlayerMove[] EXPECTED_URGENT_MOVES_P1 = {
        TwoPlayerMove.createMove(new Location(4, 3), 8192, PLAYER1_PIECE),
    };

    static final TwoPlayerMove[] EXPECTED_URGENT_MOVES_P2 = {
        TwoPlayerMove.createMove(new Location(4, 7), -8184, PLAYER2_PIECE),
    };

    private ExpectedSearchableResults() {
    }
}