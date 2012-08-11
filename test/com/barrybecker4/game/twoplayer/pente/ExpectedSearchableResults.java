/** Copyright by Barry G. Becker, 2000-2011. Licensed under MIT License: http://www.opensource.org/licenses/MIT  */
package com.barrybecker4.game.twoplayer.pente;

import com.barrybecker4.common.geometry.Location;
import com.barrybecker4.game.common.board.GamePiece;
import com.barrybecker4.game.twoplayer.common.TwoPlayerMove;

/**
 * Expected generated moves for search tests.
 * @author Barry Becker
 */
public class ExpectedSearchableResults {

    private static final GamePiece PLAYER1_PIECE = new GamePiece(true);
    private static final GamePiece PLAYER2_PIECE = new GamePiece(false);

    static final TwoPlayerMove[] EXPECTED_ALL_MIDDLE_GAME_MOVES_P1 = {
        TwoPlayerMove.createMove(new Location(4, 6), 48, PLAYER1_PIECE),
        TwoPlayerMove.createMove(new Location(7, 3), 40, PLAYER1_PIECE),
        TwoPlayerMove.createMove(new Location(3, 6), 40, PLAYER1_PIECE),
        TwoPlayerMove.createMove(new Location(3, 2), 40, PLAYER1_PIECE),
        TwoPlayerMove.createMove(new Location(6, 3), 32, PLAYER1_PIECE),
        TwoPlayerMove.createMove(new Location(3, 3), 32, PLAYER1_PIECE),
        TwoPlayerMove.createMove(new Location(7, 6), 24, PLAYER1_PIECE),
        TwoPlayerMove.createMove(new Location(2, 4), 24, PLAYER1_PIECE),
        TwoPlayerMove.createMove(new Location(5, 7), 16, PLAYER1_PIECE),
        TwoPlayerMove.createMove(new Location(2, 6), 14, PLAYER1_PIECE),
        TwoPlayerMove.createMove(new Location(2, 2), 14, PLAYER1_PIECE),
        TwoPlayerMove.createMove(new Location(4, 7), 10, PLAYER1_PIECE),
        TwoPlayerMove.createMove(new Location(1, 3), 10, PLAYER1_PIECE),
        TwoPlayerMove.createMove(new Location(8, 6), 8, PLAYER1_PIECE),
        TwoPlayerMove.createMove(new Location(8, 5), 8, PLAYER1_PIECE),
        TwoPlayerMove.createMove(new Location(8, 4), 8, PLAYER1_PIECE),
        TwoPlayerMove.createMove(new Location(8, 3), 8, PLAYER1_PIECE),
        TwoPlayerMove.createMove(new Location(7, 8), 8, PLAYER1_PIECE),
        TwoPlayerMove.createMove(new Location(6, 8), 8, PLAYER1_PIECE),
        TwoPlayerMove.createMove(new Location(6, 6), 8, PLAYER1_PIECE),
        TwoPlayerMove.createMove(new Location(5, 8), 8, PLAYER1_PIECE),
        TwoPlayerMove.createMove(new Location(5, 3), 8, PLAYER1_PIECE),
        TwoPlayerMove.createMove(new Location(4, 3), 8, PLAYER1_PIECE),
        TwoPlayerMove.createMove(new Location(1, 6), 8, PLAYER1_PIECE),
        TwoPlayerMove.createMove(new Location(1, 5), 8, PLAYER1_PIECE),
        TwoPlayerMove.createMove(new Location(1, 4), 8, PLAYER1_PIECE),
        TwoPlayerMove.createMove(new Location(1, 2), 8, PLAYER1_PIECE),
        TwoPlayerMove.createMove(new Location(7, 7), 2, PLAYER1_PIECE),
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
        TwoPlayerMove.createMove(new Location(3, 2), 34, PLAYER1_PIECE),
        TwoPlayerMove.createMove(new Location(3, 6), 26, PLAYER1_PIECE),
        TwoPlayerMove.createMove(new Location(3, 3), 26, PLAYER1_PIECE),
        TwoPlayerMove.createMove(new Location(3, 7), 10, PLAYER1_PIECE),
        TwoPlayerMove.createMove(new Location(2, 8), 10, PLAYER1_PIECE),
        TwoPlayerMove.createMove(new Location(1, 6), 10, PLAYER1_PIECE),
        TwoPlayerMove.createMove(new Location(1, 5), 10, PLAYER1_PIECE),
        TwoPlayerMove.createMove(new Location(1, 3), 10, PLAYER1_PIECE),
        TwoPlayerMove.createMove(new Location(1, 2), 10, PLAYER1_PIECE),
        TwoPlayerMove.createMove(new Location(2, 6), 8, PLAYER1_PIECE),
        TwoPlayerMove.createMove(new Location(2, 4), 8, PLAYER1_PIECE),
        TwoPlayerMove.createMove(new Location(2, 2), 8, PLAYER1_PIECE),
        TwoPlayerMove.createMove(new Location(8, 7), 2, PLAYER1_PIECE),
        TwoPlayerMove.createMove(new Location(8, 6), 2, PLAYER1_PIECE),
        TwoPlayerMove.createMove(new Location(8, 5), 2, PLAYER1_PIECE),
        TwoPlayerMove.createMove(new Location(8, 4), 2, PLAYER1_PIECE),
        TwoPlayerMove.createMove(new Location(8, 3), 2, PLAYER1_PIECE),
        TwoPlayerMove.createMove(new Location(8, 2), 2, PLAYER1_PIECE),
        TwoPlayerMove.createMove(new Location(8, 1), 2, PLAYER1_PIECE),
        TwoPlayerMove.createMove(new Location(7, 8), 2, PLAYER1_PIECE),
        TwoPlayerMove.createMove(new Location(7, 7), 2, PLAYER1_PIECE),
        TwoPlayerMove.createMove(new Location(7, 3), 2, PLAYER1_PIECE),
        TwoPlayerMove.createMove(new Location(7, 1), 2, PLAYER1_PIECE),
        TwoPlayerMove.createMove(new Location(6, 8), 2, PLAYER1_PIECE),
        TwoPlayerMove.createMove(new Location(6, 1), 2, PLAYER1_PIECE),
        TwoPlayerMove.createMove(new Location(5, 8), 2, PLAYER1_PIECE),
        TwoPlayerMove.createMove(new Location(5, 7), 2, PLAYER1_PIECE),
        TwoPlayerMove.createMove(new Location(5, 1), 2, PLAYER1_PIECE),
        TwoPlayerMove.createMove(new Location(4, 8), 2, PLAYER1_PIECE),
        TwoPlayerMove.createMove(new Location(4, 1), 2, PLAYER1_PIECE),
        TwoPlayerMove.createMove(new Location(3, 1), 2, PLAYER1_PIECE),
        TwoPlayerMove.createMove(new Location(1, 8), 2, PLAYER1_PIECE),
        TwoPlayerMove.createMove(new Location(1, 7), 2, PLAYER1_PIECE),
    };

    static final TwoPlayerMove[] EXPECTED_TOP_END_GAME_MOVES_P1 = {

        TwoPlayerMove.createMove(new Location(3, 2), 326, PLAYER1_PIECE),
        TwoPlayerMove.createMove(new Location(3, 6), 292, PLAYER1_PIECE),
        TwoPlayerMove.createMove(new Location(3, 3), 292, PLAYER1_PIECE),
        TwoPlayerMove.createMove(new Location(3, 7), 36, PLAYER1_PIECE),
        TwoPlayerMove.createMove(new Location(2, 8), 36, PLAYER1_PIECE),
        TwoPlayerMove.createMove(new Location(1, 6), 36, PLAYER1_PIECE),
        TwoPlayerMove.createMove(new Location(1, 5), 36, PLAYER1_PIECE),
    };


    static final TwoPlayerMove[] EXPECTED_ALL_MIDDLE_GAME_MOVES_P2 = {
        TwoPlayerMove.createMove(new Location(11, 10), 26, PLAYER2_PIECE),
        TwoPlayerMove.createMove(new Location(13, 10), 40, PLAYER2_PIECE),
        TwoPlayerMove.createMove(new Location(8, 10), 48, PLAYER2_PIECE),
        TwoPlayerMove.createMove(new Location(10, 13), 304, PLAYER2_PIECE),
        TwoPlayerMove.createMove(new Location(6, 12), 320, PLAYER2_PIECE),
        TwoPlayerMove.createMove(new Location(7, 12), 320, PLAYER2_PIECE),
        TwoPlayerMove.createMove(new Location(7, 11), 326, PLAYER2_PIECE),
        TwoPlayerMove.createMove(new Location(8, 11), 328, PLAYER2_PIECE),
        TwoPlayerMove.createMove(new Location(8, 15), 328, PLAYER2_PIECE),
        TwoPlayerMove.createMove(new Location(10, 14), 328, PLAYER2_PIECE),
        TwoPlayerMove.createMove(new Location(11, 13), 328, PLAYER2_PIECE),
        TwoPlayerMove.createMove(new Location(8, 14), 336, PLAYER2_PIECE),
        TwoPlayerMove.createMove(new Location(11, 8), 336, PLAYER2_PIECE),
        TwoPlayerMove.createMove(new Location(14, 10), 336, PLAYER2_PIECE),
        TwoPlayerMove.createMove(new Location(8, 13), 344, PLAYER2_PIECE),
        TwoPlayerMove.createMove(new Location(10, 9), 344, PLAYER2_PIECE),
        TwoPlayerMove.createMove(new Location(11, 7), 344, PLAYER2_PIECE),
        TwoPlayerMove.createMove(new Location(12, 13), 344, PLAYER2_PIECE),
        TwoPlayerMove.createMove(new Location(13, 12), 344, PLAYER2_PIECE),
        TwoPlayerMove.createMove(new Location(14, 12), 344, PLAYER2_PIECE),
        TwoPlayerMove.createMove(new Location(15, 10), 344, PLAYER2_PIECE),
        TwoPlayerMove.createMove(new Location(15, 12), 344, PLAYER2_PIECE),
        TwoPlayerMove.createMove(new Location(7, 14), 350, PLAYER2_PIECE),
        TwoPlayerMove.createMove(new Location(10, 8), 350, PLAYER2_PIECE),
        TwoPlayerMove.createMove(new Location(13, 8), 350, PLAYER2_PIECE),
        TwoPlayerMove.createMove(new Location(6, 13), 352, PLAYER2_PIECE),
        TwoPlayerMove.createMove(new Location(6, 14), 352, PLAYER2_PIECE),
        TwoPlayerMove.createMove(new Location(8, 8), 352, PLAYER2_PIECE),
        TwoPlayerMove.createMove(new Location(8, 9), 352, PLAYER2_PIECE),
        TwoPlayerMove.createMove(new Location(9, 8), 352, PLAYER2_PIECE),
        TwoPlayerMove.createMove(new Location(9, 15), 352, PLAYER2_PIECE),
        TwoPlayerMove.createMove(new Location(10, 12), 352, PLAYER2_PIECE),
        TwoPlayerMove.createMove(new Location(10, 15), 352, PLAYER2_PIECE),
        TwoPlayerMove.createMove(new Location(12, 7), 352, PLAYER2_PIECE),
        TwoPlayerMove.createMove(new Location(12, 12), 352, PLAYER2_PIECE),
        TwoPlayerMove.createMove(new Location(13, 7), 352, PLAYER2_PIECE),
        TwoPlayerMove.createMove(new Location(13, 9), 352, PLAYER2_PIECE),
        TwoPlayerMove.createMove(new Location(15, 11), 352, PLAYER2_PIECE),
    };

    static final TwoPlayerMove[] EXPECTED_TOP_MIDDLE_GAME_MOVES_P2 =  {
        TwoPlayerMove.createMove(new Location(11, 10), 26, PLAYER2_PIECE),
        TwoPlayerMove.createMove(new Location(13, 10), 40, PLAYER2_PIECE),
        TwoPlayerMove.createMove(new Location(8, 10), 48, PLAYER2_PIECE),
        TwoPlayerMove.createMove(new Location(10, 13), 304, PLAYER2_PIECE),
        TwoPlayerMove.createMove(new Location(6, 12), 320, PLAYER2_PIECE),
        TwoPlayerMove.createMove(new Location(7, 12), 320, PLAYER2_PIECE),
        TwoPlayerMove.createMove(new Location(7, 11), 326, PLAYER2_PIECE),
        TwoPlayerMove.createMove(new Location(8, 11), 328, PLAYER2_PIECE),
    };

    static final TwoPlayerMove[] EXPECTED_ALL_END_GAME_MOVES_P2 = {

        TwoPlayerMove.createMove(new Location(2, 2), -322, PLAYER2_PIECE),
        TwoPlayerMove.createMove(new Location(2, 4), -296, PLAYER2_PIECE),
        TwoPlayerMove.createMove(new Location(2, 6), -288, PLAYER2_PIECE),
        TwoPlayerMove.createMove(new Location(3, 2), -264, PLAYER2_PIECE),
        TwoPlayerMove.createMove(new Location(3, 3), -16, PLAYER2_PIECE),
        TwoPlayerMove.createMove(new Location(3, 7), -8, PLAYER2_PIECE),
        TwoPlayerMove.createMove(new Location(2, 8), 16, PLAYER2_PIECE),
        TwoPlayerMove.createMove(new Location(4, 8), 16, PLAYER2_PIECE),
        TwoPlayerMove.createMove(new Location(5, 8), 16, PLAYER2_PIECE),
        TwoPlayerMove.createMove(new Location(7, 3), 16, PLAYER2_PIECE),
        TwoPlayerMove.createMove(new Location(7, 7), 16, PLAYER2_PIECE),
        TwoPlayerMove.createMove(new Location(1, 8), 22, PLAYER2_PIECE),
        TwoPlayerMove.createMove(new Location(8, 4), 22, PLAYER2_PIECE),
        TwoPlayerMove.createMove(new Location(8, 7), 22, PLAYER2_PIECE),
        TwoPlayerMove.createMove(new Location(1, 2), 24, PLAYER2_PIECE),
        TwoPlayerMove.createMove(new Location(1, 3), 24, PLAYER2_PIECE),
        TwoPlayerMove.createMove(new Location(1, 5), 24, PLAYER2_PIECE),
        TwoPlayerMove.createMove(new Location(1, 6), 24, PLAYER2_PIECE),
        TwoPlayerMove.createMove(new Location(1, 7), 24, PLAYER2_PIECE),
        TwoPlayerMove.createMove(new Location(3, 1), 24, PLAYER2_PIECE),
        TwoPlayerMove.createMove(new Location(4, 1), 24, PLAYER2_PIECE),
        TwoPlayerMove.createMove(new Location(5, 1), 24, PLAYER2_PIECE),
        TwoPlayerMove.createMove(new Location(5, 7), 24, PLAYER2_PIECE),
        TwoPlayerMove.createMove(new Location(6, 1), 24, PLAYER2_PIECE),
        TwoPlayerMove.createMove(new Location(6, 8), 24, PLAYER2_PIECE),
        TwoPlayerMove.createMove(new Location(7, 1), 24, PLAYER2_PIECE),
        TwoPlayerMove.createMove(new Location(7, 8), 24, PLAYER2_PIECE),
        TwoPlayerMove.createMove(new Location(8, 1), 24, PLAYER2_PIECE),
        TwoPlayerMove.createMove(new Location(8, 2), 24, PLAYER2_PIECE),
        TwoPlayerMove.createMove(new Location(8, 3), 24, PLAYER2_PIECE),
        TwoPlayerMove.createMove(new Location(8, 5), 24, PLAYER2_PIECE),
        TwoPlayerMove.createMove(new Location(8, 6), 24, PLAYER2_PIECE),
    };


    static final TwoPlayerMove[] EXPECTED_TOP_END_GAME_MOVES_P2 = {
        TwoPlayerMove.createMove(new Location(2, 2), -322, PLAYER2_PIECE),
        TwoPlayerMove.createMove(new Location(2, 4), -296, PLAYER2_PIECE),
        TwoPlayerMove.createMove(new Location(2, 6), -288, PLAYER2_PIECE),
        TwoPlayerMove.createMove(new Location(3, 2), -264, PLAYER2_PIECE),
        TwoPlayerMove.createMove(new Location(3, 3), -16, PLAYER2_PIECE),
        TwoPlayerMove.createMove(new Location(3, 7), -8, PLAYER2_PIECE),
    };

    static final TwoPlayerMove[] EXPECTED_URGENT_MOVES_P1 = {
        TwoPlayerMove.createMove(new Location(4, 3), 8224, PLAYER1_PIECE),
        //TwoPlayerMove.createMove(new Location(4, 7), 7844, PLAYER1_PIECE),
    };

    static final TwoPlayerMove[] EXPECTED_URGENT_MOVES_P2 = {
        TwoPlayerMove.createMove(new Location(4, 7), 8164, PLAYER2_PIECE),
        //TwoPlayerMove.createMove(new Location(3, 3), 4128, PLAYER2_PIECE),
        //TwoPlayerMove.createMove(new Location(7, 3), 4096, PLAYER2_PIECE),

        //TwoPlayerMove.createMove(new Location(4, 7), -8184, PLAYER2_PIECE),
    };

    private ExpectedSearchableResults() {
    }
}