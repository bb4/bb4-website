package com.becker.puzzle.adventure.ui.editor;

import javax.swing.table.DefaultTableModel;
import java.util.List;
import java.util.Vector;


/**
 * Basically the DefaultTableModel with a few customizations
 * @see com.becker.game.multiplayer.common.ui.PlayerTable
 *
 * @author Barry Becker
 */
public class ParentTableModel extends DefaultTableModel
{
    private static final long serialVersionUID = 0;

    public ParentTableModel(Object[][] data, Object[] columnNames)
    {
        super(data, columnNames);
    }

    public ParentTableModel(Object[] columnNames, int rowCount)
    {
        super(columnNames, rowCount);
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

        return (column == ParentTable.NAVIGATE_INDEX);
    }
}