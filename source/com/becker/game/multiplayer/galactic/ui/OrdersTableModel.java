/** Copyright by Barry G. Becker, 2000-2011. Licensed under MIT License: http://www.opensource.org/licenses/MIT  */
package com.becker.game.multiplayer.galactic.ui;

import javax.swing.table.DefaultTableModel;
import java.util.List;


/**
 * Basically the DefaultTableModel with a few customizations
 * @see GalacticPlayerTable
 *
 * @author Barry Becker
 */
class OrdersTableModel extends DefaultTableModel
{
    OrdersTableModel(Object[][] data, Object[] columnNames)
    {
        super(data, columnNames);
    }

    OrdersTableModel(Object[] columnNames, int rowCount)
    {
        super(columnNames, rowCount);
    }

    @Override
    public Class getColumnClass(int col)
    {
        List v = (List)dataVector.elementAt(0);
        return v.get(col).getClass();
    }

    public boolean isCellEditable()
    {
        return true;
    }
}
