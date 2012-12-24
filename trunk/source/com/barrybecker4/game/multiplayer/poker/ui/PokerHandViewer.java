// Copyright by Barry G. Becker, 2012. Licensed under MIT License: http://www.opensource.org/licenses/MIT
package com.barrybecker4.game.multiplayer.poker.ui;

import com.barrybecker4.common.geometry.Location;
import com.barrybecker4.game.common.GameContext;
import com.barrybecker4.game.multiplayer.common.ui.ActionDialog;
import com.barrybecker4.game.multiplayer.poker.PokerAction;
import com.barrybecker4.game.multiplayer.poker.PokerController;
import com.barrybecker4.game.multiplayer.poker.PokerOptions;
import com.barrybecker4.game.multiplayer.poker.hand.PokerHand;
import com.barrybecker4.game.multiplayer.poker.player.PokerPlayer;
import com.barrybecker4.ui.components.GradientButton;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.text.NumberFormat;

/**
 * Shows the player the contents of their hand so they can bet on it.
 * @author Barry Becker
 */
final class PokerHandViewer extends JPanel {

    PokerHand hand_;
    HandRenderer handRenderer = new HandRenderer();

    public PokerHandViewer(PokerHand hand) {
        hand_ = new PokerHand(hand.getCards());
        hand_.setFaceUp(true);
        this.setPreferredSize(new Dimension(400, 120));
    }

    @Override
    protected void paintComponent(Graphics g) {
         handRenderer.render((Graphics2D) g, new Location(0, 2), hand_, 22);
    }
}

