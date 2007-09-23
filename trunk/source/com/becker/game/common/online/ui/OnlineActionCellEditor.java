package com.becker.game.common.online.ui;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

/**
 * @author Barry Becker Date: May 20, 2006
 */
public class OnlineActionCellEditor extends com.becker.ui.table.AbstractCellEditor {

    private final OnlineActionCellRenderer cellRenderer_;


    public OnlineActionCellEditor(ActionListener listener)
    {

        cellRenderer_  =  new OnlineActionCellRenderer(listener);
    }

    public Component getTableCellEditorComponent(JTable table, Object val,
                                                 boolean isSelected,
                                                 int row, int col)
    {
        return cellRenderer_.getTableCellRendererComponent(table, val, isSelected, true, row, col);
   }

    /*
    public boolean stopCellEditing() {

        setCellEditorValue(false);
        return super.stopCellEditing();
    }
    */

}
