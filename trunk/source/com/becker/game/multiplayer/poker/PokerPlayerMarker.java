package com.becker.game.multiplayer.poker;

import com.becker.game.common.*;
import com.becker.game.multiplayer.poker.player.*;
import com.becker.common.*;

import com.becker.game.multiplayer.common.MultiPlayerMarker;
import java.awt.*;

/**
 * Represents a Poker player in the viewer.
 * For the player we draw their picture or icon, their chips (or cash), various annotations and their cards.
 *
 * @see com.becker.game.multiplayer.poker.PokerTable
 * @author Barry Becker
 */
public class PokerPlayerMarker extends MultiPlayerMarker
{

    private static final long serialVersionUID = 1;


    public PokerPlayerMarker(PokerPlayer owner)
    {
        super(owner);
    }
  
}



