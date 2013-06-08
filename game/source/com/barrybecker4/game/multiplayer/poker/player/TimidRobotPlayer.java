// Copyright by Barry G. Becker, 2013. Licensed under MIT License: http://www.opensource.org/licenses/MIT
package com.barrybecker4.game.multiplayer.poker.player;


import com.barrybecker4.game.multiplayer.poker.PokerController;
import com.barrybecker4.game.multiplayer.poker.model.PokerAction;

import java.awt.Color;

/**
 * Represents a Crazy Robot Poker player.
 *
 * @author Barry Becker
 */
public class TimidRobotPlayer extends PokerRobotPlayer {

    private static final int DESIRED_RAISE = 5;


    public TimidRobotPlayer(String name, int cash, Color color, RobotType rType) {
        super(name, cash, color, rType);
    }

    @Override
    protected PokerAction createAction(PokerController pc) {
        PokerAction.Name action;
        int raise = 0;
        if ((getCash() > getCallAmount(pc)) && (getHand().getScore() > 100 || Math.random() > 0.4)) {
            action = PokerAction.Name.RAISE;
            raise = getRaise(pc);
        } else if (getHand().getScore() > 30 || Math.random() > 0.2 || allOthersFolded(pc)) {
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
        if (getHand().getScore() > 500) {
            raiseAmt = min(DESIRED_RAISE, getCash(), allInAmt);
        }
        else {
            raiseAmt = min(2, getCash(), allInAmt);
        }
        return raiseAmt;
    }

}



