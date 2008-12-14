package com.becker.game.multiplayer.trivial.ui;


import com.becker.game.common.*;
import com.becker.game.multiplayer.common.ui.*;
import com.becker.game.multiplayer.trivial.player.*;
import java.awt.Color;
import java.util.ArrayList;
import java.util.List;

import javax.swing.table.*;


/**
 * contains a list of players.
 * All the cells are editable.
 * It is initialized with a list of Players and returns a list of Players
 *
 * @author Barry Becker
 */
public class TrivialPlayerTable extends PlayerTable
{

    private static String[] trivialColumnNames_ =  {
       NAME,
       COLOR,
       HUMAN
    };


    /**
     * constructor
     * @param players to initializet the rows in the table with.
     */
    public TrivialPlayerTable(List<TrivialPlayer> players)
    {
        super(players, trivialColumnNames_);
    }


    /**
     * @return  the players represented by rows in the table
     */
    public List<? extends Player> getPlayers()
    {
        TableModel model = table_.getModel();
        int nRows = model.getRowCount();
        List<TrivialPlayer> players = new ArrayList<TrivialPlayer>(nRows);
        for (int i = 0; i < nRows; i++) {
            players.add( TrivialPlayer.createTrivialPlayer(
                                    (String) model.getValueAt(i, NAME_INDEX),                              
                                    (Color) model.getValueAt(i, COLOR_INDEX),
                                    ((Boolean) model.getValueAt(i, HUMAN_INDEX))));
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
        Color newColor = TrivialPlayer.getNewPlayerColor(getPlayers());
        TrivialPlayer player = TrivialPlayer.createTrivialPlayer(
                                             "Robot "+(ct+1), newColor, true);

        return player;
    }
}
