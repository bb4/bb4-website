package com.becker.game.multiplayer.galactic.ui;

import com.becker.game.multiplayer.galactic.GalacticPlayer;
import com.becker.game.multiplayer.galactic.Planet;
import com.becker.game.common.GameContext;
import com.becker.game.common.Location;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableModel;
import java.util.*;


/**
 * Basically the DefaultTableModel with a few customizations
 * @see PlayerTable
 *
 * @author Barry Becker
 */
class PlayerTableModel extends DefaultTableModel
{
    boolean editable_;

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
