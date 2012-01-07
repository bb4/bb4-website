/** Copyright by Barry G. Becker, 2000-2011. Licensed under MIT License: http://www.opensource.org/licenses/MIT  */
package com.becker.game.twoplayer.pente;

import com.becker.common.geometry.Location;
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
        TwoPlayerMove.createMove(new Location(4, 6), 382, PLAYER1_PIECE),
        TwoPlayerMove.createMove(new Location(7, 3), 350, PLAYER1_PIECE),
        TwoPlayerMove.createMove(new Location(3, 6), 350, PLAYER1_PIECE),
        TwoPlayerMove.createMove(new Location(3, 2), 350, PLAYER1_PIECE),
        TwoPlayerMove.createMove(new Location(6, 3), 318, PLAYER1_PIECE),
        TwoPlayerMove.createMove(new Location(3, 3), 318, PLAYER1_PIECE),
        TwoPlayerMove.createMove(new Location(7, 6), 94, PLAYER1_PIECE),
        TwoPlayerMove.createMove(new Location(2, 4), 94, PLAYER1_PIECE),
        TwoPlayerMove.createMove(new Location(5, 7), 64, PLAYER1_PIECE),
        TwoPlayerMove.createMove(new Location(2, 6), 54, PLAYER1_PIECE),
        TwoPlayerMove.createMove(new Location(2, 2), 54, PLAYER1_PIECE),
        TwoPlayerMove.createMove(new Location(4, 7), 40, PLAYER1_PIECE),
        TwoPlayerMove.createMove(new Location(1, 3), 38, PLAYER1_PIECE),
        TwoPlayerMove.createMove(new Location(6, 6), 32, PLAYER1_PIECE),
        TwoPlayerMove.createMove(new Location(5, 8), 32, PLAYER1_PIECE),
        TwoPlayerMove.createMove(new Location(8, 6), 30, PLAYER1_PIECE),
        TwoPlayerMove.createMove(new Location(8, 5), 30, PLAYER1_PIECE),
        TwoPlayerMove.createMove(new Location(8, 4), 30, PLAYER1_PIECE),
        TwoPlayerMove.createMove(new Location(8, 3), 30, PLAYER1_PIECE),
        TwoPlayerMove.createMove(new Location(7, 8), 30, PLAYER1_PIECE),
        TwoPlayerMove.createMove(new Location(6, 8), 30, PLAYER1_PIECE),
        TwoPlayerMove.createMove(new Location(5, 3), 30, PLAYER1_PIECE),
        TwoPlayerMove.createMove(new Location(4, 3), 30, PLAYER1_PIECE),
        TwoPlayerMove.createMove(new Location(1, 6), 30, PLAYER1_PIECE),
        TwoPlayerMove.createMove(new Location(1, 5), 30, PLAYER1_PIECE),
        TwoPlayerMove.createMove(new Location(1, 4), 30, PLAYER1_PIECE),
        TwoPlayerMove.createMove(new Location(1, 2), 30, PLAYER1_PIECE),
        TwoPlayerMove.createMove(new Location(7, 7), 6, PLAYER1_PIECE),
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
        TwoPlayerMove.createMove(new Location(3, 2), 326, PLAYER1_PIECE),
        TwoPlayerMove.createMove(new Location(3, 6), 292, PLAYER1_PIECE),
        TwoPlayerMove.createMove(new Location(3, 3), 292, PLAYER1_PIECE),
        TwoPlayerMove.createMove(new Location(3, 7), 36, PLAYER1_PIECE),
        TwoPlayerMove.createMove(new Location(1, 6), 36, PLAYER1_PIECE),
        TwoPlayerMove.createMove(new Location(1, 5), 36, PLAYER1_PIECE),
        TwoPlayerMove.createMove(new Location(1, 3), 36, PLAYER1_PIECE),
        TwoPlayerMove.createMove(new Location(1, 2), 36, PLAYER1_PIECE),
        TwoPlayerMove.createMove(new Location(2, 8), 28, PLAYER1_PIECE),
        TwoPlayerMove.createMove(new Location(2, 6), 28, PLAYER1_PIECE),
        TwoPlayerMove.createMove(new Location(2, 4), 28, PLAYER1_PIECE),
        TwoPlayerMove.createMove(new Location(2, 2), 26, PLAYER1_PIECE),
        TwoPlayerMove.createMove(new Location(8, 2), 6, PLAYER1_PIECE),
        TwoPlayerMove.createMove(new Location(7, 7), 6, PLAYER1_PIECE),
        TwoPlayerMove.createMove(new Location(7, 3), 6, PLAYER1_PIECE),
        TwoPlayerMove.createMove(new Location(7, 1), 6, PLAYER1_PIECE),
        TwoPlayerMove.createMove(new Location(5, 8), 6, PLAYER1_PIECE),
        TwoPlayerMove.createMove(new Location(5, 7), 6, PLAYER1_PIECE),
        TwoPlayerMove.createMove(new Location(5, 1), 6, PLAYER1_PIECE),
        TwoPlayerMove.createMove(new Location(8, 7), 4, PLAYER1_PIECE),
        TwoPlayerMove.createMove(new Location(8, 5), 4, PLAYER1_PIECE),
        TwoPlayerMove.createMove(new Location(8, 4), 4, PLAYER1_PIECE),
        TwoPlayerMove.createMove(new Location(8, 3), 4, PLAYER1_PIECE),
        TwoPlayerMove.createMove(new Location(8, 1), 4, PLAYER1_PIECE),
        TwoPlayerMove.createMove(new Location(7, 8), 4, PLAYER1_PIECE),
        TwoPlayerMove.createMove(new Location(6, 8), 4, PLAYER1_PIECE),
        TwoPlayerMove.createMove(new Location(6, 1), 4, PLAYER1_PIECE),
        TwoPlayerMove.createMove(new Location(4, 8), 4, PLAYER1_PIECE),
        TwoPlayerMove.createMove(new Location(4, 1), 4, PLAYER1_PIECE),
        TwoPlayerMove.createMove(new Location(3, 1), 4, PLAYER1_PIECE),
        TwoPlayerMove.createMove(new Location(1, 8), 4, PLAYER1_PIECE),
        TwoPlayerMove.createMove(new Location(1, 7), 4, PLAYER1_PIECE),
        TwoPlayerMove.createMove(new Location(8, 6), 2, PLAYER1_PIECE),
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
        TwoPlayerMove.createMove(new Location(11, 10), -3148, PLAYER2_PIECE),
        TwoPlayerMove.createMove(new Location(10, 13), 284, PLAYER2_PIECE),
        TwoPlayerMove.createMove(new Location(6, 12), 348, PLAYER2_PIECE),
        TwoPlayerMove.createMove(new Location(7, 12), 348, PLAYER2_PIECE),
        TwoPlayerMove.createMove(new Location(13, 10), 352, PLAYER2_PIECE),
        TwoPlayerMove.createMove(new Location(7, 11), 372, PLAYER2_PIECE),
        TwoPlayerMove.createMove(new Location(8, 15), 380, PLAYER2_PIECE),
        TwoPlayerMove.createMove(new Location(10, 14), 380, PLAYER2_PIECE),
        TwoPlayerMove.createMove(new Location(8, 11), 572, PLAYER2_PIECE),
        TwoPlayerMove.createMove(new Location(11, 13), 572, PLAYER2_PIECE),
        TwoPlayerMove.createMove(new Location(8, 10), 576, PLAYER2_PIECE),
        TwoPlayerMove.createMove(new Location(8, 14), 604, PLAYER2_PIECE),
        TwoPlayerMove.createMove(new Location(11, 8), 604, PLAYER2_PIECE),
        TwoPlayerMove.createMove(new Location(14, 10), 604, PLAYER2_PIECE),
        TwoPlayerMove.createMove(new Location(8, 13), 636, PLAYER2_PIECE),
        TwoPlayerMove.createMove(new Location(10, 9), 636, PLAYER2_PIECE),
        TwoPlayerMove.createMove(new Location(11, 7), 636, PLAYER2_PIECE),
        TwoPlayerMove.createMove(new Location(12, 13), 636, PLAYER2_PIECE),
        TwoPlayerMove.createMove(new Location(13, 12), 636, PLAYER2_PIECE),
        TwoPlayerMove.createMove(new Location(14, 12), 636, PLAYER2_PIECE),
        TwoPlayerMove.createMove(new Location(15, 10), 636, PLAYER2_PIECE),
        TwoPlayerMove.createMove(new Location(15, 12), 636, PLAYER2_PIECE),
        TwoPlayerMove.createMove(new Location(10, 8), 658, PLAYER2_PIECE),
        TwoPlayerMove.createMove(new Location(7, 14), 660, PLAYER2_PIECE),
        TwoPlayerMove.createMove(new Location(13, 8), 660, PLAYER2_PIECE),
        TwoPlayerMove.createMove(new Location(10, 12), 666, PLAYER2_PIECE),
        TwoPlayerMove.createMove(new Location(6, 13), 668, PLAYER2_PIECE),
        TwoPlayerMove.createMove(new Location(6, 14), 668, PLAYER2_PIECE),
        TwoPlayerMove.createMove(new Location(8, 8), 668, PLAYER2_PIECE),
        TwoPlayerMove.createMove(new Location(8, 9), 668, PLAYER2_PIECE),
        TwoPlayerMove.createMove(new Location(9, 8), 668, PLAYER2_PIECE),
        TwoPlayerMove.createMove(new Location(9, 15), 668, PLAYER2_PIECE),
        TwoPlayerMove.createMove(new Location(10, 15), 668, PLAYER2_PIECE),
        TwoPlayerMove.createMove(new Location(12, 7), 668, PLAYER2_PIECE),
        TwoPlayerMove.createMove(new Location(12, 12), 668, PLAYER2_PIECE),
        TwoPlayerMove.createMove(new Location(13, 7), 668, PLAYER2_PIECE),
        TwoPlayerMove.createMove(new Location(13, 9), 668, PLAYER2_PIECE),
        TwoPlayerMove.createMove(new Location(15, 11), 668, PLAYER2_PIECE),
    };


    static final TwoPlayerMove[] EXPECTED_TOP_MIDDLE_GAME_MOVES_P2 =  {
        TwoPlayerMove.createMove(new Location(11, 10), -3148, PLAYER2_PIECE),
        TwoPlayerMove.createMove(new Location(10, 13), 284, PLAYER2_PIECE),
        TwoPlayerMove.createMove(new Location(6, 12), 348, PLAYER2_PIECE),
        TwoPlayerMove.createMove(new Location(7, 12), 348, PLAYER2_PIECE),
        TwoPlayerMove.createMove(new Location(13, 10), 352, PLAYER2_PIECE),
        TwoPlayerMove.createMove(new Location(7, 11), 372, PLAYER2_PIECE),
        TwoPlayerMove.createMove(new Location(8, 15), 380, PLAYER2_PIECE),
        TwoPlayerMove.createMove(new Location(10, 14), 380, PLAYER2_PIECE),
    };

    static final TwoPlayerMove[] EXPECTED_ALL_END_GAME_MOVES_P2 = {
        TwoPlayerMove.createMove(new Location(2, 2), -356, PLAYER2_PIECE),
        TwoPlayerMove.createMove(new Location(3, 3), -62, PLAYER2_PIECE),
        TwoPlayerMove.createMove(new Location(2, 4), -60, PLAYER2_PIECE),
        TwoPlayerMove.createMove(new Location(3, 7), -30, PLAYER2_PIECE),
        TwoPlayerMove.createMove(new Location(2, 6), -28, PLAYER2_PIECE),
        TwoPlayerMove.createMove(new Location(2, 8), 256, PLAYER2_PIECE),
        TwoPlayerMove.createMove(new Location(4, 8), 256, PLAYER2_PIECE),
        TwoPlayerMove.createMove(new Location(5, 8), 256, PLAYER2_PIECE),
        TwoPlayerMove.createMove(new Location(7, 3), 256, PLAYER2_PIECE),
        TwoPlayerMove.createMove(new Location(7, 7), 256, PLAYER2_PIECE),
        TwoPlayerMove.createMove(new Location(3, 2), 262, PLAYER2_PIECE),
        TwoPlayerMove.createMove(new Location(1, 8), 280, PLAYER2_PIECE),
        TwoPlayerMove.createMove(new Location(8, 4), 280, PLAYER2_PIECE),
        TwoPlayerMove.createMove(new Location(8, 7), 280, PLAYER2_PIECE),
        TwoPlayerMove.createMove(new Location(8, 6), 286, PLAYER2_PIECE),
        TwoPlayerMove.createMove(new Location(1, 2), 288, PLAYER2_PIECE),
        TwoPlayerMove.createMove(new Location(1, 3), 288, PLAYER2_PIECE),
        TwoPlayerMove.createMove(new Location(1, 5), 288, PLAYER2_PIECE),
        TwoPlayerMove.createMove(new Location(1, 6), 288, PLAYER2_PIECE),
        TwoPlayerMove.createMove(new Location(1, 7), 288, PLAYER2_PIECE),
        TwoPlayerMove.createMove(new Location(3, 1), 288, PLAYER2_PIECE),
        TwoPlayerMove.createMove(new Location(4, 1), 288, PLAYER2_PIECE),
        TwoPlayerMove.createMove(new Location(5, 7), 288, PLAYER2_PIECE),
        TwoPlayerMove.createMove(new Location(6, 1), 288, PLAYER2_PIECE),
        TwoPlayerMove.createMove(new Location(6, 8), 288, PLAYER2_PIECE),
        TwoPlayerMove.createMove(new Location(7, 8), 288, PLAYER2_PIECE),
        TwoPlayerMove.createMove(new Location(8, 1), 288, PLAYER2_PIECE),
        TwoPlayerMove.createMove(new Location(8, 2), 288, PLAYER2_PIECE),
        TwoPlayerMove.createMove(new Location(8, 3), 288, PLAYER2_PIECE),
        TwoPlayerMove.createMove(new Location(8, 5), 288, PLAYER2_PIECE),
        TwoPlayerMove.createMove(new Location(5, 1), 290, PLAYER2_PIECE),
        TwoPlayerMove.createMove(new Location(7, 1), 290, PLAYER2_PIECE),

    };


    static final TwoPlayerMove[] EXPECTED_TOP_END_GAME_MOVES_P2 = {
        /*
        TwoPlayerMove.createMove(new Location(4, 7), 8164, PLAYER2_PIECE),
        TwoPlayerMove.createMove(new Location(3, 3), 4128, PLAYER2_PIECE),
        TwoPlayerMove.createMove(new Location(7, 3), 4096, PLAYER2_PIECE),
        */

        TwoPlayerMove.createMove(new Location(2, 2), -176, PLAYER2_PIECE),
        TwoPlayerMove.createMove(new Location(3, 3), -48, PLAYER2_PIECE),
        TwoPlayerMove.createMove(new Location(2, 4), -32, PLAYER2_PIECE),
        TwoPlayerMove.createMove(new Location(3, 7), -24, PLAYER2_PIECE),
        TwoPlayerMove.createMove(new Location(2, 6), -8, PLAYER2_PIECE),
        TwoPlayerMove.createMove(new Location(2, 8), 112, PLAYER2_PIECE),
        //TwoPlayerMove.createMove(new Location(4, 8), 112, PLAYER2_PIECE),
    };

    static final TwoPlayerMove[] EXPECTED_URGENT_MOVES_P1 = {
        TwoPlayerMove.createMove(new Location(4, 3), 8224, PLAYER1_PIECE),
        //TwoPlayerMove.createMove(new Location(4, 7), 7844, PLAYER1_PIECE),
    };

    static final TwoPlayerMove[] EXPECTED_URGENT_MOVES_P2 = {
        TwoPlayerMove.createMove(new Location(4, 7), 8164, PLAYER2_PIECE),
        TwoPlayerMove.createMove(new Location(3, 3), 4128, PLAYER2_PIECE),
        TwoPlayerMove.createMove(new Location(7, 3), 4096, PLAYER2_PIECE),

        //TwoPlayerMove.createMove(new Location(4, 7), -8184, PLAYER2_PIECE),
    };

    private ExpectedSearchableResults() {
    }
}