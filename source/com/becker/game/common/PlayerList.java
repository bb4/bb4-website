package com.becker.game.common;

import com.becker.game.common.online.IServerConnection;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Random;

/**
 * This is an abstract base class for a Game Controller.
 * It contains the key logic for n player games.
 * It is a much more general form of the TwoPlayerController subclass.
 *
 * Instance of this class process requests from the GameViewer.
 *
 *  @author Barry Becker
 */
public class PlayerList extends ArrayList<Player> {


    /** Make sure that the program runs in a reproducible way by always starting from the same random seed. */
    protected static final Random RANDOM = new Random(1);


    /**
     * Construct set of players
     */
    public PlayerList() {
    }


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
    public Player getSecondPlayer()
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