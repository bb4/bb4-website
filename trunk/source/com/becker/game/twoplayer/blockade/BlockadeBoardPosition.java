package com.becker.game.twoplayer.blockade;

import com.becker.game.common.BoardPosition;
import com.becker.game.common.GamePiece;


/**
 * The BlockadeBoardPosition describes the physical markers at a location on the board.
 * It can be empty or occupied. If occupied, then one of the BlockadePieces is there and it has an owner.
 * BlockadeBoardPosistions may have BlockadeWalls present when unoccupied.
 *
 * @see BlockadeBoard
 * @author Barry Becker
 */
public final class BlockadeBoardPosition extends BoardPosition
{

    // the walls
    // to find if the north or west sides are blocked we must examine the north and west bordering positions respectively.
    //private HashSet walls_;
    private BlockadeWall southWall_ = null;
    private BlockadeWall eastWall_ = null;

    // This is a temporary state that is used for some traversal operations.
    private boolean visited_;
    private boolean isPlayer1Home_ = false;
    private boolean isPlayer2Home_ = false;


    /**
     * create a new go stone.
     * @param row location.
     * @param col location.
     * @param piece the piece at this position if there is one (use null if no stone).
     */
    public BlockadeBoardPosition( int row, int col, GamePiece piece, BlockadeWall southWall, BlockadeWall eastWall,
                                  boolean isP1Home, boolean isP2Home)
    {
        super( row, col, piece );
        visited_ = false;
        southWall_ = southWall;
        eastWall_ = eastWall;
        isPlayer1Home_ = isP1Home;
        isPlayer2Home_ = isP2Home;
    }

    /**
     * create a deep copy of this position.
     */
    public final BoardPosition copy()
    {
        BlockadeBoardPosition pos =
            new BlockadeBoardPosition( row_, col_, (piece_==null)?null:piece_.copy(),
                                      (southWall_!=null)?southWall_.copy():null,
                                      (eastWall_!=null)? eastWall_.copy() :null,
                                      isPlayer1Home_, isPlayer2Home_);
        return pos;
    }


    /**
     * copy all fields from another stone to this one.
     */
    public final void copy( BlockadeBoardPosition pos )
    {
        super.copy(pos);
        southWall_ = (pos.getSouthWall()!=null)?pos.getSouthWall():null;
        eastWall_ = (pos.getEastWall()!=null)?pos.getEastWall():null;
        visited_ = pos.isVisited();
    }

    /**
     * @param wall the wall to set south of this position.
     */
    public void setSouthWall( BlockadeWall wall )
    {
        southWall_ = wall;
    }

    /**
     * @return the south wall, if any.
     */
    public BlockadeWall getSouthWall()
    {
        return southWall_;
    }

    /**
     * @param wall the wall to set east of this position.
     */
    public void setEastWall( BlockadeWall wall )
    {
        eastWall_ = wall;
    }

    /**
     * @return  the east wall, if any.
     */
    public BlockadeWall getEastWall()
    {
        return eastWall_;
    }


    public void setVisited( boolean visited )
    {
        visited_ = visited;
    }

    public final boolean isVisited()
    {
        return visited_;
    }

    public Object clone() throws CloneNotSupportedException
    {
        Object clone = super.clone();
        return clone;
    }

    /**
     * @return  true if the path from this cell is blocked to the south.
     */
    public boolean isSouthBlocked()
    {
        return (southWall_ != null);
    }

    /**
     *
     * @return  true if the path from this cell is blocked to the east.
     */
    public boolean isEastBlocked()
    {
        return (eastWall_ != null);
    }

    /**
     * @return  true if the path from this cell is open to the south.
     */
    public boolean isSouthOpen()
    {
        return (southWall_ == null);
    }

    /**
     *
     * @return  true if the path from this cell is open to the east.
     */
    public boolean isEastOpen()
    {
        return (eastWall_ == null);
    }

    /**
     * @return true if this position is a home base.
     */
    public boolean isHomeBase()
    {
        return (isPlayer1Home_ || isPlayer2Home_);
    }

    /**
     * @return true if this position is a home base for the specified player.
     */
    public boolean isHomeBase(boolean player1)
    {
        return (player1? isPlayer1Home_: isPlayer2Home_);
    }
}


