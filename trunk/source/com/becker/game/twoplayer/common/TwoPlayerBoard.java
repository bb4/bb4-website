package com.becker.game.twoplayer.common;

import com.becker.game.common.*;

import java.util.*;


/**
 * Defines the structure of the blockade board and the pieces on it.
 * Each BlockadeBoardPosition can contain a piece and south and east walls.
 *
 * @author Barry Becker
 */
public abstract class TwoPlayerBoard extends Board
{

    public Move undoMove() {
        if ( !getMoveList().isEmpty() ) {
            TwoPlayerMove m = (TwoPlayerMove) getMoveList().removeLast();
            undoInternalMove( m );
            return m;
        }
        return null;
    }

    /**
     * given a move specification, execute it on the board
     * This places the players symbol at the position specified by move.
     * @param move the move to make, if possible.
     * @return false if the move is illegal.
     */
    protected boolean makeInternalMove( Move move )
    {
        TwoPlayerMove m = (TwoPlayerMove)move;
        if ( !m.isPassingMove() ) {
            BoardPosition pos = positions_[m.getToRow()][m.getToCol()];
            pos.setPiece(m.piece.copy());  // need copy?
            GamePiece piece = pos.getPiece();
            assert (piece!=null):
                    "The piece was " + piece + ". Moved to " + m.getToRow() + ", " + m.getToCol();

            piece.setTransparency( m.transparency );

            // make the moveList part of the board instead of the controller
            if ( GameContext.getDebugMode() > 0 ) {
                piece.setAnnotation( Integer.toString(getNumMoves()) );
            }
        }
        return true;
    }


    public void makeMoves(List moves) {
        for (Object m : moves) {
             Move move = (Move) m;
            makeMove(move);
        }
    }

}
