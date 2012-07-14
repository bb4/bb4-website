/** Copyright by Barry G. Becker, 2000-2012. Licensed under MIT License: http://www.opensource.org/licenses/MIT  */
package com.barrybecker4.ui.table;

import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import java.awt.*;

/**
 * ColorCellRenderer renders a cell in a table that represents a color
 * @see com.barrybecker4.ui.table.ColorCellEditor
 *
 * @author Barry Becker
 */
public class ColorCellRenderer extends DefaultTableCellRenderer {
    public ColorCellRenderer() {
        setHorizontalAlignment(JLabel.CENTER);
    }

    @Override
    public Component getTableCellRendererComponent(JTable table, Object value,
                                                   boolean isSelected, boolean hasFocus,
                                                   int row, int col) {
        // we know the value is a PathColor
        Color color = (Color)value;
        setBackground(color);
        setToolTipText(color.toString());

        return this;
    }
}
