package com.becker.game.multiplayer.set.ui;

import com.becker.game.common.*;
import com.becker.game.multiplayer.common.ui.*;
import com.becker.game.multiplayer.set.*;
import java.util.List;

import javax.swing.*;


/**
 * Shows a list of the players stats.
 * None of the cells are editable.
 * @see SetPlayer
 *
 * @author Barry Becker
 */
public class SetSummaryTable extends SummaryTable
{

    private static final int NUM_SETS_INDEX = 2;

    private static final String NUM_SETS = GameContext.getLabel("NUM_SETS");

    private static final String[] COLUMN_NAMES =  {NAME,
                                                  COLOR,
                                                  NUM_SETS};

    /**
     * constructor
     * @param players to initializet the rows in the table with.
     */
    public SetSummaryTable(List<? extends Player> players)
    {
        super(players, COLUMN_NAMES);
        getTable().setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
    }


    /**
     * add a row based on a player object
     * @param player to add
     */
    protected void addRow(Object player)
    {
        SetPlayer p = (SetPlayer)player;
        Object d[] = new Object[getNumColumns()];

        d[NAME_INDEX] = p.getName();
        d[COLOR_INDEX ] = p.getColor();
        d[NUM_SETS_INDEX] = "" + p.getNumSetsFound();

        getPlayerModel().addRow(d);
    }
}
