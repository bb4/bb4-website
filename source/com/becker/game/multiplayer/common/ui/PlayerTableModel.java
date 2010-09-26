package com.becker.game.multiplayer.common.ui;

import javax.swing.table.DefaultTableModel;
import java.util.List;
import java.util.Vector;


/**
 * Basically the DefaultTableModel with a few customizations
 * @see com.becker.game.multiplayer.common.ui.PlayerTable
 *
 * @author Barry Becker
 */
public class PlayerTableModel extends DefaultTableModel
{
    boolean editable_;
    private static final long serialVersionUID = 0;

    public PlayerTableModel(Object[][] data, Object[] columnNames, boolean editable)
    {
        super(data, columnNames);
        editable_ = editable;
    }

    public PlayerTableModel(Object[] columnNames, int rowCount, boolean editable)
    {
        super(columnNames, rowCount);
        editable_ = editable;
    }

    @Override
    public Class getColumnClass(int col)
    {
        List v = (Vector)dataVector.elementAt(0);
        return v.get(col).getClass();
    }

    @Override
    public boolean isCellEditable(int row, int column)
    {
        return editable_;
    }
}
