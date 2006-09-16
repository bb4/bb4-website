package com.becker.game.multiplayer.poker.ui;

import com.becker.game.multiplayer.online.ui.*;
import com.becker.game.multiplayer.poker.*;
import com.becker.game.multiplayer.poker.player.*;
import com.becker.game.multiplayer.poker.online.*;
import com.becker.game.multiplayer.common.*;
import com.becker.game.online.*;
import com.becker.game.common.*;

import java.awt.*;
import java.awt.event.*;
import java.util.*;

/**
 * Show poker specific game options in the table row.
 *
 * @author Barry Becker Date: May 13, 2006
 */
 public class PokerOnlineGameTablesTable extends MultiPlayerOnlineGameTablesTable {

    private static final Random RANDOM = new Random();

    private static final int ANTE_INDEX = NUM_BASE_COLUMNS;
    private static final int MAX_RAISE_INDEX = NUM_BASE_COLUMNS + 1;
    private static final int INITIAL_CASH_INDEX = NUM_BASE_COLUMNS + 2;

    private static final String ANTE = GameContext.getLabel("ANTE");
    private static final String MAX_RAISE = GameContext.getLabel("MAX_RAISE");
    private static final String INITIAL_CASH = GameContext.getLabel("INITIAL_CASH");


    /**
     *
     * @param actionListener  that gets called when the player selects a different table to join.
     */
    public PokerOnlineGameTablesTable(ActionListener actionListener) {
         super(actionListener);
    }


    protected Object[] getRowObject(OnlineGameTable onlineTable, boolean localPlayerAtTable)
    {
        Object d[] = new Object[getNumColumns()];
        // false if active player is in this table.
        // You cannot join a table you are already at
        d[JOIN_INDEX] = !localPlayerAtTable;
        d[TABLE_NAME_INDEX] = onlineTable.getName();
        d[NUM_PLAYERS_INDEX] = onlineTable.getNumPlayersNeeded();
        d[PLAYER_NAMES_INDEX] = onlineTable.getPlayerNames();
        PokerOptions options = (PokerOptions) onlineTable.getGameOptions();
        d[ANTE_INDEX] = options.getAnte();
        d[MAX_RAISE_INDEX] = options.getMaxAbsoluteRaise();
        d[INITIAL_CASH_INDEX] = options.getInitialCash();
        return d;
    }


    public OnlineGameTable createOnlineTable(String ownerPlayerName, MultiGameOptions options) {
        Player player = createPlayerForName(ownerPlayerName);
        OnlinePokerTable gameTable = new OnlinePokerTable(getUniqueName(), player, options);
        return gameTable;
    }

    public Player createPlayerForName(String playerName) {
        return new PokerHumanPlayer(playerName, 100, getRandomColor());
    }

    private static Color getRandomColor() {

        int r = RANDOM.nextInt(256);
        return new Color(r, 255 - r, RANDOM.nextInt(256));
    }

    protected String[] getColumnNames() {
        return new String[] {JOIN, TABLE_NAME, MAX_NUM_PLAYERS, PLAYER_NAMES, ANTE, MAX_RAISE, INITIAL_CASH};
    }

    protected int getNumColumns() {
        return NUM_BASE_COLUMNS + 3;
    }

}
