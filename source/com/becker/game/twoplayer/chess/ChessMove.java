package com.becker.game.twoplayer.chess;

import com.becker.common.Location;
import com.becker.game.common.CaptureList;
import com.becker.game.common.GamePiece;
import com.becker.game.twoplayer.common.TwoPlayerMove;

/**
 *  Describes a change in state from one board
 *  position to the next in a Chess game.
 *
 *  @see ChessBoard
 *  @author Barry Becker
 */
public class ChessMove extends TwoPlayerMove
{
    /** the position that the piece is moving from */
    protected Location fromLocation_;

    /**
     * this is null (if no captures) or 1 if there was a capture.
     * in chess there can never be more than one piece captured by a single move.
     */
    public CaptureList captureList = null;

    /** True if the first time this piece has moved. */
    private boolean firstTimeMoved_ = true;


    /**
     *  Constructor. This should never be called directly
     *  use the factory method createMove instead.
     */
    private ChessMove( byte originRow, byte originCol,
                          byte destinationRow, byte destinationCol,
                          CaptureList captures,
                          int val, GamePiece piece )
    {
        super( destinationRow, destinationCol, val, piece );
        fromLocation_ = new Location(originRow, originCol);

        captureList = captures;
        firstTimeMoved_ = true;
    }

    /**
     * factory method for getting new moves.
     * I used to use recylced objects, but it did not seem to improve performance so I dropped it.
     * @return new chess move
     */
    public static ChessMove createMove(
            int originRow, int originCol,
            int destinationRow, int destinationCol,
            CaptureList captures,
            int val, GamePiece piece )
    {
        return new ChessMove( (byte)originRow, (byte)originCol,
                (byte)destinationRow, (byte)destinationCol, captures, val, piece );
    }

    public int getFromRow()
    {
        return fromLocation_.getRow();
    }

    public int getFromCol()
    {
        return fromLocation_.getCol();
    }

    /**
     * make a deep copy of this move.
     */
    @Override
    public TwoPlayerMove copy()
    {
        CaptureList newList = null;
        if ( captureList != null ) {
            // then make a deep copy
            newList = captureList.copy();
        }
        ChessMove cp =
                createMove( fromLocation_.getRow(), fromLocation_.getCol(),
                                   toLocation_.getRow(), toLocation_.getCol(),
                                   newList, getValue(), getPiece());
        cp.setSelected(this.isSelected());
        cp.firstTimeMoved_ = this.firstTimeMoved_;
        return cp;
    }

    public boolean isFirstTimeMoved()
    {
        return firstTimeMoved_;
    }
    public void setFirstTimeMoved( boolean firstTimeMoved)
    {
        firstTimeMoved_ = firstTimeMoved;
    }


    @Override
    public String toString()
    {
        StringBuffer sb = new StringBuffer(super.toString());
        if (this.isFirstTimeMoved())
          sb.append(" firstTimeMoved ");

        if ( captureList != null ) {
            sb.append( captureList.toString() );
        }
        sb.append(" (").append(fromLocation_).append(")->(").append(toLocation_).append(')');
        return sb.toString();
    }
}



