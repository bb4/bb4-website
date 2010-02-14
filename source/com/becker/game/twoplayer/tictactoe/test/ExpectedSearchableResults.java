package com.becker.game.twoplayer.tictactoe.test;

import com.becker.common.Location;
import com.becker.game.common.GamePiece;
import com.becker.game.twoplayer.blockade.BlockadeBoardPosition;
import com.becker.game.twoplayer.blockade.BlockadeMove;
import com.becker.game.twoplayer.blockade.BlockadeWall;
import com.becker.game.twoplayer.common.TwoPlayerMove;

/**
 * Expected generated moves for search tests.
 * @author Barry Becker
 */
public class ExpectedSearchableResults {
    
    private static final GamePiece PLAYER1_PIECE = new GamePiece(true);
    private static final GamePiece PLAYER2_PIECE = new GamePiece(false);

    static final TwoPlayerMove[] EXPECTED_ALL_MIDDLE_GAME_MOVES_CENTER_P1 = {
        TwoPlayerMove.createMove(new Location(3, 2), -4, new GamePiece(false)),
        TwoPlayerMove.createMove(new Location(2, 3), -4, new GamePiece(false)),
        TwoPlayerMove.createMove(new Location(2, 1), -4, new GamePiece(false)),
        TwoPlayerMove.createMove(new Location(1, 2), -4, new GamePiece(false)),
        TwoPlayerMove.createMove(new Location(3, 3), -8, new GamePiece(false)),
        TwoPlayerMove.createMove(new Location(3, 1), -8, new GamePiece(false)),
        TwoPlayerMove.createMove(new Location(1, 3), -8, new GamePiece(false)),
        TwoPlayerMove.createMove(new Location(1, 1), -8, new GamePiece(false)),
    };

    static final TwoPlayerMove[] EXPECTED_ALL_MIDDLE_GAME_MOVES_CORNER_P1 = {
        TwoPlayerMove.createMove(new Location(2, 2), 8, new GamePiece(false)),
        TwoPlayerMove.createMove(new Location(2, 1), 8, new GamePiece(false)),
        TwoPlayerMove.createMove(new Location(1, 2), 8, new GamePiece(false)),
        TwoPlayerMove.createMove(new Location(3, 2), 4, new GamePiece(false)),
        TwoPlayerMove.createMove(new Location(2, 3), 4, new GamePiece(false)),
        TwoPlayerMove.createMove(new Location(3, 3), -4, new GamePiece(false)),
        TwoPlayerMove.createMove(new Location(3, 1), -4, new GamePiece(false)),
        TwoPlayerMove.createMove(new Location(1, 3), -4, new GamePiece(false)),
    };

    static final TwoPlayerMove[] EXPECTED_ALL_MIDDLE_GAME_MOVES_EDGE_P1 = {
        TwoPlayerMove.createMove(new Location(2, 2), 4, new GamePiece(false)),
        TwoPlayerMove.createMove(new Location(3, 2), 0, new GamePiece(false)),
        TwoPlayerMove.createMove(new Location(1, 2), 0, new GamePiece(false)),
        TwoPlayerMove.createMove(new Location(3, 3), -4, new GamePiece(false)),
        TwoPlayerMove.createMove(new Location(2, 3), -4, new GamePiece(false)),
        TwoPlayerMove.createMove(new Location(1, 3), -4, new GamePiece(false)),
        TwoPlayerMove.createMove(new Location(3, 1), -8, new GamePiece(false)),
        TwoPlayerMove.createMove(new Location(1, 1), -8, new GamePiece(false)),
    };

    static final TwoPlayerMove[] EXPECTED_TOP_MIDDLE_GAME_MOVES_CENTER_P1 =  {
        TwoPlayerMove.createMove(new Location(3, 2), -4, new GamePiece(false)),
        TwoPlayerMove.createMove(new Location(2, 3), -4, new GamePiece(false)),
    };

    static final TwoPlayerMove[] EXPECTED_TOP_MIDDLE_GAME_MOVES_CORNER_P1 =  {
        TwoPlayerMove.createMove(new Location(2, 2), 8, new GamePiece(false)),
        TwoPlayerMove.createMove(new Location(2, 1), 8, new GamePiece(false)),
    };

    static final TwoPlayerMove[] EXPECTED_TOP_MIDDLE_GAME_MOVES_EDGE_P1 =  {
        TwoPlayerMove.createMove(new Location(2, 2), 4, new GamePiece(false)),
        TwoPlayerMove.createMove(new Location(3, 2), 0, new GamePiece(false)),
    };

    static final TwoPlayerMove[] EXPECTED_ALL_END_GAME_MOVES_P1 = {
        TwoPlayerMove.createMove(new Location(2, 3), 0, new GamePiece(false)),
        TwoPlayerMove.createMove(new Location(2, 1), 0, new GamePiece(false)),
    };

    static final TwoPlayerMove[] EXPECTED_TOP_END_GAME_MOVES_P1 = {
        TwoPlayerMove.createMove(new Location(2, 3), 0, new GamePiece(false)),
        TwoPlayerMove.createMove(new Location(2, 1), 0, new GamePiece(false)),
    };

    static final TwoPlayerMove[] EXPECTED_ALL_MIDDLE_GAME_MOVES_CENTER_P2 = {
        TwoPlayerMove.createMove(new Location(2, 1), 48, new GamePiece(true)),
        TwoPlayerMove.createMove(new Location(1, 2), 48, new GamePiece(true)),
        TwoPlayerMove.createMove(new Location(3, 2), 44, new GamePiece(true)),
        TwoPlayerMove.createMove(new Location(2, 3), 44, new GamePiece(true)),
        TwoPlayerMove.createMove(new Location(3, 1), 36, new GamePiece(true)),
        TwoPlayerMove.createMove(new Location(1, 3), 36, new GamePiece(true)),
        TwoPlayerMove.createMove(new Location(3, 3), -4, new GamePiece(true)),
    };

    static final TwoPlayerMove[] EXPECTED_ALL_MIDDLE_GAME_MOVES_CORNER_P2 = {
        TwoPlayerMove.createMove(new Location(2, 1), 28, new GamePiece(true)),
        TwoPlayerMove.createMove(new Location(1, 2), 28, new GamePiece(true)),
        TwoPlayerMove.createMove(new Location(3, 1), 24, new GamePiece(true)),
        TwoPlayerMove.createMove(new Location(1, 3), 24, new GamePiece(true)),
        TwoPlayerMove.createMove(new Location(3, 3), -8, new GamePiece(true)),
        TwoPlayerMove.createMove(new Location(3, 2), -8, new GamePiece(true)),
        TwoPlayerMove.createMove(new Location(2, 3), -8, new GamePiece(true)),
    };

    static final TwoPlayerMove[] EXPECTED_ALL_MIDDLE_GAME_MOVES_EDGE_P2 = {
        TwoPlayerMove.createMove(new Location(3, 1), 24, new GamePiece(true)),
        TwoPlayerMove.createMove(new Location(1, 1), 24, new GamePiece(true)),
        TwoPlayerMove.createMove(new Location(2, 3), -4, new GamePiece(true)),
        TwoPlayerMove.createMove(new Location(3, 3), -12, new GamePiece(true)),
        TwoPlayerMove.createMove(new Location(1, 3), -12, new GamePiece(true)),
        TwoPlayerMove.createMove(new Location(3, 2), -16, new GamePiece(true)),
        TwoPlayerMove.createMove(new Location(1, 2), -16, new GamePiece(true)),
    };

    static final TwoPlayerMove[] EXPECTED_TOP_MIDDLE_GAME_MOVES_CENTER_P2 =  {
        TwoPlayerMove.createMove(new Location(2, 1), 48, new GamePiece(true)),
        TwoPlayerMove.createMove(new Location(1, 2), 48, new GamePiece(true)),
    };

    static final TwoPlayerMove[] EXPECTED_TOP_MIDDLE_GAME_MOVES_CORNER_P2 =  {
        TwoPlayerMove.createMove(new Location(2, 1), 28, new GamePiece(true)),
        TwoPlayerMove.createMove(new Location(1, 2), 28, new GamePiece(true)),
    };

    static final TwoPlayerMove[] EXPECTED_TOP_MIDDLE_GAME_MOVES_EDGE_P2 =  {
        TwoPlayerMove.createMove(new Location(3, 1), 24, new GamePiece(true)),
       TwoPlayerMove.createMove(new Location(1, 1), 24, new GamePiece(true)),
    };
   
    static final TwoPlayerMove[] EXPECTED_ALL_END_GAME_MOVES_P2 = {
        TwoPlayerMove.createMove(new Location(3, 1), -12, new GamePiece(true)),
        TwoPlayerMove.createMove(new Location(1, 3), -12, new GamePiece(true)),
        TwoPlayerMove.createMove(new Location(3, 3), -52, new GamePiece(true))
    };

    static final TwoPlayerMove[] EXPECTED_TOP_END_GAME_MOVES_P2 = {
        TwoPlayerMove.createMove(new Location(3, 1), -12, new GamePiece(true)),
        TwoPlayerMove.createMove(new Location(1, 3), -12, new GamePiece(true)),
        TwoPlayerMove.createMove(new Location(3, 3), -52, new GamePiece(true)),
    };

    static final TwoPlayerMove[] EXPECTED_URGENT_MOVES = {
        TwoPlayerMove.createMove(new Location(3, 2), -8168, new GamePiece(false)),
    };

    private ExpectedSearchableResults() {
    }
}