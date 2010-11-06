package com.becker.game.twoplayer.common;

import com.becker.game.common.*;

import java.util.List;


/**
 * Defines the structure of the board and the pieces on it.
 * Each BoardPosition can contain a piece.
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
    @Override
    protected boolean makeInternalMove( Move move )
    {
        TwoPlayerMove m = (TwoPlayerMove)move;
        if ( !m.isPassOrResignation() ) {
            BoardPosition pos = positions_[m.getToRow()][m.getToCol()];
            assert(m.getPiece() != null): "moves piece was null :" + m;
            pos.setPiece(m.getPiece());  // need copy?  I don't think so.
            GamePiece piece = pos.getPiece();
            assert (piece!=null):
                    "The piece was " + piece + ". Moved to " + m.getToRow() + ", " + m.getToCol();
            // make the moveList part of the board instead of the controller
            if ( GameContext.getDebugMode() > 0 ) {
                piece.setAnnotation( Integer.toString(getMoveList().getNumMoves()) );
            }
        }
        return true;
    }

    /**
     * @param moves list of moves to make all at once.
     */
    protected void makeMoves(List moves) {
        for (Object m : moves) {
            Move move = (Move) m;
            makeMove(move);
        }
    }

    /**
     * Num different states. E.g. black queen.
     * This is used primarily for the Zobrist hash. You do not need to override if yo udo not use it.
     * @return number of different states this position can have.
     */
    public abstract int getNumPositionStates();


    /**
     * The index of the state for tihs position.
     * Perhaps this would be better abstract.
     * @return The index of the state for tihs position.
     */
    public int getStateIndex(BoardPosition pos) {
        if (!pos.isOccupied()) {
            return 0;
        } else {
            return pos.getPiece().isOwnedByPlayer1()? 1:2;
        }
    }

    @Override
    public String toString() {
        StringBuilder bldr = new StringBuilder(1000);
        bldr.append("\n");
        int nRows = getNumRows();
        int nCols = getNumCols();
        TwoPlayerMove m = (TwoPlayerMove) getMoveList().getLastMove();
        for ( int i = 1; i <= nRows; i++ )   {
          for ( int j = 1; j <= nCols; j++ ) {
              BoardPosition pos = this.getPosition(i,j);
              if (pos.isOccupied()) {
                  if (pos.getLocation().equals(m.getToLocation()))
                      bldr.append("[").append(pos.getPiece()).append("]");
                  else
                      bldr.append(pos.getPiece());
              }
              else {
                  bldr.append(" _ ");
              }
           }
           bldr.append("\n");
        }
        return bldr.toString();
    }
}