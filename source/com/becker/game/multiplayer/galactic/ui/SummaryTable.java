package com.becker.game.multiplayer.galactic.ui;

import com.becker.game.multiplayer.galactic.*;
import com.becker.game.common.*;
import com.becker.ui.ColorCellRenderer;

import javax.swing.*;
import javax.swing.JTable;
import javax.swing.table.*;
import java.util.*;
import java.util.List;
import java.awt.*;


/**
 * Shows a list of th eremaining players stats at the end of the game.
 * None of the cells are editable.
 * @see GalacticPlayer
 *
 * @author Barry Becker
 */
public class SummaryTable
{
    private JTable table_;

    private static final int NAME_INDEX = 0;
    private static final int COLOR_INDEX = 1;
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

    private static int NUM_COLS = columnNames_.length;


    /**
     * constructor
     * @param players to initializet the rows in the table with.
     */
    public SummaryTable(GalacticPlayer[] players)
    {
        initializeTable();

        for (int i=0; i<players.length; i++)  {
            GalacticPlayer p = players[i];
            addRow(p);
        }

    }

    private void initializeTable()
    {
        TableModel m = new PlayerTableModel(columnNames_, 0, false);
        table_ = new JTable(m);

        TableColumn colColumn = table_.getColumn(COLOR);
        colColumn.setCellRenderer(new ColorCellRenderer());
    }


    public JTable getTable()
    {
        return table_;
    }


    private PlayerTableModel getModel()
    {
        return (PlayerTableModel)table_.getModel();
    }

    /**
     * add a row based on a player object
     * @param player to add
     */
    private void addRow(GalacticPlayer player)
    {
        Object d[] = new Object[NUM_COLS];
        List planets = Galaxy.getPlanets(player);
        // sum the num ships and productions

        d[NAME_INDEX] = player.getName();
        d[COLOR_INDEX ] = player.getColor();
        d[NUM_PLANETS_INDEX] = new Integer(planets.size());
        d[SHIPS_INDEX] =  new Integer(player.getTotalNumShips());
        d[PRODUCTION_INDEX] = new Integer(player.getTotalProductionCapacity());
        getModel().addRow(d);
    }
}
