package com.becker.game.twoplayer.blockade;

import com.becker.game.common.*;
import com.becker.game.twoplayer.common.*;

/**
 *  Describes a change in state from one board
 *  position to the next in a Blockade game.
 *
 *  @see BlockadeBoard
 *  @author Barry Becker
 */
public class BlockadeMove extends TwoPlayerMove
{

    // the position that the piece is moving from
    private int fromRow_;
    private int fromCol_;

    // the wall placed as part of this move
    private BlockadeWall wall_;

    private Direction direction_;

    /**
     *  Constructor. This should never be called directly
     *  use the factory method createMove instead.
     */
    private BlockadeMove( byte originRow, byte originCol,
                          byte destinationRow, byte destinationCol,
                          double val, GamePiece piece, BlockadeWall w)
    {
        super( destinationRow, destinationCol, val,  piece );
        fromRow_ = originRow;
        fromCol_ = originCol;
        wall_ = w;
        int rowDif = toRow_ - fromRow_;
        int colDif = toCol_ - fromCol_;
        direction_ = Direction.getDirection(rowDif, colDif);
    }

    /**
     *  factory method for getting new moves.
     *  used to use recycled objects, but did not increase performance, so I removed it.
     */
    public static BlockadeMove createMove(
            int originRow, int originCol,
            int destinationRow, int destinationCol,
            double val, GamePiece piece, BlockadeWall w)
    {
        BlockadeMove m = new BlockadeMove( (byte)originRow, (byte)originCol,
                (byte)destinationRow, (byte)destinationCol, val,  piece, w);

        return m;
    }


    /**
     * make a deep copy.
     */
    public TwoPlayerMove copy()
    {

        BlockadeMove cp = createMove( fromRow_, fromCol_, toRow_, toCol_,
                                      getValue(), getPiece(), wall_);
        cp.setSelected(this.isSelected());     
        return cp;
    }
    
    
    /**
     * @param mv  the move to compare to.
     * @return  true if values are equal.
     */
    public boolean equals( Object mv )
    {
         BlockadeMove comparisonMove = (BlockadeMove) mv;
         return (fromRow_ == comparisonMove.getFromRow()) &&
                    (fromCol_ == comparisonMove.getFromCol()) &&
                    (toRow_ == comparisonMove.getToRow()) &&
                    (toCol_ == comparisonMove.getToCol()) &&
                    (wall_.equals(comparisonMove.getWall())) &&
                    (isPlayer1() == comparisonMove.isPlayer1());
    }

    public int getFromRow()
    {
        return fromRow_;
    }
    public int getFromCol()
    {
        return fromCol_;
    }

    public BlockadeWall getWall()
    {
        return wall_;
    }
    public void setWall(BlockadeWall wall)
    {
        wall_ = wall;
    }
    /**
     * @return one of the directional constants defined above (eg SOUTH_WEST)
     */
    public Direction getDirection()
    {
        return direction_;
    }


    public String toString()
    {
        String s = super.toString();
        if (wall_!=null)
            s += " "+wall_.toString();
        s += " (" + fromRow_ + ", " + fromCol_ + ")->(" + toRow_ + ", " + toCol_ + ")";
        return s;
    }
}



