package com.becker.game.multiplayer.poker.player;


import com.becker.game.multiplayer.poker.*;

import java.awt.*;

/**
 * Represents a Crazy Robot Poker player.
 *
 * @author Barry Becker
 */
public class CrazyRobotPlayer extends PokerRobotPlayer
{
    private static final long serialVersionUID = 1;
    
    private static final int DESIRED_RAISE = 20;

    public CrazyRobotPlayer(String name, int cash, Color color, RobotType rType)
    {
        super(name, cash, color, rType);
    }

    /**
     *
     * @return an appropriate action based on the situation
     */
    public Action getAction(PokerController pc) {

        if ((getCash() > getCallAmount(pc)) && (getHand().getScore() > 10 || Math.random() > 0.3)) {
            return Action.RAISE;
        } else if (getHand().getScore() > 1 || Math.random() > 0.2 || allOthersFolded(pc)) {
            return Action.CALL;
        }
        return Action.FOLD;
    }

    public int getRaise(PokerController pc, int callAmount) {
        int allInAmt = pc.getAllInAmount() - getContribution();
        if (getHand().getScore() > 100) {
            return min(DESIRED_RAISE, getCash(), allInAmt);
        }
        return min(2, getCash(), allInAmt);
    }

    public String getType() {
        return "Crazy";
    }

}



