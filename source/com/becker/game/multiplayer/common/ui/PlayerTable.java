package com.becker.game.multiplayer.common.ui;

import com.becker.game.common.*;
import com.becker.ui.ColorCellEditor;
import com.becker.ui.ColorCellRenderer;

import javax.swing.*;
import javax.swing.JTable;
import javax.swing.event.ListSelectionListener;
import javax.swing.table.*;
import java.util.*;
import java.util.List;


/**
 * GalacticPlayerTable contains a list of players.
 * All the cells are editable.
 * It is initialized with a list of Players and returns a list of Players.
 *
 * @author Barry Becker
 */
public abstract class PlayerTable
{
    protected JTable table_;

    // remember the deleted rows, so we can add them back when the user clicks add again
    protected List deletedRows_;

    protected static final int NAME_INDEX = 0;
    protected static final int COLOR_INDEX = 1;
    protected static final int TYPE_INDEX = 2;

    protected static final String NAME = GameContext.getLabel("NAME");
    protected static final String COLOR = GameContext.getLabel("COLOR");
    protected static final String HUMAN = GameContext.getLabel("HUMAN");

    protected static String[] columnNames_;

    /**
     * constructor
     * @param players to initializet the rows in the table with.
     */
    public PlayerTable(Player[] players, String[] columnNames)
    {
        deletedRows_ = new ArrayList();
        columnNames_ = columnNames;

        initializeTable();

        for (int i=0; i<players.length; i++)  {
            Player p = players[i];
            addRow(p);
        }

    }

    protected void initializeTable()
    {
        TableModel m = new PlayerTableModel(columnNames_, 0, true);
        table_ = new JTable(m);

        TableColumn colColumn = table_.getColumn(COLOR);
        colColumn.setCellRenderer(new ColorCellRenderer());
        colColumn.setCellEditor(new ColorCellEditor(GameContext.getLabel("SELECT_PLAYER_COLOR")));
    }

    /**
     * @return  the players represented by rows in the table
     */
    public abstract Player[] getPlayers();

    public JTable getTable()
    {
        return table_;
    }

    public void addListSelectionListener(ListSelectionListener l)
    {
        table_.getSelectionModel().addListSelectionListener(l);
    }

    protected PlayerTableModel getModel()
    {
        return (PlayerTableModel)table_.getModel();
    }

    /**
     * add a row based on a player object
     * @param player to add
     */
    protected abstract void addRow(Player player);

    /**
     * add another row to the end of the table.
     */
    public void addRow()
    {
        if (deletedRows_.isEmpty()) {
            Player player = createPlayer();
            addRow(player);
        }
        else
            getModel().addRow((Vector)deletedRows_.remove(0));
    }

    protected abstract Player createPlayer();

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

    protected int getNumColumns() {
        return columnNames_.length;
    }
}
