package com.becker.game.twoplayer.blockade;

import com.becker.game.common.GamePiece;
import com.becker.game.common.Move;
import com.becker.game.twoplayer.common.TwoPlayerMove;

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
    private int direction_;

    // the wall placed as part of this move
    private BlockadeWall wall_;

    // these directional constants are determined by the hash = 10*(2+rowDif) + 2+colDif
    public static final int NORTH_NORTH = 2;   // -2 0
    public static final int NORTH_WEST = 11;   // -1 -1
    public static final int NORTH = 12;        // -1 0
    public static final int NORTH_EAST = 13;   // -1 1
    public static final int WEST_WEST = 20;    // 0 -2
    public static final int WEST = 21;         // 0 -1
    public static final int EAST = 23;         // 0 1
    public static final int EAST_EAST = 24;    // 0 2
    public static final int SOUTH_WEST = 31;   // 1 -1
    public static final int SOUTH = 32;         // 1 0
    public static final int SOUTH_EAST = 33;    // 1 1
    public static final int SOUTH_SOUTH = 42;   // 2 0


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
        direction_ = (10*(2+rowDif)+2+colDif);
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
        cp.setTransparency(this.getTransparency());
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
    public int getDirection()
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



