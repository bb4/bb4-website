package com.becker.game.common;

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
 *  @see Board
 *  @author Barry Becker
 */
public class Move implements Comparable
{

    /**
     * value of this move from the point of view of player1.
     * The value is determined by static evaluation of the board.
     */
    private double value_;


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
        if ( getValue() < ((Move) move).getValue() )
            return -1;
        else if ( getValue() > ((Move) move).getValue() )
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
        if ( ((Move) move1).value_ < ((Move) move2).value_ )
            return -1;
        else if ( ((Move) move1).value_ > ((Move) move2).value_ )
            return 1;
        else
            return 0;
    }


    public String toString()
    {
        return "The value of this move is "+value_;
    }

    public double getValue() {
        return value_;
    }

    public void setValue(double value) {
        this.value_ = value;
    }
}

