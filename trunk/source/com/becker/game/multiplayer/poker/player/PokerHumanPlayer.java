package com.becker.game.multiplayer.poker.player;


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

}



