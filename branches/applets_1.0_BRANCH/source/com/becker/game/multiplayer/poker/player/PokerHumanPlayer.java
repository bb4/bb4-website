package com.becker.game.multiplayer.poker.player;

import com.becker.game.common.player.PlayerAction;
import com.becker.game.multiplayer.common.MultiGameController;
import com.becker.game.multiplayer.poker.PokerAction;

import java.awt.*;


/**
 * Represents an Human Poker player.
 *
 * @author Barry Becker
 */
public class PokerHumanPlayer extends PokerPlayer

{
    private static final long serialVersionUID = 1;
    

    public PokerHumanPlayer(String name,  int money, Color color)
    {
        super(name, money, color, true);
    }
    
    @Override
    public PlayerAction getAction(MultiGameController pc) {
        return action_;
    }
    
    @Override
    public void setAction(PlayerAction action) {
        action_ = (PokerAction) action;        
    }

}


