package com.becker.game.common;

import com.becker.common.Util;
import com.becker.game.common.GameContext;
import com.becker.game.common.GamePiece;

/**
 *  This base class describes a change in state from one board
 *  position to the next in a game.
 *
 *  Note: when I first created this class I used a freeList to recycle
 *  old moves and avoid unnecessary object creation. However, while profiling,
 *  I found that this was actually slower than jnot using it.
 *
 *  We could save significant space by removing some of these members,
 *  and reducing the size of the remaining ones. eg toRow, toCol can be byte, value can be float, etc.
 *
 *  @see com.becker.game.common.Board
 *  @author Barry Becker
 */
public class Move implements Comparable
{

    /**
     * value of this move from the point of view of player1.
     * The value is determined by static evaluation of the board.
     */
    public double value;

    /**
     * this is the moveNumberth move made so far by all players.
     * In some games moves are for indiciduals, in other games they may be atomic for the set of all players moving at once.
     * this can be used to render the playing pieces with varying degrees of
     * transparency (0 - opaque 255 - totally transparent)
     * its up to the game board to decide how it wants to use this for rendering if at all.
     */
    public int moveNumber;


    /**
     * protected Constructor.
     * use the factory method createMove instead.
     */
    protected Move()
    {}

    /**
     *  we sort based on the statically evaluated board value
     *  because the inherited value is not known yet.
     *  @return  >0 if move1 bigger, <0 if smaller, =0 if equal
     */
    public final int compareTo( Object move )
    {
        if ( value < ((Move) move).value )
            return -1;
        else if ( value > ((Move) move).value )
            return 1;
        else
            return 0;
    }

    /**
     * Compare 2 moves to see which has a higher value.
     * This allows you to sort movesby this metric.
     * @return  >0 if move1 bigger, <0 if smaller, =0 if equal.
     */
    public final int compare( Object move1, Object move2 )
    {
        if ( ((Move) move1).value < ((Move) move2).value )
            return -1;
        else if ( ((Move) move1).value > ((Move) move2).value )
            return 1;
        else
            return 0;
    }


    public String toString()
    {
        return "The value of this move is "+value;
    }
}

