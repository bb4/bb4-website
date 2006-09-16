package com.becker.game.multiplayer.poker.player;


import com.becker.game.multiplayer.poker.*;

import java.awt.*;

/**
 * Represents a robotic Poker Player.
 *
 * @author Barry Becker
 */
public class MethodicalRobotPlayer extends PokerRobotPlayer
{

    public MethodicalRobotPlayer(String name, int cash, Color color, RobotType rType)
    {
        super(name, cash, color, rType);
    }

    /**
     *
     * @return an appropriate action based on the situation
     */
    public Action getAction(PokerController pc) {
        boolean othersFolded = allOthersFolded(pc);

        if (getHand().getScore() >= 10 || Math.random() > 0.1 || othersFolded) {
            return Action.CALL;
        } else if (getCash() > getCallAmount(pc) && Math.random() > 0.1) {
            return Action.RAISE;
        } else {
            return Action.FOLD;
        }
    }

    public int getRaise(PokerController pc, int callAmount) {
        int allInAmt = pc.getAllInAmount() - this.getContribution();
        int max = getCash();

        if (getHand().getScore() > 100 || Math.random() > 0.8) {
            return min(max/10, max, allInAmt);
        }
        else if (getHand().getScore() > 10 || Math.random() > 0.1) {
            return min(1 + max/40, max, allInAmt);
        } else {
            return min(1, max, allInAmt);
        }
    }

}



