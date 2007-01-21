package com.becker.game.multiplayer.common.online.ui;

import com.becker.game.common.*;
import com.becker.game.common.online.ui.*;
import com.becker.game.common.online.*;
import com.becker.game.multiplayer.common.*;
import com.becker.game.multiplayer.common.ui.*;
import com.becker.ui.table.*;

import javax.swing.table.*;
import java.awt.event.*;
import java.util.*;

/**
 * A table that has a row for each virtual online game table.
 *
 * @author Barry Becker
 */
public abstract class MultiPlayerOnlineGameTablesTable extends TableBase {

    protected static final int JOIN_INDEX = 0;
    protected static final int NUM_PLAYERS_INDEX = 1;
    protected static final int PLAYER_NAMES_INDEX = 2;
    protected static final int NUM_BASE_COLUMNS = 3;

    protected static final String JOIN = GameContext.getLabel("ACTION");
    protected static final String MAX_NUM_PLAYERS = GameContext.getLabel("MAX_NUM_PLAYERS");
    protected static final String PLAYER_NAMES = GameContext.getLabel("PLAYER_NAMES");

    protected static final String JOIN_TIP = GameContext.getLabel("ACTION_TIP");
    protected static final String MAX_NUM_PLAYERS_TIP = GameContext.getLabel("MAX_NUM_PLAYERS_TIP");
    protected static final String PLAYER_NAMES_TIP = GameContext.getLabel("PLAYER_NAMES_TIP");

    private static final String[] COLUMN_NAMES = {JOIN, MAX_NUM_PLAYERS, PLAYER_NAMES};

    protected OnlineGameTable selectedTable_;
    protected List<OnlineGameTable> tableList_;
    private static int counter_;

    private ActionListener actionListener_;


    /**
     * constructor
     * @param actionListener called when join button clicked.
     */
    public MultiPlayerOnlineGameTablesTable(ActionListener actionListener)
    {
        this(COLUMN_NAMES, actionListener);
    }

    public MultiPlayerOnlineGameTablesTable(String[] colNames, ActionListener actionListener) {

        initColumnMeta(colNames);

        assert(actionListener != null);
        actionListener_ = actionListener;
        selectedTable_ = null;
        tableList_ = new ArrayList<OnlineGameTable>();

        initializeTable(null);
    }

    /***
     * init the table of tables.
     */
    protected void updateColumnMeta(TableColumnMeta[] columnMeta) {

        columnMeta[NUM_PLAYERS_INDEX].setTooltip(MAX_NUM_PLAYERS_TIP);

        // more space needed for the names list.
        columnMeta[PLAYER_NAMES_INDEX].setPreferredWidth(200);

        TableColumnMeta actionCol = columnMeta[JOIN_INDEX];
        actionCol.setCellRenderer(new OnlineActionCellRenderer(actionListener_));
        actionCol.setCellEditor(new OnlineActionCellEditor(actionListener_));
        actionCol.setPreferredWidth(55);
    }

    protected TableModel createTableModel(String[] columnNames) {
        return new PlayerTableModel(columnNames, 0, true);
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


    protected PlayerTableModel getPlayerModel()
    {
        return (PlayerTableModel)getModel();
    }

    /**
     * clear out all the rows in the table.
     */
    public void removeAllRows() {

        PlayerTableModel m = this.getPlayerModel();
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

    protected void addRow(Object onlineTable) {
        this.addRow((OnlineGameTable) onlineTable, true);
    }

    /**
     * add a row based on a player object
     * @param onlineTable to add
     * @param localPlayerAtTable you cannot join a table you are already at.
     */
    protected void addRow(OnlineGameTable onlineTable, boolean localPlayerAtTable) {

        getPlayerModel().addRow(getRowObject(onlineTable, localPlayerAtTable));
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


    protected static synchronized String getUniqueName() {
          return "Table "+ counter_++;
    }

}
