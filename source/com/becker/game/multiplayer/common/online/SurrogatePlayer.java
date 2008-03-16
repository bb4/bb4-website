package com.becker.game.multiplayer.common.online;

import com.becker.game.common.Player;
import com.becker.game.common.online.GameCommand;
import com.becker.game.common.online.OnlineChangeListener;
import com.becker.game.common.online.ServerConnection;
import java.awt.*;

/**
 * On the server, all players are surrogates except for the robot players.
 * On the client, all players are surrogates except for the human player that is controlling that client.
 *
 * @author Barry Becker Date: Feb 3, 2007
 */
public  class SurrogatePlayer extends Player implements OnlineChangeListener {

    private static final long serialVersionUID = 1;
    
    protected ServerConnection connection_;
    protected Player player_;

    /**
     * @@ need to pass in a Player object instead of name, color, ishuman, money etc - this will be the player that we are a surrogate for.
     */
    public SurrogatePlayer(Player player, ServerConnection connection) {
        super(player.getName(), player.getColor(), player.isHuman());
        player_ = player;
        connection_ = connection;
        connection_.addOnlineChangeListener(this);
    }


    /**
     * Update ourselves based on what was broadcast to or from the server.
     * @param cmd
     */
    public void handleServerUpdate(GameCommand cmd) {
        assert false: "need to impl";
    }
            
}
