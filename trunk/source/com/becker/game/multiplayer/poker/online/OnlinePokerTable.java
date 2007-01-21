package com.becker.game.multiplayer.poker.online;

import com.becker.game.common.*;
import com.becker.game.multiplayer.poker.player.*;
import com.becker.game.multiplayer.poker.*;
import com.becker.game.multiplayer.common.online.ui.*;

import java.awt.*;

/**
 * @author Barry Becker Date: May 13, 2006
 */
public class OnlinePokerTable extends MultiPlayerOnlineTable {

    public OnlinePokerTable(String name, Player initialPlayer, GameOptions options) {
        super(name, initialPlayer, options);
    }

    /**
     * add robot player i to the table.
     */
    protected void addRobotPlayer(int i) {

        int size = getPlayers().size();
        Color newColor = PokerPlayer.getNewPlayerColor(getPlayers().toArray(new Player[size]));

        PokerOptions options = (PokerOptions) getGameOptions();
        String name = "Robot " + (i+1);
        PokerPlayer robot = PokerRobotPlayer.getRandomRobotPlayer(name, options.getInitialCash(), newColor);
        this.addPlayer(robot);
    }

}
