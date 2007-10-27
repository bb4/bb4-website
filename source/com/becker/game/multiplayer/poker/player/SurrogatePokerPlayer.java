package com.becker.game.multiplayer.poker.player;

import com.becker.game.common.online.*;
import com.becker.game.multiplayer.poker.PokerAction;
import com.becker.game.multiplayer.poker.PokerController;

import java.awt.*;

/**
 *@@TODO - individual games should not have surrogate player classes.
 *See game/common/online/SurrogatePlayer you create a surrogate and give it the playerInfo for the actual player.
 *
 * On the server, all players are surrogates except for the robot players.
 * On the client, all players are surrogates except for the human player that is controlling that client.
 *
 * @author Barry Becker Date: Feb 3, 2007
 */
public class SurrogatePokerPlayer extends PokerPlayer implements OnlineChangeListener {

    protected ServerConnection connection_;

    public SurrogatePokerPlayer(String name, int money, Color color, boolean isHuman, ServerConnection connection) {
        super(name, money, color, isHuman);
        connection_ = connection;
        connection_.addOnlineChangeListener(this);
    }


    /**
     * Update ourselves based on what was broadcast from the server.
     * @param cmd
     */
    public void handleServerUpdate(GameCommand cmd) {
       // if its a PLAYER_ACTION command, then set the action for this player
    }

    public PokerAction getAction(PokerController pc) {
        // needs to wait/block until the handleServerUpdate returns with an action
        assert false : "to do";
        return null;
    }
}
