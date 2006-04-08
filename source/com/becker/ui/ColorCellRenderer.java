package com.becker.ui;

import javax.swing.*;
import javax.swing.table.*;
import java.awt.*;

/**
 * ColorCellRenderer renders a cell in a table that reperesents a color
 * @see com.becker.ui.ColorCellEditor
 *
 * @author Barry Becker
 */
public class ColorCellRenderer extends DefaultTableCellRenderer
{
    public ColorCellRenderer()
    {
        setHorizontalAlignment(JLabel.CENTER);
    }

    public Component getTableCellRendererComponent(JTable table, Object value,
                                                   boolean isSelected, boolean hasFocus,
                                                   int row, int col)
    {
        // we know the value is a Color
        Color color = (Color)value;
        setBackground(color);       

        return this;
    }
}
