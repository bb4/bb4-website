package com.becker.puzzle.hiq;

import com.becker.game.common.*;
import com.becker.common.*;


/**
 * Definition for a peg jumping another peg.
 * Immutable.
 *@author Barry Becker
 */
public final class PegMove
{
    // the position of the move
    private short data_;  // @@ really want unsigned byte not short here

    /**
     * protected Constructor.
     * use one of the other constructors.
     */
    private PegMove() {}

    /**
     * create a move object representing a transition on the board.
     * A naive implmentation might use 4 four  byte integers to store the from and two values.
     * This would use 16 bytes of memory per move.
     * If we do this, we will quickly run out of memory because fo the vast numbers of moves that must be stored.
     * I will use just 1 byte to store the move information. 
     * All we need to know is the "from" position (which can be stored in 6 bits) and the to direction (which can be stored in 2 bits)
     * I know that a jump is always 2 spaces.
     * The 8 bits in the byte is structured like this:
     *      [xxx]     [xxx]     [xx]
     *  fromRow fromCol  direction (N=0, E=1, W= 2, S= 3)
     */
    protected PegMove( byte fromRow, byte fromCol,
                       byte destinationRow, byte destinationCol)
    {
        //System.out.println("fromR=" + fromRow + " fromCol="+ fromCol + "  toCol="+ destinationRow+" toCol="+ destinationCol);
        byte dir = 0;
        if (destinationRow == fromRow) {
            dir = (byte)((destinationCol - fromCol) > 0 ? 1 : 2);  // E:W            
        }
        else {
            dir =(byte)((destinationRow - fromRow) > 0 ? 3 : 0);  // S:N
        }        
        data_  = (short)(fromRow * 32 + fromCol  * 4 + dir);  
        //System.out.println("dir="+ dir + " data="+data_);
    }

    protected PegMove(Location from, Location destination) {
        this((byte)from.getRow(), (byte)from.getCol(), (byte)destination.getRow(), (byte)destination.getCol());
    }

    /**
     * @return  a deep copy.
     */
    public PegMove copy()
    {
        return new PegMove(getFrom(), getTo());
    }

    public Location getFrom()
    {
        return  new Location(data_ / 32, (data_ / 4) % 8);
    }
  
    public Location getTo()
    {   
         Location loc = getFrom();
         byte fromRow = (byte) loc.getRow();
         byte fromCol =  (byte) loc.getCol();
         byte direction = (byte) (data_ % 4);
         byte rowOffset = 0;
         byte colOffset = 0;
         switch (direction) {
             case 0: rowOffset = -2; break;  // N
             case 1: colOffset = 2; break;   // E
             case 2: colOffset = -2; break;   // W
             case 3: rowOffset = 2; break;  // S
             default: assert false: "invalid direction:"+direction;
         }
         Location loc1 = new Location(fromRow + rowOffset, fromCol + colOffset);
         //System.out.println("loc="+loc1);
         return new Location(fromRow + rowOffset, fromCol + colOffset);
    }
 

    public String toString()
    {
        StringBuffer s = new StringBuffer();
        s.append( "from("+getFrom()+", to (" + getTo() + ')' );
        return s.toString();
    }
}

