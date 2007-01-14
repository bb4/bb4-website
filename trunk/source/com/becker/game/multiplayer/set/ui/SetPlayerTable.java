package com.becker.game.multiplayer.set.ui;


import com.becker.game.common.*;
import com.becker.game.multiplayer.common.ui.*;
import com.becker.game.multiplayer.set.*;

import javax.swing.table.*;
import java.awt.*;


/**
 * GalacticPlayerTable contains a list of players.
 * All the cells are editable.
 * It is initialized with a list of Players and returns a list of Players.
 * @see com.becker.game.multiplayer.set.SetPlayer
 *
 * @author Barry Becker
 */
public class SetPlayerTable extends PlayerTable
{

    private static String[] setColumnNames_ =  {
       NAME,
       COLOR,
       HUMAN
    };


    /**
     * constructor                                                                           
     * @param players to initializet the rows in the table with.
     */
    public SetPlayerTable(SetPlayer[] players)
    {
        super(players, setColumnNames_);
    }


    /**
     * @return  the players represented by rows in the table
     */
    public Player[] getPlayers()
    {
        TableModel model = table_.getModel();
        int nRows = model.getRowCount();
        Player[] players = new SetPlayer[nRows];
        for (int i=0; i<nRows; i++) {
            players[i] = SetPlayer.createSetPlayer(
                                 (String)model.getValueAt(i, NAME_INDEX),
                                 (Color)model.getValueAt(i, COLOR_INDEX),
                                 ((Boolean)model.getValueAt(i, HUMAN_INDEX)));
        }
        return players;
    }


    /**
     * add a row based on a player object
     * @param player to add
     */
    protected void addRow(Object player)
    {
        Player p = (Player) player;
        Object d[] = new Object[getNumColumns()];
        d[NAME_INDEX] = p.getName();
        d[COLOR_INDEX] = p.getColor();
        d[HUMAN_INDEX] = p.isHuman();
        getPlayerModel().addRow(d);
    }

    protected Player createPlayer() {
        int ct = table_.getRowCount();
        Color newColor = SetPlayer.getNewPlayerColor(getPlayers());
        SetPlayer player = SetPlayer.createSetPlayer("Player "+(ct+1), newColor, true);
        return player;
    }

}
