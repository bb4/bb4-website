package com.becker.game.multiplayer.poker;

import com.becker.game.common.*;

/**
 * Captures the delta state change of everything that happened during one tround of the poker game.
 * This should include the amount that each player has contributed to the pot.
 * Allows for undo.
 *
 * @see com.becker.game.multiplayer.poker.PokerTable
 * @author Barry Becker
 */
public class PokerRound extends Move
{

    // a list of player chip contributions to the pot.
    // private List contributions_;


    /**
     *  Constructor. This should never be called directly
     *  use the factory method createMove instead.
     */
    private PokerRound( )
    {
    }

    /**
     *  factory method for getting new moves.
     *  used to use recycled objects, but did not increase performance, so I removed it.
     */
    public static PokerRound createMove()
    {
        PokerRound m = new PokerRound();
        return m;
    }

}


