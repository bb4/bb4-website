package com.becker.puzzle.hiq;

import com.becker.game.common.*;


/**
 *  Definition for a peg jumping another peg.
 */
public final class PegMove
{
    // the position of the move
    protected byte toRow_;
    protected byte toCol_;

    protected byte fromRow_;
    protected byte fromCol_;

    /**
     * protected Constructor.
     * use the factory method createMove instead.
     */
    protected PegMove() {}

    /**
     * create a move object representing a transition on the board.
     */
    protected PegMove( byte fromRow, byte fromCol,
                       byte destinationRow, byte destinationCol)
    {
        fromRow_ = fromRow;
        fromCol_ = fromCol;
        toRow_ = destinationRow;
        toCol_ = destinationCol;
    }

    protected PegMove(Location from, Location destination) {
        fromRow_ = (byte) from.getRow();
        fromCol_ = (byte) from.getCol();
        toRow_ = (byte) destination.getRow();
        toCol_ = (byte) destination.getCol();
    }

    /**
     * @return  a deep copy.
     */
    public PegMove copy()
    {
        PegMove cp = new PegMove( fromRow_, fromCol_, toRow_, toCol_);
        return cp;
    }

    public int getFromRow()
    {
        return fromRow_;
    }
    public int getFromCol()
    {
        return fromCol_;
    }

    public int getToRow()
    {
        return toRow_;
    }
    public int getToCol()
    {
        return toCol_;
    }

    public String toString()
    {
        StringBuffer s = new StringBuffer();
        //s.append(" sel:"+selected);
        s.append( "from("+fromRow_+", "+fromCol_+") to (" + toRow_ + ", " + toCol_ + ')' );
        return s.toString();
    }
}

