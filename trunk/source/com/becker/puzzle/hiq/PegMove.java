package com.becker.puzzle.hiq;


/**
 * Definition for a peg jumping another peg.
 * Immutable.
 *@author Barry Becker
 */
public final class PegMove
{
    // the position of the move
    private byte toRow_;
    private byte toCol_;

    private byte fromRow_;
    private byte fromCol_;


    /**
     * create a move object representing a transition on the board.
     * A naive implmentation might use 4 four  byte integers to store the from and to values.
     * This would use 16 bytes of memory per move.
     * If we do this, we will quickly run out of memory because fo the vast numbers of moves that must be stored.
     * I will use just 1 byte to store the move information. 
     * All we need to know is the from position (which can be stored in 6 bits) and the to direction (which can be stored in 2 bits)
     * I know that a jump is always 2 spaces.
     */
    PegMove( byte fromRow, byte fromCol,
                      byte destinationRow, byte destinationCol)
    {
        fromRow_ = fromRow;
        fromCol_ = fromCol;
        toRow_ = destinationRow;
        toCol_ = destinationCol;
    }

    /**
     * @return  a deep copy.
     */
    public PegMove copy()
    {
        return new PegMove(fromRow_, fromCol_, toRow_, toCol_);
    }    

    public byte getFromRow()
    {
        return fromRow_;
    }
    public byte getFromCol()
    {
        return fromCol_;
    }

    public byte getToRow()
    {
        return toRow_;
    }
    public byte getToCol()
    {
        return toCol_;
    }

    @Override
    public String toString()
    {
        StringBuilder s = new StringBuilder();
        s.append("from(").append(fromRow_).append(", ").append(fromCol_).append(") to (");
        s.append(toRow_).append(", ").append(toCol_).append(')');
        return s.toString();
    }
}

