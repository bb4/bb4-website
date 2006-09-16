package com.becker.game.multiplayer.online.ui;

import com.becker.game.common.*;
import com.becker.game.multiplayer.common.*;
import com.becker.game.multiplayer.common.ui.*;
import com.becker.game.online.*;
import com.becker.game.online.ui.*;

import javax.swing.*;
import javax.swing.event.*;
import javax.swing.table.*;
import java.awt.event.*;
import java.util.*;

/**
 * A table that has a row for each virtual online game table.
 *
 * @author Barry Becker
 */
public abstract class MultiPlayerOnlineGameTablesTable {


    protected JTable table_;

    protected static final int JOIN_INDEX = 0;
    protected static final int TABLE_NAME_INDEX = 1;
    protected static final int NUM_PLAYERS_INDEX = 2;
    protected static final int PLAYER_NAMES_INDEX = 3;
    protected static final int NUM_BASE_COLUMNS = 4;

    protected static final String JOIN = GameContext.getLabel("ACTION");
    protected static final String TABLE_NAME = GameContext.getLabel("TABLE_NAME");
    protected static final String MAX_NUM_PLAYERS = GameContext.getLabel("MAX_NUM_PLAYERS");
    protected static final String PLAYER_NAMES = GameContext.getLabel("PLAYER_NAMES");

    protected OnlineGameTable selectedTable_;
    protected List<OnlineGameTable> tableList_;
    private static int counter_;

    ActionListener actionListener_;


    /**
     * constructor
     * @param actionListener called when join button clicked.
     */
    public MultiPlayerOnlineGameTablesTable(ActionListener actionListener)
    {
        selectedTable_ = null;
        initializeTable();
        actionListener_ = actionListener;
        tableList_ = new ArrayList<OnlineGameTable>();
    }

    /***
     * int the table of tables.
     */
    protected void initializeTable()
    {
        TableModel m = new PlayerTableModel(getColumnNames(), 0, true);
        table_ = new JTable(m);

        // more space needed for the names list.
        TableColumn nameColumn = table_.getColumn(PLAYER_NAMES);
        nameColumn.setPreferredWidth(200);

        TableColumn actionColumn = table_.getColumn(JOIN);
        actionColumn.setCellRenderer(new OnlineActionCellRenderer(actionListener_));
        actionColumn.setCellEditor(new OnlineActionCellEditor(actionListener_));
        actionColumn.setPreferredWidth(55);

        TableColumn tableNameColumn = table_.getColumn(TABLE_NAME);
        tableNameColumn.setPreferredWidth(0);
    }

    protected String[] getColumnNames() {
        return new String[] {JOIN, TABLE_NAME, MAX_NUM_PLAYERS, PLAYER_NAMES};
    }

    public JTable getTable()
    {
        return table_;
    }

    public OnlineGameTable getGameTable(int i) {
        return tableList_.get(i);
    }
    /**
     *
     * @return the table that the player has chosen to sit at if any (at most 1.) return null is not sitting.
     */
    public OnlineGameTable getSelectedTable() {
        return selectedTable_;
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
     * clear out all the rows in the table.
     */
    public void removeAllRows() {

        PlayerTableModel m = this.getModel();
        for (int i = m.getRowCount() -1; i >= 0; i--) {
            m.removeRow(i);
        }
        tableList_.clear();
    }

    /**
     *
     * @param initialPlayerName
     * @return the new online table to add as a new row.
     */
    public abstract OnlineGameTable createOnlineTable(String initialPlayerName, MultiGameOptions options);

    public abstract Player createPlayerForName(String playerName);

    /**
     * add a row based on a player object
     * @param onlineTable to add
     * @param localPlayerAtTable you cannot join a table you are already at.
     */
    protected void addRow(OnlineGameTable onlineTable, boolean localPlayerAtTable) {

        getModel().addRow(getRowObject(onlineTable, localPlayerAtTable));
        tableList_.add(onlineTable);
        selectedTable_ = onlineTable;
    }

    /**
     * @return  the object array to create a row from.
     */
    protected abstract Object[] getRowObject(OnlineGameTable onlineTable, boolean localPlayerAtTable);

    /**
     * add another row to the end of the table.
     */
    public void addRow(String playersName, MultiGameOptions options)
    {
        OnlineGameTable onlineTable = createOnlineTable(playersName, options);
        addRow(onlineTable, true);
    }


    protected int getNumColumns() {
        return getColumnNames().length;
    }

    protected static synchronized String getUniqueName() {
          return "Table "+ counter_++;
    }

}
