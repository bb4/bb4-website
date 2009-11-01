package com.becker.ui.table;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import javax.swing.table.TableCellRenderer;

/**
 * A button in a cell.
 *
 * @author Barry Becker 
 */
public class ButtonCellEditor extends com.becker.ui.table.AbstractCellEditor {

    private final ButtonCellRenderer cellRenderer_;


    /**
     * Constructor
     * @param label button text
     * @param listener what to call when button clicked.
     */
    public ButtonCellEditor(String label, ActionListener listener)
    {
        this(label, label, listener);
    }

    public ButtonCellEditor(String label, String tooltip, ActionListener listener)
    {
        this(label, tooltip, listener, null);
    }

    public ButtonCellEditor(String label,  ActionListener listener, String id)
    {
        this(label, label, listener, id);
    }

    public ButtonCellEditor(String label, String tooltip, ActionListener listener, String id)
    {
        cellRenderer_  =  new ButtonCellRenderer(label, listener, id);
        cellRenderer_.setTooltip(tooltip);
    }


    public TableCellRenderer getCellRenderer() {
        return cellRenderer_;
    }

    public Component getTableCellEditorComponent(
                                                 JTable table, Object val,
                                                 boolean isSelected,
                                                 int row, int col)
    {
        return cellRenderer_.getTableCellRendererComponent(table, val, isSelected, true, row, col);
    }

}
