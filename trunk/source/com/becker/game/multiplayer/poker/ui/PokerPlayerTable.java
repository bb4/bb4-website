package com.becker.game.multiplayer.poker.ui;


import com.becker.game.common.*;
import com.becker.game.multiplayer.poker.PokerPlayer;
import com.becker.game.multiplayer.common.ui.*;
import com.becker.ui.ColorCellEditor;
import com.becker.ui.ColorCellRenderer;

import javax.swing.*;
import javax.swing.JTable;
import javax.swing.event.ListSelectionListener;
import javax.swing.table.*;
import java.util.*;
import java.util.List;
import java.awt.*;


/**
 * GalacticPlayerTable contains a list of players.
 * All the cells are editable.
 * It is initialized with a list of Players and returns a list of Players.
 * @see com.becker.game.multiplayer.poker.PokerPlayer
 *
 * @author Barry Becker
 */
public class PokerPlayerTable extends PlayerTable
{

    private static final int CASH_INDEX = 3;

    private static final String CASH = GameContext.getLabel("CASH");

    private static String[] pokerColumnNames_ =  {
       NAME,
       COLOR,
       HUMAN,
       CASH
    };

    private static final int DEFAULT_CASH_AMOUNT = 100;


    /**
     * constructor
     * @param players to initializet the rows in the table with.
     */
    public PokerPlayerTable(PokerPlayer[] players)
    {
        super(players, pokerColumnNames_);
    }


    /**
     * @return  the players represented by rows in the table
     */
    public Player[] getPlayers()
    {
        TableModel model = table_.getModel();
        int nRows = model.getRowCount();
        Player[] players = new PokerPlayer[nRows];
        for (int i=0; i<nRows; i++) {

            players[i] = PokerPlayer.createPokerPlayer(
                                    (String)model.getValueAt(i, NAME_INDEX),
                                    ((Integer)model.getValueAt(1, CASH_INDEX)).intValue(),
                                    (Color)model.getValueAt(i, COLOR_INDEX),
                                    ((Boolean)model.getValueAt(i, TYPE_INDEX)).booleanValue());
        }
        return players;
    }


    /**
     * add a row based on a player object
     * @param player to add
     */
    protected void addRow(Player player)
    {
        PokerPlayer p = (PokerPlayer)player;
        Object d[] = new Object[getNumColumns()];
        d[NAME_INDEX] = player.getName();
        d[COLOR_INDEX ] = player.getColor();
        d[CASH_INDEX] = p.getCash();
        d[TYPE_INDEX] = new Boolean(player.isHuman());

        //data[i] = d;
        getModel().addRow(d);
    }

    protected Player createPlayer() {
        int ct = table_.getRowCount();
        Color newColor = PokerPlayer.getNewPlayerColor((PokerPlayer[])getPlayers());
        PokerPlayer player = PokerPlayer.createPokerPlayer(
                                             "Foo "+(ct+1), DEFAULT_CASH_AMOUNT, newColor, true);

        return player;
    }


    /**
     * remove the selected rows from the table
     */
    public void removeSelectedRows()
    {
        int nSelected = table_.getSelectedRowCount();
        int[] selectedRows = table_.getSelectedRows();
        for (int i=nSelected-1; i>=0; i--) {
            int selRow = selectedRows[i];
            deletedRows_.add(getModel().getDataVector().elementAt(selRow));
            getModel().removeRow(selRow);
        }
    }
}
