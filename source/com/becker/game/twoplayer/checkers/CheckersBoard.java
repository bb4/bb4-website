package com.becker.game.twoplayer.checkers;

import com.becker.game.common.board.BoardPosition;
import com.becker.game.common.GameContext;
import com.becker.game.common.Move;
import com.becker.game.twoplayer.common.TwoPlayerBoard;

/**
 * Defines the structure of the checkers board and the pieces on it.
 *
 * @author Barry Becker
 */
public class CheckersBoard extends TwoPlayerBoard {

    public static final int SIZE = 8;
    private static final int TWO = 2;

    /**
     *  Constructor
     *  dimensions must be 8*8 for a checkers/chess board.
     */
    public CheckersBoard() {
        setSize(SIZE, SIZE);
    }

    /** Copy constructor */
    public CheckersBoard(CheckersBoard b) {
        super(b);
    }

    public CheckersBoard copy() {
        return new CheckersBoard(this);        
    }

    /**
     * reset the board to its initial state.
     */
    @Override
    public void reset() {
        super.reset();
        int i;
        for ( i = 1; i <= 3; i++ )
            fillRow( i, i % TWO, true );

        for ( i = 6; i <= 8; i++ )
            fillRow( i, i % TWO, false );
    }

    /**
     * fill a reow with pieces during setup.
     */
    private void fillRow( int row, int odd, boolean player1 ) {
        
        for ( int j = 1; j <= 4; j++ )
            setPosition(new BoardPosition(row, (TWO * j - odd),
                                          new CheckersPiece(player1, CheckersPiece.REGULAR_PIECE)));
    }

    /**
     *  can't change the size of a checkers board.
     */
    @Override
    public void setSize( int numRows, int numCols )  {

        super.setSize(numRows, numCols);
        if ( numRows != SIZE || numCols != SIZE) {
            GameContext.log(0,  "Can't change the size of a checkers/chess board. It must be 8x8" );
        }
    }

    /**
     * If a checkers game has more than this many moves, then we assume it is a draw.
     */
    public int getMaxNumMoves() {
        return 220;
    }

    /**
     * given a move specification, execute it on the board.
     * This places the players symbol at the position specified by move.
     */
    @Override
    protected boolean makeInternalMove( Move move ) {
        CheckersMove m = (CheckersMove) move;
        getPosition(m.getToRow(), m.getToCol()).setPiece(m.getPiece());

        // we also need to remove the captures from the board
        m.removeCaptures( this );
        getPosition(m.getFromRow(), m.getFromCol()).clear();

        return true;
    }

    /**
     * for checkers, undoing a move means moving the piece back and restoring any captures.
     */
    @Override
    protected void undoInternalMove( Move move ) {

        CheckersMove m = (CheckersMove) move;
        BoardPosition startPos = getPosition(m.getFromRow(), m.getFromCol());

        startPos.setPiece( m.getPiece().copy() );
        if ( m.kinged ) {
            // then it was just kinged and we need to undo it
            startPos.getPiece().setType( CheckersPiece.REGULAR_PIECE );
        }
        // restore the captured pieces to the board
        m.restoreCaptures( this );

        getPosition(m.getToRow(), m.getToCol()).clear();
    }


    /**
     * Num different states. E.g. regular piece or king or no pieces at the position.
     * This is used primarily for the Zobrist hash. You do not need to override if yo udo not use it.
     * @return number of different states this position can have.
     */
    @Override
    public int getNumPositionStates() {
        return  5;
    }

    /**
     * The index of the state for tihs position.
     * @return The index of the state for tihs position.
     */
    @Override
    public int getStateIndex(BoardPosition pos) {
        if (pos.isOccupied()) {
            CheckersPiece p = (CheckersPiece) pos.getPiece();
            return (p.isOwnedByPlayer1()? 1:2) + (p.isKing()? 0:2);
        } else {
            return 0;
        }
    }

}
