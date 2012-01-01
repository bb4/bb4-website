package com.becker.game.multiplayer.poker;

import com.becker.game.common.player.PlayerAction;

/**
 * This is what will get sent between client and server as an action for a particular player.
 * Encapsulates the state change.
 *
 * @author Barry Becker
 */
public class PokerAction extends PlayerAction {

    public enum Name { FOLD, CALL, RAISE }

    private Name name_;

    private int raiseAmount_;

    public PokerAction(String playerName, Name name, int raiseAmount)  {
        super(playerName);
        name_ = name;
        raiseAmount_ = raiseAmount;
    }

    public Name getActionName() {
        return name_;
    }


    public int getRaiseAmount() {
        return raiseAmount_;
    }

}