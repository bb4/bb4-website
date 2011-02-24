package com.becker.game.multiplayer.set.ui;

import com.becker.game.common.ui.viewer.GameBoardViewer;
import com.becker.game.common.ui.viewer.ViewerMouseListener;
import com.becker.game.multiplayer.set.Card;
import com.becker.game.multiplayer.set.SetController;
import com.becker.game.multiplayer.set.SetPlayer;

import javax.swing.*;
import java.awt.event.MouseEvent;
import java.util.List;

/**
 *  Mouse handling for set game.
 *
 *  @author Barry Becker
 */
public class SetViewerMouseListener extends ViewerMouseListener {

    private Card currentlyHighlightedCard_ = null;

    /**
     * Constructor.
     */
    public SetViewerMouseListener(GameBoardViewer viewer) {
        super(viewer);
    }


    /**
     * make the human move and show it on the screen,
     * then depending on the options, the computer may move.
     */
    @Override
    public void mouseClicked(MouseEvent e) {

        SetGameViewer viewer = (SetGameViewer)viewer_;

        if (playerSelected()) {

            SetGameRenderer renderer = (SetGameRenderer)viewer.getBoardRenderer();
            Card card = renderer.findCardOver(viewer_.getController(), e.getX(), e.getY(),
                                              viewer.getWidth(), viewer.getHeight());
            if (card != null) {
               card.toggleSelect();
               viewer_.repaint();
            }
            notifyIfSetSelected();
        }
    }


    @Override
    public void mouseMoved(MouseEvent e) {

        SetGameViewer viewer = (SetGameViewer)viewer_;
        SetGameRenderer renderer = (SetGameRenderer)viewer.getBoardRenderer();
        Card card =
             renderer.findCardOver(viewer.getController(), e.getX(), e.getY(),
                                   viewer.getWidth(), viewer.getHeight());

        boolean changed = card != currentlyHighlightedCard_;

        if (changed) {

            if (currentlyHighlightedCard_ != null) {
                currentlyHighlightedCard_.setHighlighted(false);
            }
            if (card != null) {
                currentlyHighlightedCard_ = card;
                currentlyHighlightedCard_.setHighlighted(true);
            } else {
                currentlyHighlightedCard_ = null;
            }

            viewer.repaint();
        }
    }


    /**
     * A player must be selected before you can try to select a set.
     * @return true if a player is currently selected.
     */
    private boolean playerSelected() {
        if (viewer_.getController().getCurrentPlayer() == null) {
            JOptionPane.showMessageDialog(viewer_,
                    "Before you can select a set, you must specify a player on the right.");
            return false;
        }
        return true;
    }


    /**
     * if there are 3 cards selected. Check to see if it constitutes a set.
     * if it does, show a message to that effect, unselect them, delete them and add 3 more from the deck.
     * if not, then show a message, and deselect them.
     */
    private void notifyIfSetSelected() {

        SetGameViewer viewer = (SetGameViewer)viewer_;
        SetController c = (SetController)viewer.getController();

        List<Card> selectedCards = viewer.getSelectedCards();
        if (selectedCards.size() == 3) {

            SetPlayer p = ((SetPlayer)c.getCurrentPlayer());
            if (Card.isSet(selectedCards)) {
                JOptionPane.showMessageDialog(viewer_, "Congratulations, you found a set!");

                p.incrementNumSetsFound();
                c.removeCards(selectedCards);
                c.addCards(3);

            } else {
                JOptionPane.showMessageDialog(viewer_, "NO! that is not a set.");
                p.decrementNumSetsFound();
                c.gameChanged();
            }
            deselectCards(selectedCards);
            c.setCurrentPlayer(null);
            viewer_.repaint();
        }
    }

    private void deselectCards(List<Card> cards) {

        for (Card card : cards) {
            card.setSelected(false);
        }
    }

}