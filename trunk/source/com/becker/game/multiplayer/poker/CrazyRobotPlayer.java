package com.becker.game.multiplayer.poker;


import java.util.*;
import java.util.List;
import java.awt.*;

/**
 * Represents a Robot Poker player.
 *
 * @author Barry Becker
 */
public class CrazyRobotPlayer extends PokerRobotPlayer
{


    public CrazyRobotPlayer(String name, int cash, Color color)
    {
        super(name, cash, color);
    }

    /**
     *
     * @return an appropriate action based on the situation
     */
    public Action getAction(PokerController pc) {
        if (getHand().getScore() > 10 || Math.random() > .3) {
            return Action.RAISE;
        } else if (getHand().getScore() > 1 || Math.random() > .2 || allOthersFolded(pc)) {
            return Action.CALL;
        }
        return Action.FOLD;
    }

    public int getRaise() {
        if (getHand().getScore() >100) {
            return 10;
        }
        return 2;
    }

}



