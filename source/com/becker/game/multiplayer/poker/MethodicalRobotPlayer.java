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
        boolean othersFolded = allOthersFolded(pc);

        if (getHand().getScore() >= 10 || Math.random() > .1 || othersFolded) {
            return Action.CALL;
        } else if (getCash() > getCallAmount(pc) && Math.random() > .1) {
            return Action.RAISE;
        } else {
            return Action.FOLD;
        }
    }

    public int getRaise(PokerController pc) {
        int allInAmt = pc.getAllInAmount();
        int max = (getCash() - getCallAmount(pc));

        if (getHand().getScore() >100 || Math.random() > .8) {
            return min(this.getCash()/10, max, allInAmt);
        }
        else if (getHand().getScore() > 10 || Math.random() > .1) {
            return min(1 + this.getCash()/40, max, allInAmt);
        } else {
            return min(1, max, allInAmt);
        }
    }

}



