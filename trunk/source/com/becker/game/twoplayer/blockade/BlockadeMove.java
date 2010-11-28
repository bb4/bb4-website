package com.becker.game.twoplayer.blockade;

import com.becker.common.Location;
import com.becker.game.common.board.GamePiece;
import com.becker.game.twoplayer.common.TwoPlayerMove;

import java.util.Iterator;

/**
 *  Describes a change in state from one board
 *  position to the next in a Blockade game.
 *
 *  @see BlockadeBoard
 *  @author Barry Becker
 */
public class BlockadeMove extends TwoPlayerMove {

    /** the position that the piece is moving from */
    private Location fromLocation_;

    /** the wall placed as part of this move. Immutable. */
    private BlockadeWall wall_;

    private final Direction direction_;

    /**
     *  Constructor. This should never be called directly
     *  use the factory method createMove instead.
     */
    public BlockadeMove(Location origin, Location destination,
                        int val, GamePiece piece, BlockadeWall w) {
        super( destination, val,  piece );
        fromLocation_ = origin;
        wall_ = w;
        direction_ = initDirection();
    }

    /**
     * Factory method for getting new moves.
     * @return new move
     */
    public static BlockadeMove createMove( Location origin, Location destination,
                                           int val, GamePiece piece, BlockadeWall w) {

        return new BlockadeMove( origin, destination, val,  piece, w);
    }

    /**
     * copy constructor
     */
    public BlockadeMove(BlockadeMove move) {
        super(move);
        this.fromLocation_ = move.fromLocation_;
        this.wall_ = move.wall_;
        direction_ = initDirection();
    }

    /**
     * make a deep copy.
     */
    @Override
    public BlockadeMove copy() {

        return new BlockadeMove(this);
    }

    private Direction initDirection() {
        int rowDif = toLocation_.getRow() - fromLocation_.getRow();
        int colDif = toLocation_.getCol() - fromLocation_.getCol();
        return Direction.getDirection(rowDif, colDif);
    }    
    
    /**
     * @param mv  the move to compare to.
     * @return  true if values are equal.
     */
    @Override
    public boolean equals( Object mv ) {
         BlockadeMove comparisonMove = (BlockadeMove) mv;
         return (getFromLocation().equals(comparisonMove.getFromLocation())) &&
                    (getToLocation().equals(comparisonMove.getToLocation())) &&
                    ((wall_==null && comparisonMove.getWall() == null) 
                     ||wall_.equals(comparisonMove.getWall())) &&
                    (isPlayer1() == comparisonMove.isPlayer1());
    }
    
    @Override
    public int hashCode() {
       return (100*fromLocation_.getRow() + 99* fromLocation_.getCol()
                  + 30* getToLocation().getRow() + getToLocation().getCol() + wall_.hashCode() + (isPlayer1()?54321:0));
    }

    public int getFromRow() {
        return fromLocation_.getRow();
    }

    public int getFromCol() {
         return fromLocation_.getCol();
    }

    Location getFromLocation() {
        return fromLocation_;
    }

    public BlockadeWall getWall() {
        return wall_;
    }

    public void setWall(BlockadeWall wall) {
        wall_ = wall;
    }
    /**
     * @return one of the directional constants defined above (eg SOUTH_WEST)
     */
    public Direction getDirection() {
        return direction_;
    }
    
    
    /**
     * Check to see if a given wall blocks the move.
     * We assume the move is valid (eg does not move off the board or anything like that).
     * @param wall to see if blocking our move.
     * @param board
     * @return  true if the wall blocks this move.
     */
    @SuppressWarnings("fallthrough")
    public boolean isMoveBlockedByWall(BlockadeWall wall, BlockadeBoard board) {
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
                if (start.isSouthBlocked()) {
                    blocked = true;
                }
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

    /**
     * @return a string, which if executed will create a move identical to this instance.
     */
    @Override
    public String getConstructorString() {

        String wallCreator ="null";
        if ( getWall() != null) {
            Iterator<BlockadeBoardPosition> it = getWall().getPositions().iterator();
            BlockadeBoardPosition p1 = it.next();
            BlockadeBoardPosition p2 = it.next();
            wallCreator = "new BlockadeWall(new BlockadeBoardPosition(" + p1.getRow()  +", "+ p1.getCol() +  "), "
                                                             + "new BlockadeBoardPosition(" + p2.getRow()  +", "+ p2.getCol() +"))";
        }
        String pieceCreator = "null";
        if (getPiece() != null) {
            pieceCreator = "new GamePiece(" + getPiece().isOwnedByPlayer1() + ")";
        }

        return "new BlockadeMove("
                + getFromLocation().getRow() + "," + getFromLocation().getCol() +",  "
                +  getToLocation().getRow()  + "," + getToLocation().getCol()  + ", " + getValue() + ", "
                + pieceCreator +", " + wallCreator +
                "),";
    }

    
    @Override
    public String toString() {
        
        String s = super.toString();
        if (wall_!=null) {
            s += " "+wall_.toString();
        }
        else {
            s += " (no wall placed)";
        }
        s += " (" + fromLocation_ + ")->(" + getToLocation() + ")";
        return s;
    }
}



