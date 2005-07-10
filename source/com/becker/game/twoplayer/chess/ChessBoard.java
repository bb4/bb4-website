package com.becker.game.twoplayer.chess;

import com.becker.game.twoplayer.checkers.CheckersBoard;
import com.becker.game.twoplayer.common.TwoPlayerMove;
import com.becker.game.common.Move;
import com.becker.game.common.*;

import java.util.Iterator;
import java.util.List;


/**
 * Defines the structure of the Chess board and the pieces on it.
 * Chess is played on a ChekersBoard so we derive from that.
 * @see com.becker.game.twoplayer.checkers.CheckersBoard
 *
 * @author Barry Becker
 */
public class ChessBoard extends CheckersBoard
{
    // arrangement of pieces
    private static char[] PIECE_ARRANGEMENT = {
        ChessPiece.ROOK, ChessPiece.KNIGHT, ChessPiece.BISHOP,
        ChessPiece.QUEEN, ChessPiece.KING,
        ChessPiece.BISHOP, ChessPiece.KNIGHT, ChessPiece.ROOK
    };

    // constructor
    public ChessBoard()
    {}

    /**
     *  reset the board to its initial state.
     */
    public void reset()
    {
        super.reset();
        assert ( positions_!=null );
        int i,j;
        int numRows = getNumRows();
        for ( i = 1; i <= numRows; i++ )  {
            for ( j = 1; j <= getNumCols(); j++ ) {
                positions_[i][j] = new BoardPosition( i, j, null);
            }
        }

        // black player's pieces
        for ( j = 1; j <= getNumCols(); j++ ) {
            positions_[1][j] = new BoardPosition( 1, j, new ChessPiece(true, PIECE_ARRANGEMENT[j-1]) );
            positions_[2][j] = new BoardPosition( 2, j, new ChessPiece( true, ChessPiece.PAWN) );
        }
        // red player's pieces
        for ( j = 1; j <= getNumCols(); j++ ) {
            positions_[numRows][j] = new BoardPosition( numRows, j, new ChessPiece(false, PIECE_ARRANGEMENT[j-1]) );
            positions_[numRows-1][j] = new BoardPosition( (numRows-1), j, new ChessPiece(false, ChessPiece.PAWN));
        }
    }

    /**
     * determine if the specified opponent position is endangering your king.
     * @param pos the opponent position to misc
     */
    public boolean isKingCheckedByPosition(BoardPosition pos, Move lastMove)
    {
        boolean checked = false;

        if (pos.isUnoccupied())
            return false;
        List moves = ((ChessPiece)pos.getPiece()).findPossibleMoves(this, pos.getRow(), pos.getCol(), lastMove);

        // loop through the possible moves.
        // if any of them capture the king then the opponents king is in check.
        Iterator it = moves.iterator();
        while (it.hasNext()) {
            ChessMove nextMove = (ChessMove)it.next();
            CaptureList cl = nextMove.captureList;
            if (null != cl && !cl.isEmpty()) {
                GamePiece piece = ((BoardPosition)cl.getFirst()).getPiece();
                if (piece.getType() == ChessPiece.KING) {
                    checked = true;
                    break;
                }
            }
        }
        return checked;
    }


    /**
     * given a move specification, execute it on the board.
     * This places the players symbol at the position specified by move.
     * @param move to make
     */
    protected boolean makeInternalMove( Move move )
    {
        ChessMove m = (ChessMove) move;
        BoardPosition oldPos = positions_[m.getFromRow()][m.getFromCol()];
        BoardPosition newPos = positions_[m.getToRow()][m.getToCol()];

        // remove the captures before we place the moved piece since it may be underneath.
        removeCaptures( m.captureList );
        assert (oldPos.getPiece() != null): "oldpos="+oldPos+" m="+m;
        if (m != null && oldPos.getPiece() != null) {
            m.setFirstTimeMoved(((ChessPiece)oldPos.getPiece()).isFirstTimeMoved());
            newPos.setPiece(m.piece);

            // once its been moved its no longer the first time its been moved
            ((ChessPiece)newPos.getPiece()).setFirstTimeMoved(false);

            clear(positions_[m.getFromRow()][m.getFromCol()]);
        }
        return true;
    }

    /**
     * for checkers, undoing a move means moving the piece back and
     * restoring any captures.
     * @param move to undo
     */
    protected void undoInternalMove( Move move )
    {
        ChessMove m = (ChessMove) move;
        BoardPosition start = positions_[m.getFromRow()][m.getFromCol()];
        start.setPiece(m.piece);

        clear(positions_[m.getToRow()][m.getToCol()]);
        // restore the firstTimeMoved status of the piece since we
        // may be moving it back to its original position.
        ((ChessPiece)start.getPiece()).setFirstTimeMoved(m.isFirstTimeMoved());

        // restore the captured pieces to the board
        restoreCaptures( m.captureList );
    }

     public void removeCaptures( CaptureList captureList )
    {
        if ( captureList != null )
            captureList.removeFromBoard( this );
    }

    public void restoreCaptures( CaptureList captureList )
    {
        if ( captureList != null )
            captureList.restoreOnBoard( this );
    }

    /**
     * @return true if the move puts the player who made the move into check (his king gets put in check).
     */
    public boolean causesSelfCheck(TwoPlayerMove m)
    {
        // need to check all opponent pieces to see if they can capture the piece after it has moved.
        assert (null != m);
        makeMove(m);

        int row, col;
        boolean checked = false;
        for ( row = 1; row <= getNumRows(); row++ ) {
            for ( col = 1; col <= getNumCols(); col++ ) {
                BoardPosition pos = getPosition( row, col );
                if ( pos.isOccupied() && pos.getPiece().isOwnedByPlayer1() == !m.player1 ) {
                    checked = isKingCheckedByPosition(pos, m);
                }
                if (checked) {
                    undoMove();
                    return checked;
                }
            }
        }

        undoMove();
        return false;
    }
}
