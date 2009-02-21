package com.becker.game.multiplayer.common.online.ui;

import com.becker.game.common.*;
import com.becker.game.common.online.*;
import com.becker.game.multiplayer.common.*;

/**
 *
 * @author Barry Becker Date: Sep 23, 2006
 */
public abstract class MultiPlayerOnlineTable extends OnlineGameTable {


    public MultiPlayerOnlineTable(String name, Player initialPlayer, GameOptions options) {
        super(name, initialPlayer, options);
        int numRobots = ((MultiGameOptions) options).getNumRobotPlayers();
        for (int i=0; i<numRobots; i++) {
            addRobotPlayer(i);
        }
    }

    protected abstract void addRobotPlayer(int i);
}