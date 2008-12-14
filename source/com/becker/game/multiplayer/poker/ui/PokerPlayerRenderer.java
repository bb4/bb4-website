package com.becker.game.multiplayer.poker.ui;

import com.becker.game.card.*;
import com.becker.game.common.*;
import com.becker.game.common.ui.*;
import com.becker.game.multiplayer.poker.*;
import com.becker.java2d.*;
import com.becker.ui.*;
import com.becker.common.*;

import com.becker.game.multiplayer.poker.player.PokerPlayer;
import com.becker.game.twoplayer.common.ui.TwoPlayerBoardRenderer;

import javax.swing.*;
import java.awt.*;
import java.awt.geom.*;
import java.text.*;


/**
 *  A singleton class that takes a poker player and renders it for the PokerGameViewer.
 *
 * @see com.becker.game.multiplayer.poker.PokerTable
 * @author Barry Becker
 */
public class PokerPlayerRenderer extends GamePieceRenderer
{
    private static GamePieceRenderer renderer_ = null;


    public static final Color HIGHLIGHT_COLOR = new Color(245, 255, 0, 50);
    private static final Color FOLDED_COLOR = new Color(50, 50, 55, 30);

    // the suit images
    private static ImageIcon[] suitImages_ = new ImageIcon[Card.Suit.values().length];

    private static final String IMAGE_DIR = GameContext.GAME_ROOT + "multiplayer/poker/ui/images/";
    static {
        // gets the images from resources or the filesystem
        // depending if we are running as an applet or application respectively.
        suitImages_[Card.Suit.CLUBS.ordinal()] = GUIUtil.getIcon(IMAGE_DIR + "club_small.gif");
        suitImages_[Card.Suit.SPADES.ordinal()] = GUIUtil.getIcon(IMAGE_DIR + "spade_small.gif");
        suitImages_[Card.Suit.HEARTS.ordinal()] = GUIUtil.getIcon(IMAGE_DIR + "heart_small.gif");
        suitImages_[Card.Suit.DIAMONDS.ordinal()] = GUIUtil.getIcon(IMAGE_DIR + "diamond_small.gif");
    }

    /**
     * private constructor because this class is a singleton.
     * Use getRenderer instead
     */
    private PokerPlayerRenderer()
    {}

    public static GamePieceRenderer getRenderer()
    {
        if (renderer_ == null)
            renderer_ = new PokerPlayerRenderer();
        return renderer_;
    }

    protected int getPieceSize(int cellSize, GamePiece piece)
    {
        int pieceSize = (int) (0.85f * cellSize * 2);
        return pieceSize;
    }

    protected Color getPieceColor(GamePiece piece) {
        PokerPlayerMarker marker = (PokerPlayerMarker)piece;
        return marker.getColor();
    }

    /**
     * this draws the actual player marker, cards, and chips at this location (if there is one).
     *
     * @param g2 graphics context
     * @param position the position of the piece to render
     */
    public void render( Graphics2D g2, BoardPosition position, int cellSize, Board b)
    {
        PokerPlayerMarker playerMarker = (PokerPlayerMarker)position.getPiece();
        if (playerMarker == null)
            return; // nothing to render


        int pieceSize = getPieceSize(cellSize, playerMarker);
        Point pos = getPosition(position, cellSize, pieceSize);
        Ellipse2D circle = new Ellipse2D.Float( pos.x, pos.y, pieceSize + 1, pieceSize + 1 );
        int hlOffset = (int) (pieceSize / 2.3 + 0.5);  //spec highlight offset
        Color c= getPieceColor(playerMarker);

        RoundGradientPaint rgp = new RoundGradientPaint(
                pos.x + hlOffset, pos.y + hlOffset, Color.white, SPEC_HIGHLIGHT_RADIUS, c );

        g2.setPaint( rgp );
        g2.fill( circle );

        if ( playerMarker.isHighlighted() ) {
            //g2.setStroke(HIGHLIGHT_STROKE);
            g2.setColor( HIGHLIGHT_COLOR );
            g2.fillOval( pos.x, pos.y, 3*pieceSize , 3*pieceSize );
        }

        Font font = BASE_FONT.deriveFont(Font.BOLD, (float) cellSize /
                    TwoPlayerBoardRenderer.MINIMUM_CELL_SIZE  * 8);
        int offset = (pieceSize<(0.6*cellSize))? -1 : cellSize/5;
        if ( playerMarker.getAnnotation() != null ) {
            g2.setColor( Color.black );
            g2.setFont( font );
            g2.drawString( playerMarker.getAnnotation(), pos.x - cellSize, pos.y - 3*offset);
        }

        PokerPlayer p = (PokerPlayer)playerMarker.getOwner();
        if (!p.hasFolded())
            renderHand(g2, position.getLocation(), p.getHand(), cellSize);
        else {
            // they have folded. Cover with a gray rectangle to indicate.
            g2.setColor(FOLDED_COLOR);
            g2.fillRect( pos.x - cellSize, pos.y - cellSize, 6*pieceSize , 6*pieceSize );
        }

        renderChips(g2, position.getLocation(), p.getCash(), cellSize);
    }


    private static final float CARD_WIDTH = 1.7f;
    private static final float CARD_HEIGHT = 3.3f;
    private static final float CARD_ARC = 0.28f;
    private static final int POKER_CARD_FONT_SIZE = 10;
    //private static final Font POKER_CARD_FONT = new Font( "Sans-serif", Font.PLAIN, POKER_CARD_FONT_SIZE );
    private static final Color CARD_BG_COLOR = Color.white;
    private static final Color CARD_BACK_COLOR = new Color(170, 220, 255);
    private static final Color RED_COLOR = new Color(200, 0, 0);
    private static final Color BLACK_COLOR   = Color.black;

    /**
     * Draw the poker hand (the cards are all face up or all face down)
     */
    public void renderHand(Graphics2D g2, Location location, PokerHand hand, int cellSize) {

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
                                         int cellSize, int cardArc, Card c)
    {
        Font font = POKER_CHIP_FONT.deriveFont((float) cellSize /
                    TwoPlayerBoardRenderer.MINIMUM_CELL_SIZE  * POKER_CARD_FONT_SIZE);

        ImageIcon imgIcon = suitImages_[c.suit().ordinal()];
        float rat = (float) imgIcon.getIconHeight() / imgIcon.getIconWidth();
        int imageWidth = (int)(cellSize * 0.82 *CARD_WIDTH);

        g2.drawImage(imgIcon.getImage(), (int)(x + 0.7*cardArc), (int)(y + cellSize*(CARD_HEIGHT/2.16)),
                     imageWidth, (int)(rat * imageWidth), null);

        g2.setFont(font);
        g2.setColor((c.suit() == Card.Suit.HEARTS || c.suit() == Card.Suit.DIAMONDS)? RED_COLOR : BLACK_COLOR);

        String symbol = c.rank().getSymbol();
        Rectangle2D r = font.getStringBounds(symbol, g2.getFontRenderContext());
        double symbWidth = r.getWidth();
        g2.drawString(symbol, x + (int)((cellSize * CARD_WIDTH - symbWidth)/1.7), (int)(y + 0.7*r.getHeight() + cardArc));
    }


    private static final double CHIP_PILE_WIDTH = 0.9;
    private static final double CHIP_HEIGHT = 0.15;
    private static final int POKER_CHIP_FONT_SIZE = 6;
    private static final Font POKER_CHIP_FONT = new Font( "Sans-serif", Font.PLAIN, POKER_CHIP_FONT_SIZE );


    public void renderChips(Graphics2D g2, Location location, int amount, int cellSize) {
        int[] numChips = PokerChip.getChips(amount);
        int i,x, width, height=0, firstNonZeroPile=0;
        int y = (cellSize * (location.getRow()));
        //System.out.println("chips stacks = "+numChips[1]+" "+numChips[2] + " "+numChips[3] + " "+numChips[4]);
        for (i=0; i<numChips.length; i++) {

            if (numChips[i] > 0) {
                if (firstNonZeroPile == 0) {
                   firstNonZeroPile = i;
                }
                height = (int)(cellSize * numChips[i] * CHIP_HEIGHT);
                width = (int)(CHIP_PILE_WIDTH * cellSize);
                g2.setColor(PokerChip.values()[i].getColor());
                x = (int)(((float)i*CHIP_PILE_WIDTH + location.getCol() +1) * cellSize);
                y = location.getRow() * cellSize - height;
                //System.out.println("x="+x+"  y="+y+"   w="+width+" ht="+height);
                g2.fillRect(x, y, width, height);
                g2.setColor(BLACK_COLOR);
                g2.drawRect(x, y, width, height);
                for (int j=1; j<numChips[i]; j++) {
                     y = (int)(cellSize * (location.getRow() - j * CHIP_HEIGHT));
                     g2.drawLine(x, y, (int)(x + cellSize*CHIP_PILE_WIDTH), y);
                }
            }

        }
        // amount of cash represented by chips
        g2.setColor(BLACK_COLOR);
        NumberFormat currencyFormat = NumberFormat.getCurrencyInstance(GameContext.getDefaultLocaleType().getLocale());
        String cashAmount = currencyFormat.format(amount);
        x = (int)((location.getCol() +1 +firstNonZeroPile*CHIP_PILE_WIDTH) * cellSize);
        Font f = POKER_CHIP_FONT.deriveFont((float) cellSize /
                 TwoPlayerBoardRenderer.MINIMUM_CELL_SIZE*POKER_CHIP_FONT_SIZE);
        g2.setFont(f);
        g2.drawString(cashAmount, x , (int)(y + height + cellSize/1.2));
    }
}
