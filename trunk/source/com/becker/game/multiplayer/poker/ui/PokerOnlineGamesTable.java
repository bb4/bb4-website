package com.becker.game.multiplayer.poker.ui;

import com.becker.game.multiplayer.online.ui.*;
import com.becker.game.multiplayer.poker.*;
import com.becker.game.multiplayer.poker.online.*;
import com.becker.game.online.*;

import java.awt.*;
import java.util.*;

/**
 * @@ need to have a column for chiper per player, and bet limit.
 *
 * @author Barry Becker Date: May 13, 2006
 */
 public class PokerOnlineGamesTable extends MultiPlayerOnlineGamesTable {

    private static final Random RANDOM = new Random();

    public PokerOnlineGamesTable(OnlineGameTable[] tables) {
         super(tables);
    }

    protected void addRow(OnlineGameTable onlineTable) {
        Object d[] = new Object[getNumColumns()];
        d[JOIN_INDEX] = true; // true if active player is in this table
        d[TABLE_NAME_INDEX] = onlineTable.getName();
        d[PLAYER_NAMES_INDEX] = onlineTable.getPlayerNames();
        //d[CASH_INDEX] = DEFAULT_CASH_AMOUNT; //p.getCash();

        getModel().addRow(d);

        selectedTable_ = onlineTable;
    }


    public OnlineGameTable createOnlineTable(String initialPlayerName) {
        // we should pass in a strucutre that contains the initial game options.
        PokerPlayer player = new PokerHumanPlayer(initialPlayerName, 100, getRandomColor());
        OnlinePokerTable gameTable = new OnlinePokerTable(getUniqueName(), player);
        return gameTable;
    }

    private static Color getRandomColor() {

        int r = RANDOM.nextInt(256);
        return new Color(r, 255 -r, RANDOM.nextInt(256));
    }

}
