package com.becker.game.common;

/**
 *  This base class describes a change in state from one board
 *  position to the next in a game.
 *
 *  @see com.becker.game.common.board.Board
 *  @author Barry Becker
 */
public class Move implements Comparable<Move>
{
    /**
     * The value of this move from the point of view of player1.
     * The value is determined by static evaluation of the board.
     * We use an integer because 0 width search windows require
     * coarse values to work well.
     */
    private int value_;

    /**
     * protected Constructor.
     * use the factory method createMove instead.
     */
    protected Move()
    {}

    /**
     *  we sort based on the statically evaluated board value
     *  because the inherited value is not known yet.
     *  @return  > 0 if move1 bigger, < 0 if smaller, = 0 if equal
     */
    public int compareTo( Move move )
    {
        if ( getValue() < move.getValue() )   {
            return -1;
        }
        else if ( getValue() > move.getValue() )  {
            return 1;
        }
        else {
            return 0;
        }
    }

    @Override
    public String toString()
    {
        return "The value of this move is " + value_;
    }

    public int getValue() {
        return value_;
    }

    public void setValue(int value) {
        this.value_ = value;
    }
}

