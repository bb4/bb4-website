package com.becker.game.twoplayer.common;

import com.becker.game.common.*;
import com.becker.game.common.Move;


/**
 * Defines the structure of the blockade board and the pieces on it.
 * Each BlockadeBoardPosition can contain a piece and south and east walls.
 *
 * @author Barry Becker
 */
public abstract class TwoPlayerBoard extends Board
{

    /**
     * given a move specification, execute it on the board
     * This places the players symbol at the position specified by move.
     * @param move the move to make, if possible.
     * @return false if the move is illegal.
     */
    public boolean makeMove( Move move )
    {
        TwoPlayerMove m = (TwoPlayerMove)move;
        if ( !m.isPassingMove() ) {
            BoardPosition pos = positions_[m.getToRow()][m.getToCol()];
            pos.setPiece(m.piece.copy());  // need copy?
            GamePiece piece = pos.getPiece();
            assert (piece!=null):
                    "The piece was " + piece + ". Moved to " + m.getToRow() + ", " + m.getToCol();
            //piece.setOwnedByPlayer1( move.piece.isOwnedByPlayer1() );
            //piece.setType( move.piece.getType() );
            piece.setTransparency( m.transparency );
            if ( GameContext.getDebugMode() > 0 )
                piece.setAnnotation( Integer.toString(move.moveNumber) );
        }
        return true;
    }


}
