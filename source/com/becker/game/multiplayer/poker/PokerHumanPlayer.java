package com.becker.game.multiplayer.poker;


import java.awt.*;


/**
 * Represents an Human Poker player.
 *
 * @author Barry Becker
 */
class PokerHumanPlayer extends PokerPlayer
{


    public PokerHumanPlayer(String name,  int money, Color color)
    {
        super(name, money, color, true);
    }

}



