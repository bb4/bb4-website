package com.becker.game.twoplayer.tictactoe;

import com.becker.game.twoplayer.pente.PenteBoard;

/**
 * Representation of a Pente Game Board
 *
 * @author Barry Becker
 */
public class TicTacToeBoard extends PenteBoard
{

    /** 
     * Constructor
     */
    public TicTacToeBoard() {
        setSize( 3, 3 );
    }

    @Override
    public int getMaxNumMoves() {
        return 9;
    }

    @Override
    public int getTypicalNumMoves() {
        return 7;
    }
}
