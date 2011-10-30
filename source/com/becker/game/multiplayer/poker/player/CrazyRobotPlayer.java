/** Copyright by Barry G. Becker, 2000-2011. Licensed under MIT License: http://www.opensource.org/licenses/MIT  */
package com.becker.game.multiplayer.poker.player;


import com.becker.game.multiplayer.poker.PokerAction;
import com.becker.game.multiplayer.poker.PokerController;

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
    
    @Override
    protected PokerAction createAction(PokerController pc) {
        PokerAction.Name action;
        int raise = 0;
        if ((getCash() > getCallAmount(pc)) && (getHand().getScore() > 10 || Math.random() > 0.3)) {
            action =  PokerAction.Name.RAISE;
            raise = getRaise(pc);
        } else if (getHand().getScore() > 1 || Math.random() > 0.2 || allOthersFolded(pc)) {
            action =  PokerAction.Name.CALL;
        } else {
            action = PokerAction.Name.FOLD;
        }
        return new PokerAction(getName(), action, raise);
    }

    @Override
    protected int getRaise(PokerController pc) {
        int allInAmt = pc.getAllInAmount() - getContribution() - getCallAmount(pc);
        int raiseAmt = 0;
        if (getHand().getScore() > 100) {
            raiseAmt = min(DESIRED_RAISE, getCash(), allInAmt);
        }
        else {
            raiseAmt = min(2, getCash(), allInAmt);
        }
        return raiseAmt;
    }

    @Override
    public String getType() {
        return "Crazy";
    }

}



