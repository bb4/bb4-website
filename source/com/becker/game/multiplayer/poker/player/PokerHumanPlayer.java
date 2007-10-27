package com.becker.game.multiplayer.poker.player;


import com.becker.game.multiplayer.poker.PokerAction;
import com.becker.game.multiplayer.poker.PokerController;
import java.awt.*;


/**
 * Represents an Human Poker player.
 *
 * @author Barry Becker
 */
public class PokerHumanPlayer extends PokerPlayer

{
    private static final long serialVersionUID = 1;
    
    private PokerAction action_;

    public PokerHumanPlayer(String name,  int money, Color color)
    {
        super(name, money, color, true);
    }
    
    public PokerAction getAction(PokerController pc) {
        return action_;
    }
    
    public void setAction(PokerAction action) {
        action_ = action;        
    }

}



