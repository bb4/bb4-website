/** Copyright by Barry G. Becker, 2000-2011. Licensed under MIT License: http://www.opensource.org/licenses/MIT  */
package com.barrybecker4.game.common.player;

import com.barrybecker4.game.common.online.GameCommand;
import com.barrybecker4.game.common.online.OnlineChangeListener;
import com.barrybecker4.game.common.online.server.IServerConnection;

/**
 * On the server, all players are surrogates except for the robot players.
 * On the client, all players are surrogates except for the human player that is controlling that client.
 *
 * @author Barry Becker
 */
public  class SurrogatePlayer extends Player implements OnlineChangeListener {

    private Player player_;


    /**
     * @param player
     * @param connection to the server so we can get updated actions.
     */
    public SurrogatePlayer(Player player, IServerConnection connection) {
        super(player.getName(), player.getColor(), player.isHuman());
        player_ = player;
        connection.addOnlineChangeListener(this);
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
    @Override
    public Player getActualPlayer() {
        return player_;
    }


    @Override
    public boolean isSurrogate() {
        return true;
    }
}