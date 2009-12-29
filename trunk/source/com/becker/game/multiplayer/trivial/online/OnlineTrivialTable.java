package com.becker.game.multiplayer.trivial.online;

import com.becker.game.common.*;
import com.becker.game.multiplayer.common.MultiGamePlayer;
import com.becker.game.multiplayer.trivial.player.*;
import com.becker.game.multiplayer.trivial.*;
import com.becker.game.multiplayer.common.online.ui.*;

import java.awt.*;

/**
 * @author Barry Becker Date: May 13, 2006
 */
public class OnlineTrivialTable extends MultiPlayerOnlineTable {
    
    private static final long serialVersionUID = 1;

    public OnlineTrivialTable(String name, Player initialPlayer, GameOptions options) {
        super(name, initialPlayer, options);
    }

    /**
     * add robot player i to the table.
     */
    @Override
    protected void addRobotPlayer(int i) {

        String name = "Robot " + (i+1);
        Color newColor = MultiGamePlayer.getNewPlayerColor(getPlayers());
       
        Player robot = new TrivialRobotPlayer(name, newColor);
        this.addPlayer(robot);
    }  
}
