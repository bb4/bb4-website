package com.becker.game.multiplayer.poker;

import com.becker.game.multiplayer.common.MultiPlayerMarker;
import com.becker.game.multiplayer.poker.player.PokerPlayer;

/**
 * Represents a Poker player in the viewer.
 * For the player we draw their picture or icon, their chips (or cash), various annotations and their cards.
 *
 * @see PokerTable
 * @author Barry Becker
 */
public class PokerPlayerMarker extends MultiPlayerMarker {

    public PokerPlayerMarker(PokerPlayer owner)  {
        super(owner);
    }
}


