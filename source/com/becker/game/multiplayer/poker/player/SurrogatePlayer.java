package com.becker.game.multiplayer.poker.player;

import com.becker.game.common.online.*;

import java.awt.*;

/**
 * @author Barry Becker Date: Feb 3, 2007
 */
public class SurrogatePlayer extends PokerPlayer implements OnlineChangeListener {

    protected ServerConnection connection_;

    public SurrogatePlayer(String name, int money, Color color, boolean isHuman, ServerConnection connection) {
        super(name, money, color, isHuman);
        connection_ = connection;
        connection_.addOnlineChangeListener(this);
    }


    /**
     * Update ourselves based on
     * @param cmd
     */
    public void handleServerUpdate(GameCommand cmd) {
      
    }
}
