package com.becker.game.multiplayer.common.ui;

import com.becker.game.common.*;
import com.becker.ui.*;

import javax.swing.*;
import javax.swing.table.*;


/**
 * Shows a list of th remaining players stats at the end of the game.
 * None of the cells are editable.
 * @see com.becker.game.multiplayer.galactic.GalacticPlayer
 *
 * @author Barry Becker
 */
public abstract class SummaryTable
{
    protected JTable table_;

    protected static final int NAME_INDEX = 0;
    protected static final int COLOR_INDEX = 1;


    protected static final String NAME = GameContext.getLabel("NAME");
    protected static final String COLOR = GameContext.getLabel("COLOR");


    protected String[] columnNames_;
    /**
     * constructor
     * @param players to initializet the rows in the table with.
     */
    public SummaryTable(Player[] players, String[] columnNames)
    {
        initializeTable(columnNames);

        for (int i=0; i<players.length; i++)  {
            Player p = players[i];
            addRow(p);
        }
    }

    protected void initializeTable(String[] columnNames)
    {
        columnNames_ = columnNames;
        TableModel m = new PlayerTableModel(columnNames, 0, false);
        table_ = new JTable(m);

        TableColumn colColumn = table_.getColumn(COLOR);
        colColumn.setCellRenderer(new ColorCellRenderer());
    }


    public JTable getTable()
    {
        return table_;
    }


    protected PlayerTableModel getModel()
    {
        return (PlayerTableModel)table_.getModel();
    }

    protected int getNumColumns()  {
        return columnNames_.length;
    }

    /**
     * add a row based on a player object
     * @param player to add
     */
    protected abstract void addRow(Player player);

}
