package com.becker.game.multiplayer.galactic.ui;

import javax.swing.table.DefaultTableModel;
import java.util.*;


/**
 * Basically the DefaultTableModel with a few customizations
 * @see PlayerTable
 *
 * @author Barry Becker
 */
class OrdersTableModel extends DefaultTableModel
{
    public OrdersTableModel(Object[][] data, Object[] columnNames)
    {
        super(data, columnNames);
    }

    public OrdersTableModel(Object[] columnNames, int rowCount)
    {
        super(columnNames, rowCount);
    }

    public Class getColumnClass(int col)
    {
        Vector v = (Vector)dataVector.elementAt(0);
        return v.elementAt(col).getClass();
    }

    public boolean isCellEditable()
    {
        return true;
    }
}
