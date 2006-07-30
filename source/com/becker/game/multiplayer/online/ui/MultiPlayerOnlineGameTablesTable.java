package com.becker.game.multiplayer.online.ui;

import com.becker.game.common.*;
import com.becker.game.multiplayer.common.ui.*;
import com.becker.game.online.*;
import com.becker.game.online.ui.*;

import javax.swing.*;
import javax.swing.event.*;
import javax.swing.table.*;
import java.awt.event.*;

/**
 * A table that has a row for each virtual online game table.
 *
 * @author Barry Becker
 */
public abstract class MultiPlayerOnlineGameTablesTable implements ActionListener {


    protected JTable table_;

    protected static final int JOIN_INDEX = 0;
    protected static final int TABLE_NAME_INDEX = 1;
    protected static final int PLAYER_NAMES_INDEX = 2;

    protected static final String ACTION = GameContext.getLabel("ACTION");
    protected static final String TABLE_NAME = GameContext.getLabel("TABLE_NAME");
    protected static final String PLAYER_NAMES = GameContext.getLabel("PLAYER_NAMES");

    protected OnlineGameTable selectedTable_;

    private static final String[] COLUMN_NAMES = {ACTION, TABLE_NAME, PLAYER_NAMES };

    private static int counter_;

    /**
     * constructor
     */
    public MultiPlayerOnlineGameTablesTable(OnlineGameTable[] tables)
    {
        selectedTable_ = null;

        initializeTable(tables);
    }

    /***
     * int the table of tables.
     * @@ something odd here. Why are we passing in tables?
     * @param tables initial list (may be null)
     */
    protected void initializeTable(OnlineGameTable[] tables)
    {
        TableModel m = new PlayerTableModel(COLUMN_NAMES, 0, true);
        table_ = new JTable(m);

        TableColumn nameColumn = table_.getColumn(TABLE_NAME);
        nameColumn.setPreferredWidth(130);

        TableColumn actionColumn = table_.getColumn(ACTION);
        actionColumn.setCellRenderer(new OnlineActionCellRenderer(this));
        actionColumn.setCellEditor(new OnlineActionCellEditor(this));
        actionColumn.setPreferredWidth(55);
        //table_.sizeColumnsToFit(0);
    }

    /**
     * @return  the players represented by rows in the table
     */
    //public abstract OnlineGameTable[] getOnlineGameTables();

    public JTable getTable()
    {
        return table_;
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
     * add a row based on a player object
     * @param onlineTable to add
     */
    protected abstract void addRow(OnlineGameTable onlineTable);


    /**
     * clear out all the rows in the table.
     */
    public void removeAllRows() {

        PlayerTableModel m = this.getModel();
        for (int i = m.getRowCount() -1; i >= 0; i--) {
            m.removeRow(i);
        }
    }

    /**
     *
     * @param initialPlayerName
     * @return the new online table to add as a new row.
     */
    public abstract OnlineGameTable createOnlineTable(String initialPlayerName);


    /**
     * add another row to the end of the table.
     */
    public void addRow(String playersName)
    {
        OnlineGameTable onlineTable = createOnlineTable(playersName);
        addRow(onlineTable);
    }


    protected int getNumColumns() {
        return COLUMN_NAMES.length;
    }

    protected static synchronized String getUniqueName() {
          return "Table "+ counter_++;
    }

    public void actionPerformed(ActionEvent event) {

        OnlineActionCellRenderer.JoinButton b =
                (OnlineActionCellRenderer.JoinButton) event.getSource();

        final int joinRow = b.getRow();
        final PlayerTableModel m = getModel();

        for (int i=0; i<m.getRowCount(); i++) {
            m.setValueAt(Boolean.valueOf(i != joinRow), i, 0);
        }

        table_.removeEditor();
    }

}
