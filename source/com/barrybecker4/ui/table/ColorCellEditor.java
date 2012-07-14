/** Copyright by Barry G. Becker, 2000-2011. Licensed under MIT License: http://www.opensource.org/licenses/MIT  */
package com.barrybecker4.ui.table;

import javax.swing.*;
import java.awt.*;

/**
 * ColorCellRenderer renders a cell in a table that reperesents a color
 * @see com.barrybecker4.ui.table.ColorCellRenderer
 *
 * @author Barry Becker
 */
public class ColorCellEditor extends AbstractCellEditor {

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
        if (selectedColor == null)  {// then it was canceled.
           selectedColor = color;
        }

        this.setCellEditorValue(selectedColor);
        table.getModel().setValueAt(selectedColor, row, col);    // shouldn't need this

        return cellRenderer_.getTableCellRendererComponent(table, selectedColor, true, isSelected, row, col);
    }

}
