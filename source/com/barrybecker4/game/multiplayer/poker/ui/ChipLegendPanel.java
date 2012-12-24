// Copyright by Barry G. Becker, 2012. Licensed under MIT License: http://www.opensource.org/licenses/MIT
package com.barrybecker4.game.multiplayer.poker.ui;

import com.barrybecker4.game.common.GameContext;
import com.barrybecker4.game.common.GameController;
import com.barrybecker4.game.common.Move;
import com.barrybecker4.game.common.player.Player;
import com.barrybecker4.game.common.ui.panel.GameChangedEvent;
import com.barrybecker4.game.common.ui.panel.GameChangedListener;
import com.barrybecker4.game.common.ui.panel.GameInfoPanel;
import com.barrybecker4.game.multiplayer.poker.PokerAction;
import com.barrybecker4.game.multiplayer.poker.PokerController;
import com.barrybecker4.game.multiplayer.poker.player.PokerPlayer;
import com.barrybecker4.game.multiplayer.poker.ui.chips.PokerChip;
import com.barrybecker4.game.multiplayer.poker.ui.dialog.BettingDialog;
import com.barrybecker4.ui.components.GradientButton;
import com.barrybecker4.ui.legend.DiscreteColorLegend;

import javax.swing.*;
import javax.swing.border.TitledBorder;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.text.MessageFormat;

/**
 * Show legend for the different chip amounts.
 *
 * @author Barry Becker
 */
class ChipLegendPanel extends JPanel {

    /**
     * Constructor
     */
    ChipLegendPanel() {
        initUI();
    }

    /**
     * This panel shows a discrete color legend for the poker chip values
     */
    void initUI() {
        PokerChip[] chipTypes = PokerChip.values();
        int n = chipTypes.length;
        Color[] colors = new Color[n];
        String[] values = new String[n];
        for (int i = n; i > 0; i--) {
            colors[n-i] = chipTypes[i-1].getColor();
            values[n-i] = chipTypes[i-1].getLabel();
        }
        JPanel legend = new DiscreteColorLegend(null, colors, values);
        legend.setPreferredSize(new Dimension(500, 100));
        add(legend);
    }

}