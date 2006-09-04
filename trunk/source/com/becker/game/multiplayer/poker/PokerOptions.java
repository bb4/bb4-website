package com.becker.game.multiplayer.poker;

import com.becker.game.common.*;

/**
 * @author Barry Becker Date: Sep 2, 2006
 */
public class PokerOptions extends GameOptions{


    private static final int DEFAULT_ANTE = 2;
    private static final int DEFAULT_MAX_ABS_RAISE = 50;
    private static final int DEFAULT_INITIAL_CHIPS = 100;
    private static final int DEFAULT_PLAYER_LIMIT = 5;
    private static final int DEFAULT_NUM_ROBOT_PLAYERS = 1;

    // starting bid
    private int ante_ = DEFAULT_ANTE;
    // can't raise by more than this
    private int maxAbsoluteRaise_ = DEFAULT_MAX_ABS_RAISE;
    // default starting chips for each player
    private int initialChips_ = DEFAULT_INITIAL_CHIPS;
    // no more than this many allowed at the table.
    private int playerLimit_ = DEFAULT_PLAYER_LIMIT;
    // number of robot players at the table.
    // You can change this in the new game dlg if stand alone.
    private int numRobotPlayers_ = DEFAULT_NUM_ROBOT_PLAYERS;


    /**
     * this constructor uses all default values.
     */
    public PokerOptions() {}

    /**
     * User specified valeus for options.
     */
    public PokerOptions(int ante, int maxAbsoluteRaise, int initialChips, int playerLimit) {
        setAnte(ante);
        setMaxAbsoluteRaise(maxAbsoluteRaise);
        setInitialChips(initialChips);
        setPlayerLimit(playerLimit);
    }


    public int getAnte() {
        return ante_;
    }

    public void setAnte(int ante) {
        this.ante_ = ante;
    }

    public int getMaxAbsoluteRaise() {
        return maxAbsoluteRaise_;
    }

    public void setMaxAbsoluteRaise(int maxAbsoluteRaise) {
        this.maxAbsoluteRaise_ = maxAbsoluteRaise;
    }

    public int getInitialChips() {
        return initialChips_;
    }

    public void setInitialChips(int initialChips) {
        this.initialChips_ = initialChips;
    }

    public int getPlayerLimit() {
        return playerLimit_;
    }

    public void setPlayerLimit(int playerLimit) {
        this.playerLimit_ = playerLimit;
    }

    public int getNumRobotPlayers() {
        return numRobotPlayers_;
    }

    public void setNumRobotPlayers(int numRobotPlayers) {
        this.numRobotPlayers_ = numRobotPlayers;
    }
}
