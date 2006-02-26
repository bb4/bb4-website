package com.becker.game.twoplayer.checkers;

import com.becker.game.common.*;
import com.becker.game.common.Move;
import com.becker.game.twoplayer.common.TwoPlayerBoard;

/**
 * Defines the structure of the checkers board and the pieces on it.
 *
 * @author Barry Becker
 */
public class CheckersBoard extends TwoPlayerBoard
{

    private static final int TWO = 2;

    /**
     *   constructor
     *   dimensions must be 8*8 for a checkers/chess board.
     */
    public CheckersBoard()
    {
        numRows_ = 8;
        numCols_ = 8;
        rowsTimesCols_ = 64;
        positions_ = new BoardPosition[numRows_ + 1][numCols_ + 1];
        reset();
    }

    /**
     * reset the board to its initial state.
     */
    public void reset()
    {
        super.reset();
        assert ( positions_!=null );
        int i;
        for ( i = 1; i <= getNumRows(); i++ ) {
            for ( int j = 1; j <= getNumCols(); j++ ) {
                positions_[i][j] = new BoardPosition( i, j, null);
            }
        }

        for ( i = 1; i <= 3; i++ )
            fillRow( i, i % TWO, true );

        for ( i = 6; i <= 8; i++ )
            fillRow( i, i % TWO, false );
    }

    /**
     * fill a reow with pieces during setup.
     */
    private void fillRow( int row, int odd, boolean player1 )
    {
        for ( int j = 1; j <= 4; j++ )
            positions_[row][TWO * j - odd] = new BoardPosition( row, (TWO * j - odd),
                                                                new CheckersPiece(player1, CheckersPiece.REGULAR_PIECE));
    }

    /**
     *  can't change the size of a checkers board.
     */
    public void setSize( int numRows, int numCols )
    {
        if ( numRows != 8 || numCols != 8 )
            GameContext.log(0,  "Can't change the size of a checkers/chess board. It must be 8x8" );
    }

    /**
     * If a checkers game has more than this many moves, then we assume it is a draw.
     */
    public int getMaxNumMoves()
    {
        return 220;
    }

    /**
     * @return typical number of moves in a go game.
     */
    public int getTypicalNumMoves() {
        return rowsTimesCols_;
    }
    
    /**
     * given a move specification, execute it on the board.
     * This places the players symbol at the position specified by move.
     */
    protected boolean makeInternalMove( Move move )
    {
        CheckersMove m = (CheckersMove) move;
        positions_[m.getToRow()][m.getToCol()].setPiece(m.getPiece());

        // we also need to remove the captures from the board
        m.removeCaptures( this );
        positions_[m.getFromRow()][m.getFromCol()].clear();

        return true;
    }

    /**
     * for checkers, undoing a move means moving the piece back and restoring any captures.
     */
    protected void undoInternalMove( Move move )
    {
        CheckersMove m = (CheckersMove) move;
        BoardPosition startPos = positions_[m.getFromRow()][m.getFromCol()];
        startPos.setPiece( m.getPiece().copy() );     // @@ set to a copy of the piece ??
        if ( m.kinged ) { // then it was just kinged and we need to undo it
            startPos.getPiece().setType( CheckersPiece.REGULAR_PIECE );
        }
        // restore the captured pieces to the board
        m.restoreCaptures( this );

        positions_[m.getToRow()][m.getToCol()].clear();
    }

}
