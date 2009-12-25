package com.becker.game.twoplayer.tictactoe;

import com.becker.game.twoplayer.pente.PenteBoard;

/**
 * Representation of a TicTacToe Game Board
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

}
