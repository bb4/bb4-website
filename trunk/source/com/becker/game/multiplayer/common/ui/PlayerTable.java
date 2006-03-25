package com.becker.game.multiplayer.common.ui;

import com.becker.game.common.*;
import com.becker.ui.*;

import javax.swing.*;
import javax.swing.event.*;
import javax.swing.table.*;
import java.util.*;


/**
 * PlayerTable contains a list of players.
 * All the cells are editable.
 * It is initialized with a list of Players and returns a list of Players.
 *
 * @author Barry Becker
 */
public abstract class PlayerTable
{
    protected JTable table_;

    // remember the deleted rows, so we can add them back when the user clicks add again
    protected List<Vector> deletedRows_;

    protected static final int NAME_INDEX = 0;
    protected static final int COLOR_INDEX = 1;
    protected static final int TYPE_INDEX = 2;

    protected static final String NAME = GameContext.getLabel("NAME");
    protected static final String COLOR = GameContext.getLabel("COLOR");
    protected static final String HUMAN = GameContext.getLabel("HUMAN");


    protected String[] columnNames_;

    /**
     * constructor
     * @param players to initializet the rows in the table with.
     */
    public PlayerTable(Player[] players, String[] columnNames)
    {
        deletedRows_ = new ArrayList<Vector>();
        columnNames_ = columnNames;

        initializeTable();

        for (Player p : players) {
            addRow(p);
        }
    }

    protected void initializeTable()
    {
        TableModel m = new PlayerTableModel(columnNames_, 0, true);
        table_ = new JTable(m);

        TableColumn nameColumn = table_.getColumn(NAME);
        nameColumn.setPreferredWidth(130);

        TableColumn colColumn = table_.getColumn(COLOR);
        colColumn.setCellRenderer(new ColorCellRenderer());
        colColumn.setCellEditor(new ColorCellEditor(GameContext.getLabel("SELECT_PLAYER_COLOR")));
        colColumn.setPreferredWidth(25);
        colColumn.setWidth(20);
        colColumn.setMaxWidth(25);

        table_.sizeColumnsToFit(0);
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
            getModel().addRow(deletedRows_.remove(0));
    }

    protected abstract Player createPlayer();

    /**
     * remove the selected rows from the table
     */
    public void removeSelectedRows()
    {
        int nSelected = table_.getSelectedRowCount();
        int[] selectedRows = table_.getSelectedRows();
        if (selectedRows.length == table_.getRowCount()) {
            JOptionPane.showMessageDialog(null, "You are not allowed to delete all the players!");
            return;
        }
        for (int i=nSelected-1; i>=0; i--) {
            int selRow = selectedRows[i];
            System.out.println("adding this to delete list:"+ getModel().getDataVector().elementAt(selRow).getClass().getName());
            deletedRows_.add((Vector)getModel().getDataVector().elementAt(selRow));
            getModel().removeRow(selRow);
        }
    }



    protected int getNumColumns() {
        return columnNames_.length;
    }

}
