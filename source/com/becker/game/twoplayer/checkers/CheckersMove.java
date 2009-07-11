package com.becker.game.twoplayer.checkers;

import com.becker.common.Location;
import com.becker.game.common.*;
import com.becker.game.twoplayer.common.TwoPlayerMove;

/**
 *  Describes a change in state from one board
 *  position to the next in a checkers game.
 *
 *  @see CheckersBoard
 *  @author Barry Becker
 */
public class CheckersMove extends TwoPlayerMove
{

    /** the position that the piece is moving from */
    protected Location fromLocation_;

    /** True if the piece just got kinged as a result of this move. */
    public boolean kinged;

    /**a linked list of the pieces that were captured with this move
     * Usually this is null (if no captures) or 1, but could be more.
     */
    public CaptureList captureList = null;

    /**
     *  Constructor. This should never be called directly
     *  use the factory method createMove instead.
     */
    private CheckersMove( byte originRow, byte originCol,
                          byte destinationRow, byte destinationCol,
                          CaptureList captures,
                          double val, GamePiece piece)
    {
        super( destinationRow, destinationCol, val,  piece );
        fromLocation_ = new Location(originRow, originCol);
        kinged = false;
        captureList = captures;
    }

    /**
     *  factory method for getting new moves.
     *  used to use recycled objects, but did not increase performance, so I removed it.
     */
    public static CheckersMove createMove(
            int originRow, int originCol,
            int destinationRow, int destinationCol,
            CaptureList captures,
            double val, GamePiece piece )
    {
        CheckersMove m = new CheckersMove( (byte)originRow, (byte)originCol,
                (byte)destinationRow, (byte)destinationCol, captures, val, piece );

        if ( (piece.getType() == CheckersPiece.REGULAR_PIECE) &&
            ((piece.isOwnedByPlayer1() && m.getToRow() == CheckersController.NUM_ROWS) ||
             (!piece.isOwnedByPlayer1() && m.getToRow() == 1)) ) {
            m.kinged = true;
            piece.setType(CheckersPiece.KING);
            m.setPiece(piece);

        }
        return m;
    }

    public void setToRow(int toRow)
    {
        toLocation_.setRow(toRow);
    }
    public void setToCol(int toCol)
    {
        toLocation_.setCol(toCol);
    }

    public int getFromRow()
    {
        return fromLocation_.getRow();
    }
    public int getFromCol()
    {
        return fromLocation_.getCol();
    }

    public void removeCaptures( CheckersBoard b )
    {
        if ( captureList != null )
            captureList.removeFromBoard( b );
    }

    public void restoreCaptures( CheckersBoard b )
    {
        if ( captureList != null )
            captureList.restoreOnBoard( b );
    }

    /**
     * make a deep copy.
     */
    public TwoPlayerMove copy()
    {
        CaptureList newList = null;
        if ( captureList != null ) {
            // then make a deep copy
            newList = captureList.copy();
        }
        CheckersMove cp = 
                createMove( fromLocation_.getRow(), fromLocation_.getCol(),
                                   toLocation_.getRow(), toLocation_.getCol(),
                                   newList, getValue(), getPiece().copy());
        cp.setSelected(this.isSelected());
        cp.kinged = this.kinged;
        return cp;
    }

    public String toString()
    {
        String s = super.toString();
        if ( kinged )
            s += " (the piece was just kinged)";
        if ( captureList != null ) {
            s += captureList.toString();
        }
        s += " (" + fromLocation_ + ")->(" + toLocation_ + ")";
        return s;
    }
}



