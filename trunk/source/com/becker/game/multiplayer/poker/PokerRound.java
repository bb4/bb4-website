package com.becker.game.multiplayer.poker;

import com.becker.game.common.*;
import com.becker.game.common.Move;
import com.becker.sound.MusicMaker;

import java.util.*;

/**
 *  Captures the delta state change of everything that happened during one tround of the poker game.
 *  This should include the amount that each player has contributed to the pot.
 *
 *  @see com.becker.game.multiplayer.poker.PokerTable
 *  @author Barry Becker
 */
public class PokerRound extends Move
{

    // a list of battle simulations
    //private List contributions_;


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
    public static PokerRound createMove(int moveNum)
    {
        PokerRound m = new PokerRound();

        m.moveNumber = moveNum;
        return m;
    }

}



