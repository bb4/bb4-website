package com.becker.game.common;

import java.io.*;

/**
 * This constitutes a move by a multiplayer game player.
 * It is what will be serialized and sent between client and server to
 * communitacte the player's action (whether robot or human).
 *
 * @author Barry Becker Date: Sep 24, 2006
 */
public class PlayerAction implements Serializable {

    private static final long serialVersionUID = 1;

    private String playerName_;

    /**
     * 
     * @param playerName the name of the player making the action.
     */
    public PlayerAction(String playerName) {
        playerName_ = playerName;
    }

    public String getPlayerName() {
        return playerName_;
    }
}
