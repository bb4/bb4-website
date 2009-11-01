package com.becker.ui.table;

import javax.swing.*;
import javax.swing.table.*;
import java.awt.*;
import java.awt.event.*;

/**
 * Renders a button in a table cell.
 *
 * @author Barry Becker 
 */
public class ButtonCellRenderer implements TableCellRenderer {

    private final TableButton button_;


    /**
     * Constructor
     * @param label button text
     * @param listener what to call when clicked.
     */
    public ButtonCellRenderer(String label, ActionListener listener)
    {
        this(label, listener, null);
    }

    /**
     * Constructor
     * @param label button text
     * @param listener what to call when clicked.
     * @param id button identifier
     */
    public ButtonCellRenderer(String label, ActionListener listener, String id)
    {
        button_ = new TableButton(label);
        button_.setToolTipText(label);
        button_.addActionListener(listener);
        button_.setId(id);
    }

    public void setTooltip(String tooltip) {
        button_.setToolTipText(tooltip);
    }

    public Component getTableCellRendererComponent(
                                                   JTable table,
                                                   Object value,   // boolean or string
                                                   boolean isSelected,
                                                   boolean hasFocus,
                                                   int row, int col)  {
        if (value != null) {
            if (value instanceof String) {
                 button_.setText((String)value);
            } else if (value instanceof Boolean) {
                 button_.setEnabled((Boolean) value);
            } else {
                assert false : "unexpected type for value : " + value;
            }
        }

        button_.setRow(row);
        return button_;
    }


    public int getRow() {
        return button_.getRow();
    }

    public void addMouseListener(MouseListener l) {
        button_.addMouseListener(l);
    }
}

