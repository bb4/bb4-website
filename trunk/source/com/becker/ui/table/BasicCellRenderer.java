// Copyright by Barry G. Becker, 2012. Licensed under MIT License: http://www.opensource.org/licenses/MIT
package com.becker.ui.table;

import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import java.awt.*;

/**
 * Renders a cell in a table that has a tooltip
 * @see ColorCellEditor
 *
 * @author Barry Becker
 */
public class BasicCellRenderer extends DefaultTableCellRenderer {

    
    public BasicCellRenderer() {}

    /*
    public BasicCellRenderer(Color backGroundColor) {
        super();
        this.setBackground(backGroundColor);
    }*/
    /**
     * {@inheritDoc}
     */
    @Override
    protected void setValue(Object value) {
        super.setValue(value);
        this.setToolTipText((value == null) ? "" : value.toString());
    }
    
    
}
