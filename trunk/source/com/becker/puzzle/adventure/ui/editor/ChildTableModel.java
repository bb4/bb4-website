package com.becker.puzzle.adventure.ui.editor;

import com.becker.game.multiplayer.common.ui.*;
import javax.swing.table.*;
import java.util.*;


/**
 * Basically the DefaultTableModel with a few customizations
 * @see com.becker.game.multiplayer.common.ui.PlayerTable
 *
 * @author Barry Becker
 */
public class ChildTableModel extends DefaultTableModel
{

    public ChildTableModel(Object[][] data, Object[] columnNames)
    {
        super(data, columnNames);
    }

    public ChildTableModel(Object[] columnNames, int rowCount)
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

        return (column == ChildTable.ACTION_INDEX
                || column == ChildTable.NAVIGATE_INDEX
                || column == ChildTable.CHOICE_DESCRIPTION_INDEX);
    }
}
