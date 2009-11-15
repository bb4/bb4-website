/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package com.becker.game.multiplayer.common.ui;

import java.awt.Dimension;
import javax.swing.JLabel;
import javax.swing.JPanel;
import com.becker.game.common.Player;

/**
 *
 * @author becker
 */
public class PlayerLabel extends JPanel {

    JPanel swatch;
    JLabel playerLabel;

    public PlayerLabel() {
        swatch = new JPanel();
        swatch.setPreferredSize(new Dimension(10, 10));
        playerLabel = new JLabel();
        add(swatch);
        add(playerLabel);
    }

    public void setPlayer(Player player) {
        swatch.setBackground(player.getColor());
        playerLabel.setText(player.getName());
    }
}
