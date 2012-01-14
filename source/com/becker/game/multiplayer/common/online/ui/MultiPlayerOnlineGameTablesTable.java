/** Copyright by Barry G. Becker, 2000-2011. Licensed under MIT License: http://www.opensource.org/licenses/MIT  */
package com.becker.game.multiplayer.common.online.ui;

import com.becker.game.common.GameContext;
import com.becker.game.common.online.OnlineGameTable;
import com.becker.game.common.player.Player;
import com.becker.game.multiplayer.common.MultiGameOptions;
import com.becker.ui.table.BasicTableModel;
import com.becker.ui.table.*;

import javax.swing.table.TableModel;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;

/**
 * A table that has a row for each virtual online game table.
 *
 * @author Barry Becker
 */
public abstract class MultiPlayerOnlineGameTablesTable extends TableBase  {

    protected static final int JOIN_INDEX = 0;
    protected static final int NUM_PLAYERS_INDEX = 1;
    protected static final int PLAYER_NAMES_INDEX = 2;
    protected static final int NUM_BASE_COLUMNS = 3;

    protected static final String JOIN = GameContext.getLabel("ACTION");
    protected static final String MIN_NUM_PLAYERS = GameContext.getLabel("MIN_NUM_PLAYERS");
    protected static final String PLAYER_NAMES = GameContext.getLabel("PLAYER_NAMES");

    protected static final String JOIN_TIP = GameContext.getLabel("ACTION_TIP");
    private static final String MIN_NUM_PLAYERS_TIP = GameContext.getLabel("MIN_NUM_PLAYERS_TIP");
    protected static final String PLAYER_NAMES_TIP = GameContext.getLabel("PLAYER_NAMES_TIP");

    private static final String[] COLUMN_NAMES = {JOIN, MIN_NUM_PLAYERS, PLAYER_NAMES};

    private OnlineGameTable selectedTable_;
    private List<OnlineGameTable> tableList_;
    private static int counter_;

    private TableButtonListener tableButtonListener_;


    /**
     * constructor
     * @param tableButtonListener called when join button clicked.
     */
    public MultiPlayerOnlineGameTablesTable(TableButtonListener tableButtonListener)
    {
        this(COLUMN_NAMES, tableButtonListener);
    }

    protected MultiPlayerOnlineGameTablesTable(String[] colNames, TableButtonListener tableButtonListener) {

        initColumnMeta(colNames);

        assert(tableButtonListener != null);
        tableButtonListener_ = tableButtonListener;
        selectedTable_ = null;
        tableList_ = new ArrayList<OnlineGameTable>();

        initializeTable(null);
    }

    /**
     * init the table of tables.
     */
    @Override
    protected void updateColumnMeta(TableColumnMeta[] columnMeta) {

        columnMeta[NUM_PLAYERS_INDEX].setTooltip(MIN_NUM_PLAYERS_TIP);

        // more space needed for the names list.
        columnMeta[PLAYER_NAMES_INDEX].setPreferredWidth(200);

        TableColumnMeta actionCol = columnMeta[JOIN_INDEX];

        TableButton joinCellEditor = new TableButton(GameContext.getLabel("JOIN"), "id");
        joinCellEditor.addTableButtonListener(tableButtonListener_);
        joinCellEditor.setToolTipText(GameContext.getLabel("JOIN_TIP"));
        actionCol.setCellRenderer(joinCellEditor);
        actionCol.setCellEditor(joinCellEditor);
        actionCol.setPreferredWidth(55);
    }

    @Override
    protected TableModel createTableModel(String[] columnNames) {
        return new BasicTableModel(columnNames, 0, true);
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


    BasicTableModel getPlayerModel()
    {
        return (BasicTableModel)getModel();
    }

    /**
     * clear out all the rows in the table.
     */
    public void removeAllRows() {

        BasicTableModel m = this.getPlayerModel();
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

    @Override
    protected void addRow(Object onlineTable) {
        this.addRow((OnlineGameTable) onlineTable, true);
    }

    /**
     * add a row based on a player object
     * @param onlineTable to add
     * @param localPlayerAtTable you cannot join a table you are already at.
     */
    void addRow(OnlineGameTable onlineTable, boolean localPlayerAtTable) {

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
    
     protected static Color getRandomColor() {

        int r = GameContext.random().nextInt(256);
        return new Color(r, 255 - r, GameContext.random().nextInt(256));
    }

}
