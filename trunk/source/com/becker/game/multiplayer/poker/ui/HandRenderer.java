// Copyright by Barry G. Becker, 2012. Licensed under MIT License: http://www.opensource.org/licenses/MIT
package com.becker.game.multiplayer.poker.ui;

import com.becker.common.geometry.Location;
import com.becker.game.card.Card;
import com.becker.game.card.Suit;
import com.becker.game.common.GameContext;
import com.becker.game.multiplayer.poker.PokerHand;
import com.becker.game.twoplayer.common.ui.TwoPlayerBoardRenderer;
import com.becker.ui.util.GUIUtil;

import javax.swing.*;
import java.awt.*;
import java.awt.geom.Rectangle2D;

import static com.becker.game.multiplayer.poker.ui.PokerPlayerRenderer.POKER_FONT;

/**
 *  A singleton class renders a playing card.
 *
 * @author Barry Becker
 */
public class HandRenderer  {

    // the suit images
    private static ImageIcon[] suitImages_ = new ImageIcon[Suit.values().length];

    private static final String IMAGE_DIR = GameContext.GAME_ROOT + "multiplayer/poker/ui/images/";
    static {
        // gets the images from resources or the filesystem
        // depending if we are running as an applet or application respectively.
        suitImages_[Suit.CLUBS.ordinal()] = GUIUtil.getIcon(IMAGE_DIR + "club_small.gif");
        suitImages_[Suit.SPADES.ordinal()] = GUIUtil.getIcon(IMAGE_DIR + "spade_small.gif");
        suitImages_[Suit.HEARTS.ordinal()] = GUIUtil.getIcon(IMAGE_DIR + "heart_small.gif");
        suitImages_[Suit.DIAMONDS.ordinal()] = GUIUtil.getIcon(IMAGE_DIR + "diamond_small.gif");
    }

    private static final float CARD_WIDTH = 1.7f;
    private static final float CARD_HEIGHT = 3.3f;
    private static final float CARD_ARC = 0.28f;
    private static final int POKER_CARD_FONT_SIZE = 10;
    private static final Color CARD_BG_COLOR = Color.white;
    private static final Color CARD_BACK_COLOR = new Color(170, 220, 255);
    private static final Color RED_COLOR = new Color(200, 0, 0);
    private static final Color BLACK_COLOR   = Color.black;

    /**
     * Create an instance
     */
    public HandRenderer() {}

    /**
     * Draw the poker hand (the cards are all face up or all face down)
     */
    public void render( Graphics2D g2, Location location, PokerHand hand, int cellSize) {

        assert (hand!=null): "Did you forget to deal cards to one of the players?";
        int x = ((location.getCol()-1) * cellSize);
        int y = (int) ((location.getRow() + 1.6) * cellSize);
        int cardArc = (int)(cellSize * CARD_ARC);

        for (Card c : hand.getCards()) {
            if (hand.isFaceUp())  {
                g2.setColor(CARD_BG_COLOR);
            } else {
                g2.setColor(CARD_BACK_COLOR);
            }
            int w = (int)(cellSize *(CARD_WIDTH + CARD_ARC));
            int h = (int)(cellSize*CARD_HEIGHT);
            g2.fillRoundRect(x, y, w, h, cardArc, cardArc);
            g2.setColor(BLACK_COLOR);
            g2.drawRoundRect(x, y, w, h, cardArc, cardArc);

            if (hand.isFaceUp()) {
                renderFaceUpCard(g2, x, y,  cellSize, cardArc, c);
            }

            x += cellSize * CARD_WIDTH;
        }
    }

    /**
     *  Draw the card face up.
     */
    private static void renderFaceUpCard(Graphics2D g2, int x, int y,
                                         int cellSize, int cardArc, Card c) {
        Font font = POKER_FONT.deriveFont((float) cellSize /
                    TwoPlayerBoardRenderer.MINIMUM_CELL_SIZE  * POKER_CARD_FONT_SIZE);

        ImageIcon imgIcon = suitImages_[c.suit().ordinal()];
        float rat = (float) imgIcon.getIconHeight() / imgIcon.getIconWidth();
        int imageWidth = (int)(cellSize * 0.82 *CARD_WIDTH);

        g2.drawImage(imgIcon.getImage(), (int)(x + 0.7*cardArc), (int)(y + cellSize*(CARD_HEIGHT/2.16)),
                     imageWidth, (int)(rat * imageWidth), null);

        g2.setFont(font);
        g2.setColor((c.suit() == Suit.HEARTS || c.suit() == Suit.DIAMONDS)? RED_COLOR : BLACK_COLOR);

        String symbol = c.rank().getSymbol();
        Rectangle2D r = font.getStringBounds(symbol, g2.getFontRenderContext());
        double symbWidth = r.getWidth();
        g2.drawString(symbol, x + (int)((cellSize * CARD_WIDTH - symbWidth)/1.7), (int)(y + 0.7*r.getHeight() + cardArc));
    }
}
