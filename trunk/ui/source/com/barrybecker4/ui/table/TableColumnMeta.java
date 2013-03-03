/** Copyright by Barry G. Becker, 2000-2011. Licensed under MIT License: http://www.opensource.org/licenses/MIT  */
package com.barrybecker4.ui.table;

import javax.swing.*;
import javax.swing.table.TableCellEditor;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableColumn;

/**
 * Meta data information about a column in a Table.
 * @author Barry Becker
 */
public class TableColumnMeta {

    /** name of the column */
    private String name_;
    /** mouse over tip (optional)   */
    private String tooltip_;
    private Integer minWidth_;
    private Integer preferredWidth_;
    private Integer maxWidth_;
    private boolean resizable_ = true;
    private TableCellRenderer cellRenderer_ = null;
    private TableCellEditor cellEditor_ = null;

    public TableColumnMeta(String name, String tooltip) {
        name_ = name;
        tooltip_ = tooltip;
    }

    public TableColumnMeta(String name, String tooltip, int minWidth, int preferredWidth, int maxWidth) {
        this(name, tooltip);
        minWidth_ = minWidth;
        preferredWidth_ = preferredWidth;
        maxWidth_ = maxWidth;
    }

    /**
     * Initialize the column in this table for this metaColumn data.
     * @param table
     */
    public void initializeColumn(JTable table) {
        String name = getName();
        TableColumn column = table.getColumn(name);
        if (getTooltip() != null)  {
            TableCellRenderer r = new HeaderRenderer();
            JComponent c = (JComponent)r.getTableCellRendererComponent(table, name, false, false, 0, 0);
            c.setToolTipText(getTooltip());
            column.setHeaderRenderer(r);
        }
        if (getMinWidth() != null) {
            column.setMinWidth(getMinWidth());
        }
        if (getPreferredWidth() != null) {
            column.setPreferredWidth(getPreferredWidth());
        }
        if (getMaxWidth() != null) {
            column.setMaxWidth(getMaxWidth());
        }
        if (getCellRenderer() != null) {
            column.setCellRenderer(getCellRenderer());
        }
        if (getCellEditor() != null) {
            column.setCellEditor(getCellEditor());
        }
    }

    public String getName() {
        return name_;
    }


    public void setTooltip(String tip) {
        tooltip_ = tip;
    }

    public String getTooltip() {
        return tooltip_;
    }

    public void setMinWidth(int w) {
        minWidth_ = w;
    }

    public Integer getMinWidth() {
        return minWidth_;
    }

    public void setPreferredWidth(int w) {
        preferredWidth_ = w;
    }

    public Integer getPreferredWidth() {
        return preferredWidth_;
    }

    public void setMaxWidth(int w) {
        maxWidth_ = w;
    }

    public Integer getMaxWidth() {
        return maxWidth_;
    }

    public boolean isResizable() {
        return resizable_;
    }

    public void setResizable(boolean resizable) {
        this.resizable_ = resizable;
    }


    public TableCellRenderer getCellRenderer() {
        return cellRenderer_;
    }

    public void setCellRenderer(TableCellRenderer cellRenderer) {
        cellRenderer_ = cellRenderer;
    }

    public TableCellEditor getCellEditor() {
        return cellEditor_;
    }

    public void setCellEditor(TableCellEditor cellEditor) {
        cellEditor_ = cellEditor;
    }

}
