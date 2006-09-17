package com.becker.game.online;

import com.becker.game.common.*;

import java.util.*;
import java.io.*;

/**
 * Some number of players sitting around a virtual game table online.
 * Each table has a name, set of Players and other game specific properties.
 *
 * @author Barry Becker Date: May 13, 2006
 */
public class OnlineGameTable implements Serializable {

    private static final int serialVersionUID = 1;

    // the name of the virtual online table.
    private String name_;

    // the player who created this table, even if they are not sitting here anymore.
    private Player owner_;

    // list of players currently sitting at the table.
    private List<Player> players_;

    private GameOptions gameOptions_;

    // most recent player to join the table.
    private Player newestPlayer_;


    public OnlineGameTable(String name, Player initialPlayer, GameOptions options) {
        this(name, initialPlayer, new Player[] {initialPlayer}, options);
    }

    public OnlineGameTable(String name, Player owner, Player[] initialPlayers, GameOptions options) {
        name_ = name;
        owner_ = owner;
        newestPlayer_ = owner;
        players_ = new LinkedList<Player>();
        gameOptions_ = options;
        for (Player p : initialPlayers) {
            players_.add(p);
        }
    }

    /**
     * @return the name of the table.
     */
    public String getName() {
        return name_;
    }

    public void setName(String name) {
        name_ = name;
    }

    public List<Player> getPlayers() {
        return players_;
    }

    public Player[] getPlayersAsArray() {
        return players_.toArray(new Player[players_.size()]);
    }

    public Player getOwner() {
        return owner_;
    }

    public Player getNewestPlayer() {
        return newestPlayer_;
    }

    public int getNumPlayersNeeded() {
        return gameOptions_.getMaxNumPlayers();
    }

    public GameOptions getGameOptions() {
        return gameOptions_;
    }

    /**
     * @return the list of players at the table in a comman delimited list.
     */
    public String getPlayerNames() {
        StringBuffer buf = new StringBuffer("");
        if (players_.isEmpty()) {
            return "-";
        }
        for (Player p : players_) {
            buf.append(p.getName() + ", ");
        }
        return buf.substring(0, buf.length() - 2);
    }

    public void changeName(String oldName, String newName) {
        for (Player p : players_) {
            if (p.getName().equals(oldName)) {                
                p.setName(newName);
            }
        }
    }

    public void addPlayer(Player player) {
        players_.add(player);
        newestPlayer_ = player;
    }

    public void removePlayer(Player player) {
        players_.remove(player);
        if (player.equals(newestPlayer_))
            newestPlayer_ = null;
    }

    public boolean hasPlayer(String playerName) {
        for (Player p : players_) {
            if (p.getName().equals(playerName)) {
                return true;
            }
        }
        return false;
    }

    public String toString()
    {
        StringBuilder buf = new StringBuilder(20);
        buf.append("Name: " + name_ + '\n');
        buf.append("Players:\n"+ players_ + '\n');
        return buf.toString();
    }

}
