package com.becker.game.online;

import com.becker.game.common.*;

import java.io.*;
import java.util.*;

/**
 * A list of tables that players can sit down at in rder to play a game in an online environment.
 * 
 * @author Barry Becker Date: May 21, 2006
 */
public class OnlineGameTableList extends ArrayList<OnlineGameTable>
                                 implements Serializable {

    private static final long serialVersionUID = 1L;

    public OnlineGameTableList() {}

    /**
     * Change all occurrences of oldName to newName in the table list.
     */
    public void changeName(String oldName, String newName) {
        for (OnlineGameTable table : this)  {
            table.changeName(oldName, newName);
        }
    }


    /**
     * Remove this player from all tables. If last player at a table, remove the table too.
     * @param player to remove from all tables.
     */
    public void removePlayer(Player player) {
        Iterator<OnlineGameTable> it = this.iterator();
        while (it.hasNext()) {
            OnlineGameTable table = it.next();
            table.getPlayers().remove(player);
            if (table.hasNoHumanPlayers())  {
                it.remove(); // remove table if now empty.
                // and abort here since the player can not be at more than one table.
                break;
            }
        }
    }

    /**
     * Remove this player from all tables. If last human player at a table, remove the table too.
     * @param playerName to remove from all tables.
     */
    public void removePlayer(String playerName) {
        Iterator<OnlineGameTable> it = this.iterator();
        while (it.hasNext()) {
            OnlineGameTable table = it.next();
            // loop through the list of players and remove the player if the name matches
            Iterator<Player> pit = table.getPlayers().iterator();
            while (pit.hasNext()) {
                Player p = pit.next();
                if (p.getName().equals(playerName))  {
                    pit.remove();
                    break;
                }
            }
            if (table.hasNoHumanPlayers())  {
                it.remove(); // remove table if now empty.
                // and abort here since the player can not be at more than one table.
                break;
            }
        }
    }

    public boolean hasTableReadyToPlay() {
        Iterator<OnlineGameTable> it = this.iterator();
        while (it.hasNext()) {
            OnlineGameTable table = it.next();
            if (table.isReadyToPlay())
                return true;
        }
        return false;
    }



    public List<OnlineGameTable> getTablesReadyToPlay() {
        List<OnlineGameTable> readyTables = new ArrayList<OnlineGameTable>();
        Iterator<OnlineGameTable> it = this.iterator();
        while (it.hasNext()) {
            OnlineGameTable table = it.next();
            if (table.isReadyToPlay())
                readyTables.add(table);
        }
        return readyTables;
    }

    /**
     * @return the table which is ready to play and has playerName, otherwise return null of no tables like that.
     */
    public OnlineGameTable getTableReadyToPlay(String playerName) {
        Iterator<OnlineGameTable> it = this.iterator();
        while (it.hasNext()) {
            OnlineGameTable table = it.next();
            if (table.isReadyToPlay() && table.hasPlayer(playerName)) {
                return table;
            }
        }
        return null;
    }

    /**
     * Add a new player to specified table.
     */
    public void join(String tableName, Player newPlayer) {
        Iterator<OnlineGameTable> it = this.iterator();
        while (it.hasNext()) {
            OnlineGameTable table = it.next();
            if (table.getName().equals(tableName))  {
                table.addPlayer(newPlayer);
                return;
            }
        }
    }

    public String toString()  {
        StringBuilder bldr = new StringBuilder("Tables:\n");
        for (OnlineGameTable t : this) {
             bldr.append(t + "\n");
        }
        return bldr.toString();
    }
}
