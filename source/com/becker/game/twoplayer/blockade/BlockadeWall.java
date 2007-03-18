package com.becker.game.twoplayer.blockade;


import java.util.*;

/**
 *  the CheckersPiece describes the physical marker at a location on the board.
 *  Its either a King or a Regular piece.
 *  Not that there is no player ownership for walls (that is why we do not extend GamePiece).
 *  Both players have to abide by them equally regardless of who placed them.
 *
 * @see BlockadeBoard
 * @author Barry Becker
 */
public class BlockadeWall
{

    // whether the wall is VERTICAL or HORIZONTAL
    private boolean isVertical_;

    // the BlockadeBoardPosition that contain the wall (on the south or east faces depending on the orientation).
    private Set positions_;

    /**
     * constructor
     */
    public BlockadeWall( boolean isVertical)
    {
        isVertical_ = isVertical;
        positions_ = new HashSet();
    }

    /**
     * constructor
     */
    public BlockadeWall( boolean isVertical, Set positions)
    {
        isVertical_ = isVertical;
        positions_ = positions;
    }

    /**
     *
     * @return true if this is a vertically oriented wall.
     */
    public boolean isVertical()
    {
        return isVertical_;
    }

    /**
     *
     * @return true if this is a horizontally oriented wall.
     */
    public boolean isHorizontal()
    {
        return !isVertical_;
    }



    /**
     * @return  the positions bordered by this wall.
     */
    public Set getPositions()
    {
        return positions_;
    }

    /**
     *   create a deep copy of the position.
     */
    public BlockadeWall copy()
    {
        BlockadeWall w = new BlockadeWall( isVertical_, positions_ );
        return w;
    }

    public String toString()
    {
        // we may also want to include the position that the wall is at.
        StringBuffer buf = new StringBuffer("wall: "+(isVertical()?"V":"H"));
        Iterator it = positions_.iterator();
        while (it.hasNext()) {
            BlockadeBoardPosition pos = (BlockadeBoardPosition)it.next();
            buf.append(pos.toString()+(isVertical()?"eastBlocked ":"southBlocked "));
        }
        return buf.toString();
    }
}



