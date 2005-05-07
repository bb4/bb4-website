package com.becker.game.multiplayer.galactic.ui;

import com.becker.game.multiplayer.galactic.*;
import com.becker.game.multiplayer.common.ui.PlayerTableModel;
import com.becker.game.multiplayer.common.ui.SummaryTable;
import com.becker.game.common.*;
import com.becker.ui.ColorCellRenderer;

import javax.swing.*;
import javax.swing.JTable;
import javax.swing.table.*;
import java.util.*;
import java.util.List;
import java.awt.*;


/**
 * Shows a list of th remaining players stats at the end of the game.
 * None of the cells are editable.
 * @see GalacticPlayer
 *
 * @author Barry Becker
 */
public class GalacticSummaryTable extends SummaryTable
{

    private static final int NUM_PLANETS_INDEX = 2;
    private static final int SHIPS_INDEX = 3;
    private static final int PRODUCTION_INDEX = 4;   // total over all owned planets

    private static final String NAME = GameContext.getLabel("NAME");
    private static final String COLOR = GameContext.getLabel("COLOR");
    private static final String NUM_PLANETS = GameContext.getLabel("NUM_PLANETS");
    private static final String NUM_SHIPS = GameContext.getLabel("NUM_SHIPS");
    private static final String PRODUCTION = GameContext.getLabel("PRODUCTION");

    private static String[] columnNames_ =  {NAME,
                                             COLOR,
                                             NUM_PLANETS,
                                             NUM_SHIPS,
                                             PRODUCTION };

    /**
     * constructor
     * @param players to initializet the rows in the table with.
     */
    public GalacticSummaryTable(Player[] players)
    {
        super(players, columnNames_);
    }


    /**
     * add a row based on a player object
     * @param player to add
     */
    protected void addRow(Player player)
    {
        GalacticPlayer p = (GalacticPlayer)player;
        Object d[] = new Object[getNumColumns()];
        List planets = Galaxy.getPlanets(p);
        // sum the num ships and productions

        d[NAME_INDEX] = player.getName();
        d[COLOR_INDEX ] = player.getColor();
        d[NUM_PLANETS_INDEX] = new Integer(planets.size());
        d[SHIPS_INDEX] =  new Integer(p.getTotalNumShips());
        d[PRODUCTION_INDEX] = new Integer(p.getTotalProductionCapacity());
        getModel().addRow(d);
    }
}
