package com.becker.game.multiplayer.set.ui;


import com.becker.game.common.*;
import com.becker.game.common.ui.*;
import com.becker.game.multiplayer.common.online.SurrogateMultiPlayer;
import com.becker.game.multiplayer.set.*;

import com.becker.game.multiplayer.common.MultiGameController;
import com.becker.game.multiplayer.common.ui.MultiGameViewer;
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.text.*;
import java.util.*;
import java.util.List;

/**
 *  Shows the current cards in the Set Game in a canvas.
 *
 * @author Barry Becker
 */
public final class SetGameViewer extends MultiGameViewer
                                 implements MouseMotionListener
{

    private Card currentlyHighlightedCard_ = null;


    /**
     * Constructor
     */
    SetGameViewer()
    {
        NumberFormat formatter_=new DecimalFormat();
        formatter_.setGroupingUsed(true);
        formatter_.setMaximumFractionDigits(0);

        this.addMouseMotionListener(this);
    }


    @Override
    public void startNewGame() {
        controller_.reset();
    }

    /**
     * @return the game specific controller for this viewer.
     */
    @Override
    protected MultiGameController createController() {
        return new SetController();
    }

    @Override
    protected GameBoardRenderer getBoardRenderer() {
        return SetGameRenderer.getRenderer();
    }


    /**
     * make the computer move and show it on the screen.
     *
     * @param player computer player to move
     * @return done return true if the game is over after moving
     */
    @Override
    public boolean doComputerMove(Player player)
    {
        assert false : " no computer player for set yet. coming soon!";
        return false;
    }

    /**
     * make the computer move and show it on the screen.
     *
     * @param player computer player to move
     * @return done return true if the game is over after moving
     */
    @Override
    public boolean doSurrogateMove(SurrogateMultiPlayer player)
    {
        assert false : " no online pla for set yet. coming soon!";
        return false;
    }

    @Override
    public String getGameOverMessage() {
        SetPlayer winner =  ((SetController) controller_).determineWinner();
        return "the game is over. The winner is " + winner.getName() + " with " + winner.getNumSetsFound() + "sets";
    }


    public void mouseMoved(MouseEvent e) {
        Card card =
             ((SetGameRenderer)getBoardRenderer()).findCardOver(controller_, e.getX(), e.getY(),
                                                                getWidth(), getHeight());

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

            this.repaint();
        }
    }

    public List<Card> getSelectedCards() {

        List<Card> selected = new ArrayList<Card>();
        SetController c = (SetController)controller_;

        for (int i = 0; i<c.getNumCardsShowing(); i++ ) {
            Card card = c.getDeck().get(i);
            if (card.isSelected()) {
                selected.add(card);
            }
        }
        return selected;
    }

    private static void deselectCards(List<Card> cards) {

         for (int i = 0; i < cards.size(); i++) {
             cards.get(i).setSelected(false);
         }
    }

    /**
     * draw a grid of some sort if there is one.
     * none by default.
     */
    protected void drawGrid(Graphics2D g2, int startPos, int rightEdgePos, int bottomEdgePos, int start,
                            int nrows1, int ncols1, int gridOffset) {}


    public void mouseDragged(MouseEvent e) {}

    @Override
    public void mouseClicked(MouseEvent e) {

        SetController c = (SetController)controller_;

        if (c.getCurrentPlayer() == null) {
            JOptionPane.showMessageDialog(this, "Before you can select a set, you must specify a player on the right.");
            return;
        }
        Card card = ((SetGameRenderer)getBoardRenderer()).findCardOver(c, e.getX(), e.getY(), getWidth(), getHeight());
        if (card != null) {
           card.toggleSelect();
           this.repaint();
        }
        // if there are 3 cards selected. Check to see if it constitutes a set.
        // if it does, show a message to that effect, unselect them, delete them and add 3 more from the deck.
        // if not, then show a message, and deselect them.
        List<Card> selectedCards = getSelectedCards();
        if (selectedCards.size() == 3) {

            SetPlayer p = ((SetPlayer)c.getCurrentPlayer());
            if (Card.isSet(selectedCards)) {
                JOptionPane.showMessageDialog(this, "Congratulations, you found a set!");

                p.incrementNumSetsFound();
                c.removeCards(selectedCards);
                c.addCards(3);

            } else {
                JOptionPane.showMessageDialog(this, "NO! that is not a set.");
                p.decrementNumSetsFound();
                c.gameChanged();
            }
            deselectCards(selectedCards);
            c.setCurrentPlayer(null);
            this.repaint();
        }
    }

    /**
     * @return the tooltip for the panel given a mouse event
     */
    @Override
    public String getToolTipText( MouseEvent e )
    {
        return null;
    }
}

