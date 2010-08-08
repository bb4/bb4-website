package com.becker.game.common;

import com.becker.game.common.online.IServerConnection;

import java.util.ArrayList;
import java.util.Random;

/**
 * A list of players.
 *
 *  @author Barry Becker
 */
public class PlayerList extends ArrayList<Player> {

    /** Make sure that the program runs in a reproducible way by always starting from the same random seed. */
    protected static final Random RANDOM = new Random(1);


    /**
     * Construct set of players
     */
    public PlayerList() {}

    /**
     *  @return the player that goes first.
     */
    public Player getFirstPlayer()
    {
        return get(0);
    }

    /**
     *  @return the player that goes first.
     */
    public Player getPlayer1()
    {
        return get(0);
    }

    /**
     *  @return the player that goes second.
     */
    public Player getPlayer2()
    {
        return get(1);
    }

    /**
     * @return  number of active players.
     */
    public int getNumPlayers()
    {
        return size();
    }


    /**
     * @return true if any of the players have won.
     */
    public boolean anyPlayerWon() {
        for (Player p : this) {
            if (p.hasWon()) {
                return true;
            }
        }
        return false;
    }

    /**
     * @return true if there are only human players
     */
    public boolean allPlayersHuman()
    {
       for (Player player : this)  {
           if (!player.isHuman()) return false;
       }
       return true;
    }

    /**
     * @return true if there are only computer players
     */
    public boolean allPlayersComputer()
    {
       for (Player player : this)  {
           if (player.isHuman()) return false;
       }
       return true;
    }
}