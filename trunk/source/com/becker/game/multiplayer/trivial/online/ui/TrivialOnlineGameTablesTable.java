package com.becker.game.multiplayer.trivial.online.ui;

import com.becker.game.multiplayer.trivial.*;
import com.becker.game.multiplayer.trivial.player.*;
import com.becker.game.multiplayer.trivial.online.*;
import com.becker.game.multiplayer.common.*;
import com.becker.game.multiplayer.common.online.ui.*;
import com.becker.game.common.online.*;
import com.becker.game.common.*;

import com.becker.ui.table.TableButtonListener;
import java.awt.event.*;

/**
 * Show Trivial specific game options in the table row.
 *
 * @author Barry Becker Date: May 13, 2006
 */
public class TrivialOnlineGameTablesTable extends MultiPlayerOnlineGameTablesTable {

    
    private static final String[] TRIVIAL_COLUMN_NAMES = {JOIN, MIN_NUM_PLAYERS, PLAYER_NAMES};

    /**
     *
     * @param actionListener  that gets called when the player selects a different table to join.
     */
    public TrivialOnlineGameTablesTable(TableButtonListener tableButtonListener) {
         super(TRIVIAL_COLUMN_NAMES, tableButtonListener);
    }


    @Override
    protected Object[] getRowObject(OnlineGameTable onlineTable, boolean localPlayerAtTable)
    {
        Object d[] = new Object[getNumColumns()];
        // false if active player is in this table.
        // You cannot join a table you are already at
        d[JOIN_INDEX] = !localPlayerAtTable;
        d[NUM_PLAYERS_INDEX] = onlineTable.getNumPlayersNeeded();
        d[PLAYER_NAMES_INDEX] = onlineTable.getPlayerNames();
        TrivialOptions options = (TrivialOptions) onlineTable.getGameOptions();
        return d;
    }

    @Override
    public OnlineGameTable createOnlineTable(String ownerPlayerName, MultiGameOptions options) {
        Player player = createPlayerForName(ownerPlayerName);
        return new OnlineTrivialTable(getUniqueName(), player, options);
    }

    @Override
    public Player createPlayerForName(String playerName) {
        return new TrivialHumanPlayer(playerName,  getRandomColor());
    }

}
