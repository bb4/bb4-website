/** Copyright by Barry G. Becker, 2000-2011. Licensed under MIT License: http://www.opensource.org/licenses/MIT  */
package com.barrybecker4.game.multiplayer.common.online;

import com.barrybecker4.game.common.GameContext;
import com.barrybecker4.game.common.online.GameCommand;
import com.barrybecker4.game.common.online.server.IServerConnection;
import com.barrybecker4.game.common.online.OnlineChangeListener;
import com.barrybecker4.game.common.player.PlayerAction;
import com.barrybecker4.game.multiplayer.common.MultiGameController;
import com.barrybecker4.game.multiplayer.common.MultiGamePlayer;
import com.barrybecker4.game.multiplayer.common.MultiPlayerMarker;

/**
 * On the server, all players are surrogates except for the robot players.
 * On the client, all players are surrogates except for the human player that is controlling that client.
 *
 * @author Barry Becker
 */
public class SurrogateMultiPlayer extends MultiGamePlayer implements OnlineChangeListener {

    private MultiGamePlayer player;

    /** wait about 40 seconds for the player to move before timing out. */
    private static final int TIMEOUT_DURATION = 40000;


    /**
     * @param player
     * @param connection to the server so we can get updated actions.
     */
    public SurrogateMultiPlayer(MultiGamePlayer player, IServerConnection connection) {
        super(player.getName(), player.getColor(), player.isHuman());
        this.player = player;
        connection.addOnlineChangeListener(this);
    }

    /**
     * Update ourselves based on what was broadcast to or from the server.
     * @param cmd
     */
    public synchronized void handleServerUpdate(GameCommand cmd) {

        if (cmd.getName() == GameCommand.Name.DO_ACTION) {
            PlayerAction action = (PlayerAction) cmd.getArgument();
            if (action.getPlayerName().equals(getName())) {
                GameContext.log(0, "Setting surrogate(" + player.getName()
                        + ") action="+action + " on "+this+",  Thread=" + Thread.currentThread().getName());
                player.setAction(action);
                notifyAll();  // unblock the wait below
            }
        }
    }

    @Override
    public void setAction(PlayerAction action) {
        assert false : "must not set action directly on a surrogate";
    }

    /**
     *
     * @param controller
     * @return an action for this player. Block until the real player, for which we are a surrogate,
     *    has played and we have an  action to return.
     */
    @Override
    public synchronized PlayerAction getAction(MultiGameController controller) {

        try {

            long t1 = System.currentTimeMillis();
            // wait gives other threads time to execute until we receive a notify and can continue.
            System.out.println(player.getName() + " now waiting for surrogate action on "
                    + this + ",  Thread=" + Thread.currentThread().getName());
            wait(TIMEOUT_DURATION);
            if ((System.currentTimeMillis() - t1) > (TIMEOUT_DURATION - 10)) {
                  System.out.println("****** TIMEOUT! "+ player.getName() +" is waiting for someone to play.");
            }
            PlayerAction a = player.getAction(controller);
            float time = (float)(System.currentTimeMillis() - t1)/1000.0f;
            System.out.println("got action =" + a + " for " + player.getName() + " after " + time + "s   on "
                    + this + ",  Thread=" + Thread.currentThread().getName());
            return a;

        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * The player that we are representing (that is actually located somewhere else)
     * @return the specific game player backed by another player of the same type somewhere else.
     */
    public synchronized MultiGamePlayer getPlayer() {
        return player;
    }

    @Override
    public synchronized MultiPlayerMarker getPiece() {
        return player.getPiece();
    }

    @Override
    public boolean isSurrogate() {
        return true;
    }
}
