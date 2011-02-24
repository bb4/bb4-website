package com.becker.game.multiplayer.common.online.ui;

import com.becker.game.common.GameOptions;
import com.becker.game.common.player.Player;
import com.becker.game.common.online.OnlineGameTable;
import com.becker.game.multiplayer.common.MultiGameOptions;

/**
 *
 * @author Barry Becker
 */
public abstract class MultiPlayerOnlineTable extends OnlineGameTable {


    protected MultiPlayerOnlineTable(String name, Player initialPlayer, GameOptions options) {
        super(name, initialPlayer, options);
        int numRobots = ((MultiGameOptions) options).getNumRobotPlayers();
        for (int i=0; i<numRobots; i++) {
            addRobotPlayer(i);
        }
    }

    protected abstract void addRobotPlayer(int i);
}
