/** Copyright by Barry G. Becker, 2000-2011. Licensed under MIT License: http://www.opensource.org/licenses/MIT  */
package com.barrybecker4.ui.table;

import javax.swing.table.DefaultTableModel;
import java.util.List;


/**
 * Basically the DefaultTableModel with a few customizations.
 *
 * @author Barry Becker
 */
public class BasicTableModel extends DefaultTableModel {

    private static final long serialVersionUID = 0;

    private boolean editable_;

    public BasicTableModel(Object[][] data, Object[]
            columnNames, boolean editable) {
        super(data, columnNames);
        editable_ = editable;
    }

    public BasicTableModel(Object[] columnNames, int rowCount,
                           boolean editable) {
        super(columnNames, rowCount);
        editable_ = editable;
    }

    @Override
    public Class getColumnClass(int col) {
        List v = (List)dataVector.elementAt(0);
        return v.get(col).getClass();
    }

    @Override
    public boolean isCellEditable(int row, int column) {
        return editable_;
    }
}
