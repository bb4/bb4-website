package com.becker.game.common;


/**
 *  the BoardPosition describes the physical marker at a location on the board.
 *  It may be empty if there is no piece there.
 *
 * @see com.becker.game.common.Board
 * @author Barry Becker
 */
public class BoardPosition
{

    // we need to store the location so we can restore captures
    protected int row_;
    protected int col_;

    // the piece to display at this position. Null if the position is unoccupied.
    protected GamePiece piece_ = null;


    /**
     * constructor
     * @param row - y position on the board.
     * @param col - x position on the board.
     * @param piece - the pice to put at this position (use null if there is none).
     */
    public BoardPosition( int row, int col, GamePiece piece)
    {
        row_ = row;
        col_ = col;
        piece_ = piece;
    }

    /**
     * @return the piece at this position if there is one.
     */
    public GamePiece getPiece()
    {
        return piece_;
    }

    /**
     * @param piece the piece to assign to this position.
     */
    public void setPiece(GamePiece piece)
    {
        piece_ = piece;
    }

    /**
     * @return true if the piece space is currently unoccupied
     */
    public final boolean isUnoccupied()
    {
        return (piece_ == null);
    }

    /**
     * @return true if the piece space is currently occupied
     */
    public final boolean isOccupied()
    {
         return (piece_ != null);
    }

    public final void setRow( int row )
    {
        row_ = row;
    }

    public final int getRow()
    {
        return row_;
    }

    public final void setCol( int col )
    {
        col_ = col;
    }

    public final int getCol()
    {
        return col_;
    }

     public final void setLocation( Location loc )
    {
        row_ = loc.row;
        col_ = loc.col;
    }

    public final Location getLocation()
    {
        return new Location(row_, col_);
    }

    /**
     * create a deep copy.
     */
    public BoardPosition copy()
    {
        BoardPosition p = new BoardPosition( row_, col_, (piece_==null)?null:piece_.copy());
        return p;
    }

    /**
     * copy data from another position into me.
     */
    public void copy(BoardPosition p)
    {
        row_ = p.row_;
        col_ = p.col_;
        if (p.piece_ != null)
            piece_ = p.piece_.copy();
        else
            piece_ = null;
    }


    /**
     * Get the euclidean distance from another board position
     * @param position to get the distance from
     * @return distance from another position
     */
    public final double getDistanceFrom( BoardPosition position )
    {
        double deltaX = this.getCol() - position.getCol();
        double deltaY = this.getRow() - position.getRow();
        return Math.sqrt( deltaX * deltaX + deltaY * deltaY );
    }


    /**
     * @return a string representation of the board position
     */
    public String toString()
    {
        StringBuffer sb = new StringBuffer( "" );
        if (piece_ != null)
            sb.append(piece_.toString());
        sb.append( " (" + row_ + ", " + col_ + ")" );
        return sb.toString();
    }
}

