package com.becker.game.online.ui;

import com.becker.game.common.*;

import javax.swing.*;
import java.awt.event.*;
import java.awt.*;

/**
 * @author Barry Becker Date: Sep 17, 2006
 */
public class JoinButton extends JButton {

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
