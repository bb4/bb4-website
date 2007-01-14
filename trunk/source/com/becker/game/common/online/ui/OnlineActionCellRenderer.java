package com.becker.game.common.online.ui;

import com.becker.game.common.online.ui.*;

import javax.swing.*;
import javax.swing.table.*;
import java.awt.*;
import java.awt.event.*;

/**
 * Renders the action button in the OnlineGamesTable.
 *
 * @author Barry Becker Date: May 20, 2006
 */
public class OnlineActionCellRenderer implements TableCellRenderer {

    private JoinButton joinButton_;



    public OnlineActionCellRenderer(ActionListener listener)
    {
        joinButton_ = new JoinButton(listener);

    }

    public Component getTableCellRendererComponent(JTable table,
                                                   Object value,   // boolean
                                                   boolean isSelected,
                                                   boolean hasFocus,
                                                   int row, int col)
    {
        if (value != null)
            joinButton_.setEnabled((Boolean) value);

        joinButton_.setRow(row);
        return joinButton_;
    }


    public int getRow() {
        return joinButton_.getRow();
    }

    public void addMouseListener(MouseListener l) {
        joinButton_.addMouseListener(l);
    }


}

