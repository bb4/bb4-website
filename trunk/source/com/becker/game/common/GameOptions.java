package com.becker.game.common;

import java.io.Serializable;


/**
 * Options specified on a per game basis.
 *
 * @author Barry Becker Date: Sep 2, 2006
 */
public abstract class GameOptions implements Serializable {

    private static final long serialVersionUID = 1L;


    protected GameOptions() {}


    /**
     * @return Limit on the number of players allowed to join the game.
     */
    public abstract int getMaxNumPlayers();

    /**
     * Check constraints on options to verify validity.
     * @return  null if no errors, return error messages if constraints violated.
     */
    public String testValidity() {
        return null;
    }
}
