package com.becker.game.multiplayer.common;

import com.becker.game.common.*;

/**
 * @author Barry Becker Date: Sep 9, 2006
 */
public class MultiGameOptions extends GameOptions {

    private static final int DEFAULT_PLAYER_LIMIT = 8;
    private static final int DEFAULT_NUM_ROBOT_PLAYERS = 1;

    // no more than this many allowed at the table.
    private int maxNumPlayers_ = DEFAULT_PLAYER_LIMIT;
    // number of robot players at the table.
    // You can change this in the new game dlg if stand alone.
    private int numRobotPlayers_ = DEFAULT_NUM_ROBOT_PLAYERS;


    public MultiGameOptions() {
          this(DEFAULT_PLAYER_LIMIT, DEFAULT_NUM_ROBOT_PLAYERS);
    }


    public MultiGameOptions(int maxNumPlayers, int numRobotPlayers) {
         maxNumPlayers_ = maxNumPlayers;
         numRobotPlayers_ = numRobotPlayers;
    }

    /**
     * usually 2 but we allow for override.
     */
    public int getMinNumPlayers() {
        return 2;
    }

    @Override
    public int getMaxNumPlayers() {
        return maxNumPlayers_;
    }

    /**
     * You wont be able to add more than this many players.
     * @param playerLimit
     */
    public void setMaxNumPlayers(int playerLimit) {
        maxNumPlayers_ = playerLimit;
    }

    public int getNumRobotPlayers() {
        return numRobotPlayers_;
    }

    public void setNumRobotPlayers(int numRobotPlayers) {
        numRobotPlayers_ = numRobotPlayers;
    }

    /**
     * Check constraints on options to verify validity.
     * @return  null if no errors, return error messages if constraints violated.
     */
    @Override
    public String testValidity() {
        String msgs = "";
        if (getNumRobotPlayers() > getMaxNumPlayers())  {
            msgs += "The number of robot players cannot can exceed the total number of players.";
        }
        return (msgs.length() > 0)? msgs : null;
    }
}
