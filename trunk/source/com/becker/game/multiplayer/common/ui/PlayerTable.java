package com.becker.game.multiplayer.common.ui;

import com.becker.game.common.*;
import com.becker.ui.table.*;

import javax.swing.*;
import javax.swing.table.*;
import java.util.*;


/**
 * PlayerTable contains a list of players.
 * All of the cells are editable.
 * It is initialized with a list of Players and returns a list of Players.
 *
 * @author Barry Becker
 */
public abstract class PlayerTable extends TableBase
{

    // remember the deleted rows, so we can add them back when the user clicks add again
    protected List<Vector> deletedRows_;

    protected static final int NAME_INDEX = 0;
    protected static final int COLOR_INDEX = 1;
    protected static final int HUMAN_INDEX = 2;

    protected static final String NAME = GameContext.getLabel("NAME");
    protected static final String COLOR = GameContext.getLabel("COLOR");
    protected static final String HUMAN = GameContext.getLabel("HUMAN");


    /**
     * constructor
     * @param players to initializet the rows in the table with.
     */
    public PlayerTable(PlayerList players, String[] columnNames)
    {
        super(players, columnNames);

        deletedRows_ = new ArrayList<Vector>();
    }

    @Override
    protected void updateColumnMeta(TableColumnMeta[] columnMeta) {

        columnMeta[NAME_INDEX].setPreferredWidth(130);
        columnMeta[HUMAN_INDEX].setPreferredWidth(80);
        TableColumnMeta colorColumnMeta = new TableColumnMeta(COLOR, null, 20, 40, 40);
        colorColumnMeta.setCellRenderer(new ColorCellRenderer());
        colorColumnMeta.setCellEditor(new ColorCellEditor(GameContext.getLabel("SELECT_PLAYER_COLOR")));
        columnMeta[COLOR_INDEX] = colorColumnMeta;
    }


    /**
     * @return  the players represented by rows in the table
     */
    public abstract PlayerList getPlayers();


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
            getPlayerModel().addRow(deletedRows_.remove(0));
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
        PlayerTableModel model = (PlayerTableModel)getModel();
        for (int i=nSelected-1; i>=0; i--) {
            int selRow = selectedRows[i];
            GameContext.log(0, "adding this to delete list:"
                    + model.getDataVector().elementAt(selRow).getClass().getName());
            deletedRows_.add((Vector)model.getDataVector().elementAt(selRow));
            model.removeRow(selRow);
        }
    }

    protected PlayerTableModel getPlayerModel() {
        return (PlayerTableModel) getModel();
    }


    @Override
    protected TableModel createTableModel(String[] columnNames) {
        return new PlayerTableModel(columnNames, 0, true);
    }

}
