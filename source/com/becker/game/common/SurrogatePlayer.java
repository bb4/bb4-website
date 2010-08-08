package com.becker.game.common;

import com.becker.game.common.online.GameCommand;
import com.becker.game.common.online.IServerConnection;
import com.becker.game.common.online.OnlineChangeListener;

/**
 * On the server, all players are surrogates except for the robot players.
 * On the client, all players are surrogates except for the human player that is controlling that client.
 *
 * @author Barry Becker
 */
public  class SurrogatePlayer extends Player implements OnlineChangeListener {

    protected IServerConnection connection_;
    protected Player player_;

    // wait about 10 seconds for the player to move before timing out.
    private static final int TIMEOUT_DURATION = 40000;


    /**
     * @param player
     * @param connection to the server so we can get updated actions.
     */
    public SurrogatePlayer(Player player, IServerConnection connection) {
        super(player.getName(), player.getColor(), player.isHuman());
        player_ = player;
        connection_ = connection;
        connection_.addOnlineChangeListener(this);
    }

    /**
     * Update ourselves based on what was broadcast to or from the server.
     * @param cmd
     */
    public synchronized void handleServerUpdate(GameCommand cmd) {

        if (cmd.getName() == GameCommand.Name.DO_ACTION) {
            PlayerAction action = (PlayerAction) cmd.getArgument();
            /// @@ need to do something for regular players here.
        }
    }
          
    /**
     * The player that we are representing (that is actually located somewhere else)
     * @return the specific game player backed by another player of the same type somewhere else.
     */
    public Player getPlayer() {
        return player_;
    }


    @Override
    public boolean isSurrogate() {
        return true;
    }
}