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

    private final Direction direction_;

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
                    ((wall_==null && comparisonMove.getWall() == null) ||wall_.equals(comparisonMove.getWall())) &&
                    (isPlayer1() == comparisonMove.isPlayer1());
    }
    
    public int hashCode() {
       return (100*fromRow_ + 99*fromCol_ + 30*toRow_ + toCol_ + wall_.hashCode() + (isPlayer1()?54321:0));
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
    
    
    /**
     * Check to see if a given wall blocks the move.
     * We assume the move is valid (eg does not move off the board or anything like that).
     * @param wall to see if blocking our move.
     * @param board
     * @return  true if the wall blocks this move.
     */
    public boolean isMoveBlockedByWall(BlockadeWall wall, BlockadeBoard board)
    {
        // We assume that this wall does not interfere with other walls as that would be invalid.
        boolean blocked = false;

        int fromRow = getFromRow();
        int fromCol = getFromCol();
        BlockadeBoardPosition start = (BlockadeBoardPosition)board.getPosition(fromRow, fromCol);        
        BlockadeBoardPosition west = start.getNeighbor(Direction.WEST, board); 
        BlockadeBoardPosition north = start.getNeighbor(Direction.NORTH, board); 
        BlockadeBoardPosition south, east;

        switch (getDirection()) {
            case NORTH_NORTH :
                BlockadeBoardPosition northNorth = start.getNeighbor(Direction.NORTH_NORTH, board); 
                if (northNorth.isSouthBlocked())
                    blocked = true;
            case NORTH :
                if (north.isSouthBlocked() )
                    blocked = true;
                break;
            case WEST_WEST :
                BlockadeBoardPosition westWest = start.getNeighbor(Direction.WEST_WEST, board); 
                if (westWest.isEastBlocked() )
                    blocked = true;
            case WEST :
                if (west.isEastBlocked())
                    blocked = true;
                break;
            case EAST_EAST :
                east = start.getNeighbor(Direction.EAST, board); 
                if (east.isEastBlocked())
                    blocked = true;
            case EAST :
                if (start.isEastBlocked())
                    blocked = true;
                break;
            case SOUTH_SOUTH :
                south = start.getNeighbor(Direction.SOUTH, board); 
                if (south.isSouthBlocked())
                    blocked = true;
            case SOUTH :
                if (start.isSouthBlocked())
                    blocked = true;
                break;
            case NORTH_WEST :
                BlockadeBoardPosition northWest = start.getNeighbor(Direction.NORTH_WEST, board); 
                if (!((west.isEastOpen() && northWest.isSouthOpen()) ||
                     (north.isSouthOpen() && northWest.isEastOpen()) ) )  {
                    blocked = true;
                }
                break;
            case NORTH_EAST :
                BlockadeBoardPosition northEast = start.getNeighbor(Direction.NORTH_EAST, board); 
                if (!((start.isEastOpen() && northEast.isSouthOpen()) ||
                     (north.isSouthOpen() && north.isEastOpen()) ) )  {
                    blocked = true;
                }
                break;
            case SOUTH_WEST :
                BlockadeBoardPosition southWest = start.getNeighbor(Direction.SOUTH_WEST, board); 
                if (!((west.isEastOpen() && west.isSouthOpen()) ||
                     (start.isSouthOpen() && southWest.isEastOpen()) ) )  {
                    blocked = true;
                }
                break;
            case SOUTH_EAST :
                south = start.getNeighbor(Direction.SOUTH, board); 
                east = start.getNeighbor(Direction.EAST, board); 
                if (!((start.isEastOpen() && east.isSouthOpen()) ||
                     (start.isSouthOpen() && south.isEastOpen()) ) )  {
                    blocked = true;
                }
                break;
        }

        return blocked;
    }

    
    public String toString()
    {
        String s = super.toString();
        if (wall_!=null) {
            s += " "+wall_.toString();
        }
        else {
            s += " (no wall placed)";
        }
        s += " (" + fromRow_ + ", " + fromCol_ + ")->(" + toRow_ + ", " + toCol_ + ")";
        return s;
    }
}



