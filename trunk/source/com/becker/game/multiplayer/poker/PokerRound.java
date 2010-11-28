package com.becker.game.multiplayer.poker;

import com.becker.game.common.Move;

/**
 * Captures the delta state change of everything that happened during one round of the poker game.
 * This should include the amount that each player has contributed to the pot.
 * Allows for undo.
 *
 * @see PokerTable
 * @author Barry Becker
 */
public class PokerRound extends Move {

    /**
     *  Constructor.
     */
    public PokerRound() {}

}



