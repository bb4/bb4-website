package com.becker.puzzle.set;


import com.becker.game.common.*;

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
final class SetGameViewer extends JPanel
                          implements MouseMotionListener, MouseListener
{

    private static final int LEFT_MARGIN = 10;
    private static final int TOP_MARGIN = 10;

    private static final Color BACKGROUND_COLOR = new Color(200, 200, 210);
    private static final float CARD_HEIGHT_RAT = 1.5f;

    private NumberFormat formatter_;
    private Card currentlyHighlightedCard_ = null;

    List<Card> deck_;
    int numCardsShown_ = 12;

    // Constructor.
    SetGameViewer()
    {
        deck_ = Card.newDeck();
        formatter_ = new DecimalFormat();
        formatter_.setGroupingUsed(true);
        formatter_.setMaximumFractionDigits(0);
        this.addMouseMotionListener(this);
        this.addMouseListener(this);

    }

    /*
    public Dimension getPreferredSize() {
        return new Dimension(150, 10000);
    }*/

    public void startNewGame() {
    }


    public void addCard() {
        if (hasCardsToAdd()) {
            numCardsShown_++;
        }
    }

    public void addCards(int num) {
        for (int i=0; i<num; i++) {
            addCard();
        }
    }

    public void removeCard() {
        if (canRemoveCards()) {
            numCardsShown_--;
        }
    }

    public boolean canRemoveCards() {
        return (numCardsShown_ > 3);
    }

    public boolean hasCardsToAdd() {
        return (numCardsShown_ < deck_.size() && numCardsShown_ < 20);
    }

    public int getCanvasWidth() {
        return getWidth() - 2 * LEFT_MARGIN;
    }

    public int getNumColumns() {
        float rat = (float) getCanvasWidth() / (getHeight() - 2 * TOP_MARGIN);
        //System.out.println("rat=" + rat);

        int numColumns = 20;
        if (rat < 0.05) {
            numColumns = 1;
        } else if (rat < 0.15) {
            numColumns = 2;
        } else if (rat < 0.3) {
            numColumns = 3;
        } else if (rat < 0.6) {
            numColumns = 4;
        } else if (rat < 0.9) {
            numColumns = 5;
        } else if (rat < 1.2) {
            numColumns = 6;
        } else if (rat < 2.0) {
            numColumns = 7;
        } else if (rat < 3.4) {
            numColumns = 10;
        }
        return numColumns;
    }

    private Dimension calcCardDimension(int numCols) {
        int cardWidth = getCanvasWidth() / numCols;
        return new Dimension(cardWidth, (int) (cardWidth * CARD_HEIGHT_RAT));
    }

    /**
     * This renders the current state of the puzzle to the screen.
     * Render each card in the deck.
     */
    protected void paintComponent( Graphics g )
    {
        int i, xpos, ypos;

        super.paintComponents( g );
        // erase what's there and redraw.

        g.clearRect( 0, 0, getWidth(), getHeight() );
        g.setColor( BACKGROUND_COLOR );
        g.fillRect( 0, 0, getWidth(), getHeight() );

        int numCols = getNumColumns();

        Dimension cardDim = calcCardDimension(numCols);
        int cardWidth = (int) cardDim.getWidth();
        int cardHeight = (int) cardDim.getHeight();

        for (i = 0; i<numCardsShown_; i++ ) {
            int row = i / numCols;
            int col = i % numCols;
            int colPos = col * cardWidth + LEFT_MARGIN;
            int rowPos = row * cardHeight + TOP_MARGIN;
            CardRenderer.render((Graphics2D) g, deck_.get(i),
                                new Location(colPos, rowPos), cardWidth, cardHeight, false);
        }
    }

    /**
     * @return  the card that the mous is currently over (at x, y coordsw)
     */
    private Card findCardOver(int x, int y) {
        Card card;

        int numCols = getNumColumns();

        Dimension cardDim = calcCardDimension(numCols);
        int cardWidth = (int) cardDim.getWidth();
        int cardHeight = (int) cardDim.getHeight();

        int selectedIndex = -1;
        for (int i = 0; i<numCardsShown_; i++ ) {
            int row = i / numCols;
            int col = i % numCols;
            int colPos = col * cardWidth + LEFT_MARGIN;
            int rowPos = row * cardHeight + TOP_MARGIN;
            if (   x > colPos && x <= colPos + cardDim.getWidth()
                && y > rowPos && y <= rowPos + cardDim.getHeight()) {
                selectedIndex = i;
                break;
            }
        }
        if (selectedIndex == -1) {
            return null;
        }
        return deck_.get(selectedIndex);
    }

    public void mouseDragged(MouseEvent e) {}

    public void mouseMoved(MouseEvent e) {
        Card card = findCardOver(e.getX(), e.getY());

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

        for (int i = 0; i<numCardsShown_; i++ ) {
            Card c = deck_.get(i);
            if (c.isSelected()) {
                selected.add(c);
            }
        }
        return selected;
    }

    private static void deselectCards(List<Card> cards) {

         for (int i = 0; i < cards.size(); i++) {
             cards.get(i).setSelected(false);
         }
    }

    private void removeCards(List<Card> cards) {
          deck_.removeAll(cards);
          numCardsShown_ -= 3;
    }

    public void mouseClicked(MouseEvent e) {
        Card card = findCardOver(e.getX(), e.getY());
        if (card != null) {
           card.toggleSelect();
           this.repaint();
        }
        // if there are 3 cards selected. Check to see if it constitutes a set.
        // if it does, show a message to that effect, unselect them, delete them and add 3 more from the deck.
        // if not, then show a message, and deselect them.
        List<Card> selectedCards = getSelectedCards();
        if (selectedCards.size() == 3) {
            if (Card.isSet(selectedCards)) {
                JOptionPane.showMessageDialog(null, "Congratulations, you found a set!");
                removeCards(selectedCards);
                addCards(3);
            } else {
                JOptionPane.showMessageDialog(null, "NO! that is not a set.");
            }
            deselectCards(selectedCards);
            this.repaint();
        }
    }

    public void mousePressed(MouseEvent e) {}
    public void mouseReleased(MouseEvent e) {}
    public void mouseEntered(MouseEvent e) {}
    public void mouseExited(MouseEvent e) {}
}

