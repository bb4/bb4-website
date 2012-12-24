// Copyright by Barry G. Becker, 2012. Licensed under MIT License: http://www.opensource.org/licenses/MIT
package com.barrybecker4.game.multiplayer.poker.ui.dialog;

import com.barrybecker4.common.geometry.Location;
import com.barrybecker4.game.multiplayer.poker.hand.PokerHand;
import com.barrybecker4.game.multiplayer.poker.ui.render.HandRenderer;

import javax.swing.*;
import java.awt.*;

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

