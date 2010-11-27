package com.becker.game.multiplayer.common.ui;

import com.becker.game.common.GameContext;
import com.becker.game.common.player.PlayerList;
import com.becker.ui.table.ColorCellEditor;
import com.becker.ui.table.ColorCellRenderer;
import com.becker.ui.table.TableBase;
import com.becker.ui.table.TableColumnMeta;

import javax.swing.table.TableModel;


/**
 * Shows a list of th remaining players stats at the end of the game.
 * None of the cells are editable.
 *
 * @author Barry Becker
 */
public abstract class SummaryTable extends TableBase  {

    protected static final int NAME_INDEX = 0;
    protected static final int COLOR_INDEX = 1;

    protected static final String NAME = GameContext.getLabel("NAME");
    protected static final String COLOR = GameContext.getLabel("COLOR");


    /**
     * constructor
     * @param players to initializet the rows in the table with.
     */
    protected SummaryTable(PlayerList players, String[] columnNames)
    {
        super(players, columnNames);
    }


    @Override
    protected void updateColumnMeta(TableColumnMeta[] columnMeta) {

        TableColumnMeta colorMeta = columnMeta[COLOR_INDEX];
        colorMeta.setCellRenderer(new ColorCellRenderer());
        colorMeta.setCellEditor(new ColorCellEditor(GameContext.getLabel("SELECT_PLAYER_COLOR")));
        colorMeta.setPreferredWidth(25);
        colorMeta.setMinWidth(20);
        colorMeta.setMaxWidth(25);

        columnMeta[COLOR_INDEX].setPreferredWidth(100);
    }


    @Override
    protected TableModel createTableModel(String[] columnNames)  {
        return  new PlayerTableModel(columnNames, 0, false);
    }


    protected PlayerTableModel getPlayerModel()
    {
        return (PlayerTableModel)table_.getModel();
    }
}
