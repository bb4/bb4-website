// Copyright by Barry G. Becker, 2012. Licensed under MIT License: http://www.opensource.org/licenses/MIT
package com.becker.ui.table;

import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import java.awt.*;

/**
 * ColorCellRenderer renders a cell in a table that represents a color
 * @see ColorCellEditor
 *
 * @author Barry Becker
 */
public class BasicCellRenderer extends DefaultTableCellRenderer {

    /**
     * {@inheritDoc}
     */
    @Override
    protected void setValue(Object value) {
        super.setValue(value);
        this.setToolTipText((value == null) ? "" : value.toString());
    }
}
