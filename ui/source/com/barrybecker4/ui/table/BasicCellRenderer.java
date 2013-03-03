// Copyright by Barry G. Becker, 2012. Licensed under MIT License: http://www.opensource.org/licenses/MIT
package com.barrybecker4.ui.table;

import javax.swing.table.DefaultTableCellRenderer;

/**
 * Renders a cell in a table that has a tooltip
 * @see ColorCellEditor
 *
 * @author Barry Becker
 */
public class BasicCellRenderer extends DefaultTableCellRenderer {


    public BasicCellRenderer() {}

    /**
     * {@inheritDoc}
     */
    @Override
    protected void setValue(Object value) {
        super.setValue(value);
        this.setToolTipText((value == null) ? "" : value.toString());
    }


}
