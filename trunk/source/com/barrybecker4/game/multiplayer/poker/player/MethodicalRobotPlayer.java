/** Copyright by Barry G. Becker, 2000-2011. Licensed under MIT License: http://www.opensource.org/licenses/MIT  */
package com.barrybecker4.game.multiplayer.poker.player;


import com.barrybecker4.game.multiplayer.poker.PokerAction;
import com.barrybecker4.game.multiplayer.poker.PokerController;

import java.awt.*;

/**
 * Represents a robotic Poker Player.
 *
 * @author Barry Becker
 */
public class MethodicalRobotPlayer extends PokerRobotPlayer
{
    private static final long serialVersionUID = 1;

    public MethodicalRobotPlayer(String name, int cash, Color color, RobotType rType)
    {
        super(name, cash, color, rType);
    }

    /**
     * @return an appropriate action based on the situation
     */
    @Override
    protected PokerAction createAction(PokerController pc) {
        boolean othersFolded = allOthersFolded(pc);

        PokerAction.Name action;
        int raise = 0;
        if (getHand().getScore() >= 10 || Math.random() > 0.1 || othersFolded) {
            action = PokerAction.Name.CALL;
        } else if (getCash() > getCallAmount(pc) && Math.random() > 0.1) {
            action = PokerAction.Name.RAISE;
            raise = getRaise(pc);
        } else {
            action = PokerAction.Name.FOLD;
        }
        return new PokerAction(getName(), action, raise);
    }

    @Override
    protected int getRaise(PokerController pc) {
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



