package com.becker.game.common;

import com.becker.common.Location;


/**
 *  the BoardPosition describes the physical marker at a location on the board.
 *  It may be empty if there is no piece there.
 *
 * @see Board
 * @author Barry Becker
 */
public class BoardPosition
{
    /** we need to store the location so we can restore captures.  */
    protected Location location_;

    /** the piece to display at this position. Null if the position is unoccupied. */
    protected GamePiece piece_ = null;


    /**
     * constructor
     * @param row - y position on the board.
     * @param col - x position on the board.
     * @param piece - the pice to put at this position (use null if there is none).
     */
    public BoardPosition( int row, int col, GamePiece piece)  {
        this(new Location(row, col), piece);
    }

    /**
     * constructor
     * @param loc -  location on the board.
     * @param piece - the pice to put at this position (use null if there is none).
     */
    public BoardPosition( Location loc, GamePiece piece)  {
        location_ = loc;
        piece_ = piece;
    }
            
    /**
     * @return  true if values are equal.
     */
    @Override
    public boolean equals( Object pos )  {   
         if ((pos == null) || !(pos.getClass().equals(this.getClass()))) {
             return false;
         }

         BoardPosition comparisonPos = (BoardPosition) pos;
         boolean sameSide = true;
         if (getPiece() != null && comparisonPos.getPiece() != null)   {
             sameSide = (getPiece().isOwnedByPlayer1() == comparisonPos.getPiece().isOwnedByPlayer1());
         }  
         else {
             sameSide = (getPiece() == null && comparisonPos.getPiece() == null);
         }
         return (getRow() == comparisonPos.getRow()) &&
                (getCol() == comparisonPos.getCol()) && (sameSide);
    }
    
    /**
     *override hashcode if you override equals
     */
    @Override
    public int hashCode() {
        return getRow() * 300 + getCol() ;
    }
        
    /**
     * @return the piece at this position if there is one.
     */
    public GamePiece getPiece()  {
        return piece_;
    }

    /**
     * @param piece the piece to assign to this position.
     */
    public void setPiece(GamePiece piece) {
        piece_ = piece;
    }

    /**
     * @return true if the piece space is currently unoccupied
     */
    public final boolean isUnoccupied() {
        return (piece_ == null);
    }

    /**
     * @return true if the piece space is currently occupied
     */
    public final boolean isOccupied() {
         return (piece_ != null);
    }

    public final void setRow( int row ) {
        location_.setRow(row);
    }

    public final int getRow() {
        return location_.getRow();
    }

    public final void setCol( int col ) {
        location_.setCol(col);
    }

    public final int getCol() {
        return location_.getCol();
    }

     public final void setLocation( Location loc ) {
        location_ = loc;
    }

    public final Location getLocation()
    {
        return location_;
    }

    /**
     * @return a deep copy.
     */
    public BoardPosition copy()
    {
        return new BoardPosition( location_, (piece_== null) ? null : piece_.copy());
    }

    /**
     * copy data from another position into me.
     */
    public void copy(BoardPosition p)
    {
        location_ = new Location(p.getRow(), p.getCol());
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
    public final double getDistanceFrom( BoardPosition position ) {
        return location_.getDistanceFrom(position.getLocation());
    }


    /**
     * @param position to check if neighboring
     * @return true if immediate neighbor (nobi neighbor)
     */
    public final boolean isNeighbor( BoardPosition position ) {
        return getDistanceFrom(position) == 1.0;
    }

    /**
     * make it show an empty board position.
     */
    public void clear() {
        setPiece( null );
    }

    /**
     * @return a string representation of the board position
     */
    public String getDescription() {
        return toString(true);
    }

    /**
     * @return a string representation of the board position
     */
    @Override
    public String toString() {
        return toString(false);
    }

    /**
     * @return string form.
     */
    private String toString(boolean longForm) {
        StringBuffer sb = new StringBuffer( "" );
        if (piece_ != null)
            sb.append(longForm? piece_.getDescription() : piece_.toString());
        sb.append(" (").append(location_.toString()).append(')');
        return sb.toString();
    }

}

