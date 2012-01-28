// Copyright by Barry G. Becker, 2012. Licensed under MIT License: http://www.opensource.org/licenses/MIT
package com.becker.game.twoplayer.comparison.ui.grid.cellrenderers;

import com.becker.game.twoplayer.comparison.model.Outcome;
import com.becker.game.twoplayer.comparison.model.PerformanceResultsPair;
import com.becker.ui.util.GUIUtil;

import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.JTableHeader;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableModel;
import java.awt.*;
import java.awt.geom.Point2D;

/**
 * Show the column headers with player two's color.
 * validate/revalidate overridden in cell renderer for performance reasons..
 *
 * @author Barry Becker
 */
public class ResultHeaderCellRenderer extends PlayerHeadCellRenderer {

    /**
     * This method gets called each time a column header is rendered.
     * @param value column header value of column 'vColIndex'
     * @param row always -1
     * @param isSelected always false.
     * @param hasFocus always false.
     */
    @Override
    public Component getTableCellRendererComponent(JTable table, Object value,
            boolean isSelected, boolean hasFocus, int row, int column) {
        // Inherit the colors and font from the header component
        if (table != null) {
            JTableHeader header = table.getTableHeader();
            if (header != null) {
                setForeground(Outcome.PLAYER2_WON.getColor().darker());
                setBackground(Color.WHITE);
                setFont(header.getFont());
            }
        }

        if (value instanceof TextAndIcon) {
            setIcon(((TextAndIcon)value).icon);
            setText(((TextAndIcon)value).text);
        } else {
            setText((value == null) ? "" : value.toString());
            setIcon(null);
        }
        setBorder(UIManager.getBorder("TableHeader.cellBorder"));
        setHorizontalAlignment(JLabel.CENTER);
        this.setFont(FONT);
        return this;
    }

    class TextAndIcon {
        TextAndIcon(String text, Icon icon) {
            this.text = text;
            this.icon = icon;
        }
        String text;
        Icon icon;
    }

}
                                            