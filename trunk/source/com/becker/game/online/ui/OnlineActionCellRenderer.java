package com.becker.game.online.ui;

import com.becker.game.common.*;

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
                                                   boolean isSelected, boolean hasFocus,
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



    public static class JoinButton extends JButton {

        private int row_;

        public JoinButton(ActionListener listener) {

            super(GameContext.getLabel("JOIN"));
            setToolTipText(GameContext.getLabel("JOIN_TIP"));
            setHorizontalAlignment(JLabel.CENTER);
            setPreferredSize(new Dimension(100, 18));
            addActionListener(listener);
        }


        public int getRow() {
            return row_;
        }

        public void setRow(int row) {
            row_ = row;
        }

        public String toString() {
            return "JoinButton row = "+getRow() ;
        }
    }

}

