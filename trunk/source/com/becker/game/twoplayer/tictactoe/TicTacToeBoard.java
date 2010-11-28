package com.becker.game.twoplayer.tictactoe;

import com.becker.game.twoplayer.pente.PenteBoard;

/**
 * Representation of a TicTacToe Game Board
 *
 * @author Barry Becker
 */
public class TicTacToeBoard extends PenteBoard {

    /** 
     * Constructor.
     * Tic tac toe is always 3x3
     */
    public TicTacToeBoard() {
        setSize( 3, 3 );
    }

    @Override
    public int getMaxNumMoves() {
        return 9;
    }

    /**
     * All empty positions are candidate moves dor tic tac toe.
     * This is a bit similar than what we do for pente.
     */
    @Override
    public void determineCandidateMoves() {
        boolean[][] b = candidateMoves_;
        // first clear out what we had before
        initCandidateMoves();

        int i,j;

        for ( i = 1; i <= getNumRows(); i++ )  {
            for ( j = 1; j <= getNumCols(); j++ )  {
                if ( !getPosition(i, j).isOccupied())
                    b[i][j] = true;
            }
        }
    }

}
