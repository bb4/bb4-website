package com.becker.game.common.online;

import com.becker.game.common.*;

import java.util.*;
import java.io.*;

/**
 * Some number of players sitting around a virtual game table online.
 *
 * @author Barry Becker Date: May 13, 2006
 */
public class OnlineGameTable implements Serializable {


    private static final int serialVersionUID = 1;

    // the name of the virtual online table.
    private String name_;

    private List<Player> players_;

    // @@ also pass in game options


    public OnlineGameTable(String name, Player initialPlayer) {
        this(name, new Player[] {initialPlayer});
    }

    public OnlineGameTable(String name, Player[] initialPlayers) {
        name_ = name;
        players_ = new LinkedList<Player>();
        for (Player p : initialPlayers) {
            players_.add(p);
        }
    }

    public String getName() {
        return name_;
    }

    public Player[] getPlayers() {
        return (Player[]) players_.toArray(new Player[players_.size()]);
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

    public void addPlayer(Player player) {
        players_.add(player);
    }

    public void removePlayer(Player player) {
        players_.remove(player);
    }

    public String toString()
    {
        StringBuilder buf = new StringBuilder(20);
        buf.append("Name: " + name_ + '\n');
        buf.append("Players:\n"+ players_ + '\n');
        return buf.toString();
    }

}
