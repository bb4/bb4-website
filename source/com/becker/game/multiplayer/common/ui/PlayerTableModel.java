package com.becker.game.multiplayer.common.ui;

import javax.swing.table.*;
import java.util.*;


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

    public Class getColumnClass(int col)
    {
        Vector v = (Vector)dataVector.elementAt(0);
        return v.elementAt(col).getClass();
    }

    public boolean isCellEditable(int row, int column)
    {
        return editable_;
    }
}
