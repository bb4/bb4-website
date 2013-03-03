/** Copyright by Barry G. Becker, 2000-2011. Licensed under MIT License: http://www.opensource.org/licenses/MIT  */
package com.barrybecker4.ui.table;

import javax.swing.*;
import java.awt.*;

/**
 * ColorCellRenderer renders a cell in a table that represents a color
 * @see ColorCellRenderer
 *
 * @author Barry Becker
 */
public class ColorCellEditor extends com.barrybecker4.ui.table.AbstractCellEditor {

    ColorCellRenderer cellRenderer_ = new ColorCellRenderer();
    String title_;

    public ColorCellEditor(String title) {
        title_ = title;
    }

    public Component getTableCellEditorComponent(JTable table, Object value,
                                                 boolean isSelected,
                                                 int row, int col) {
        // we know the value is a PathColor
        Color color = (Color)value;
        Color selectedColor = JColorChooser.showDialog(table, title_, color );
        if (selectedColor == null)  {
            // then it was canceled.
            selectedColor = color;
        }

        this.setCellEditorValue(selectedColor);
        // shouldn't need this
        table.getModel().setValueAt(selectedColor, row, col);

        return cellRenderer_.getTableCellRendererComponent(table, selectedColor, true, isSelected, row, col);
    }

}
