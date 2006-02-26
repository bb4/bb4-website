package com.becker.puzzle.set;


import com.becker.game.common.*;

import javax.swing.*;
import java.awt.*;
import java.text.*;
import java.util.List;

/**
 *  Shows the current cards in the Set Game.
 *
 */
final class SetGameViewer extends JPanel
{

    private static final int INC = 10;

    private static final int LEFT_MARGIN = 10;
    private static final int TOP_MARGIN = 10;

    private static final Color BACKGROUND_COLOR = new Color(180, 170, 200);
    private static final float CARD_HEIGHT_RAT = 1.5f;

    private NumberFormat formatter_;

    List<Card> deck_;
    int numCardsShown_ = 12;

    // Constructor.
    SetGameViewer()
    {
        deck_ = Card.newDeck();
        //setPreferredSize(new Dimension( board.getSize() * INC, board.getSize() * INC ));
        formatter_ = new DecimalFormat();
        formatter_.setGroupingUsed(true);
        formatter_.setMaximumFractionDigits(0);
    }

    public void startNewGame() {
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
        }else if (rat < 3.4) {
            numColumns = 10;
        }
        return numColumns;
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

        int cardWidth = getCanvasWidth() / numCols;
        int cardHeight = (int) (cardWidth * CARD_HEIGHT_RAT);

        for (i = 0; i<numCardsShown_; i++ ) {
            int row = i / numCols;
            int col = i % numCols;
            int rowPos = col * cardWidth + LEFT_MARGIN;
            int colPos = row * cardHeight + TOP_MARGIN;
            CardRenderer.render((Graphics2D) g, deck_.get(i),
                                new Location(rowPos, colPos), cardWidth, cardHeight, false);
        }
    }
}

