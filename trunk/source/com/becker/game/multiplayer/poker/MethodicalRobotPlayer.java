package com.becker.game.multiplayer.poker;


import com.becker.game.multiplayer.galactic.GalacticRobotPlayer;
import com.becker.game.multiplayer.galactic.Planet;
import com.becker.game.multiplayer.galactic.Galaxy;

import java.util.*;
import java.util.List;
import java.awt.*;

/**
 * Represents a robotic Poker Player.
 *
 * @author Barry Becker
 */
public class MethodicalRobotPlayer extends PokerRobotPlayer
{

    public MethodicalRobotPlayer(String name, int cash, Color color)
    {
        super(name, cash, color);
    }

    /**
     *
     * @return an appropriate action based on the situation
     */
    public Action getAction(PokerController pc) {
        if (getHand().getScore() < 10 || Math.random() > .1) {
            return Action.FOLD;
        } else if (getHand().getScore() >= 10 || Math.random() > .2) {
            return Action.CALL;
        } else {
            return Action.RAISE;
        }
    }

    public int getRaise() {
        if (getHand().getScore() > 100 || Math.random() > .2) {
            return this.getCash()/10;
        } else if (getHand().getScore() > 10 || Math.random() > .1) {
            return 1 + this.getCash()/40;
        } else {
            return  1;
        }
    }

}



