package com.becker.game.multiplayer.poker.ui;

import com.becker.game.common.*;
import com.becker.game.common.ui.GamePieceRenderer;
import com.becker.game.common.ui.GameBoardViewer;
import com.becker.game.multiplayer.poker.PokerPlayerMarker;
import com.becker.game.multiplayer.poker.PokerHand;
import com.becker.game.card.Card;
import com.becker.java2d.RoundGradientPaint;
import com.becker.ui.GUIUtil;

import javax.swing.*;
import java.awt.*;
import java.awt.font.LineMetrics;
import java.awt.geom.Rectangle2D;
import java.awt.geom.Ellipse2D;
import java.text.NumberFormat;


/**
 *  a singleton class that takes a poker player and renders it for the PokerGameViewer.
 * @see com.becker.game.multiplayer.poker.PokerTable
 * @author Barry Becker
 */
public class PokerRenderer extends GamePieceRenderer
{
    private static GamePieceRenderer renderer_ = null;


    public static final Color HIGHLIGHT_COLOR = new Color(245, 255, 0, 50);
    private static final BasicStroke HIGHLIGHT_STROKE = new BasicStroke(2);
    private static final Color FOLDED_COLOR = new Color(50, 50, 55, 30);

     // instead of rendering we can just show image icons which look even better.
    // @@ should we instead maintain an array of images indexed by type and player?
    private static ImageIcon[] suitImages_ = new ImageIcon[Card.Suit.values().length];

    private static final String IMAGE_DIR = GameContext.GAME_ROOT+"multiplayer/poker/ui/images/";
    static {
        // gets the images from resources or the filesystem depending if we are running as an applet or application respectively.
        suitImages_[Card.Suit.CLUBS.ordinal()] = GUIUtil.getIcon(IMAGE_DIR+"club_small.gif");
        suitImages_[Card.Suit.SPADES.ordinal()] = GUIUtil.getIcon(IMAGE_DIR+"spade_small.gif");
        suitImages_[Card.Suit.HEARTS.ordinal()] = GUIUtil.getIcon(IMAGE_DIR+"heart_small.gif");
        suitImages_[Card.Suit.DIAMONDS.ordinal()] = GUIUtil.getIcon(IMAGE_DIR+"diamond_small.gif");
    }

    /**
     * private constructor because this class is a singleton.
     * Use getPieceRenderer instead
     */
    private PokerRenderer()
    {}

    public static GamePieceRenderer getRenderer()
    {
        if (renderer_ == null)
            renderer_ = new PokerRenderer();
        return renderer_;
    }

    protected int getPieceSize(int cellSize, GamePiece piece)
    {
        int pieceSize = (int) (.85f * cellSize * 2);
        return pieceSize;
    }

    /**
     * this draws the actual piece at this location (if there is one).
     * Uses the RoundGradientFill from Knudsen to put a specular highlight on the planet.
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
        int hlOffset = (int) (pieceSize / 2.3 + .5);  //spec highlight offset
        Color c= playerMarker.getColor();

        RoundGradientPaint rgp = new RoundGradientPaint(
                pos.x + hlOffset, pos.y + hlOffset, Color.white, SPEC_HIGHLIGHT_RADIUS, c );

        g2.setPaint( rgp );
        g2.fill( circle );

        if ( playerMarker.isHighlighted() ) {
            //g2.setStroke(HIGHLIGHT_STROKE);
            g2.setColor( HIGHLIGHT_COLOR );
            g2.fillOval( pos.x, pos.y, 3*pieceSize , 3*pieceSize );
        }


        Font font = BASE_FONT.deriveFont(Font.BOLD, cellSize/(float)GameBoardViewer.MINIMUM_CELL_SIZE  * 8);
        int offset = (pieceSize<(.6*cellSize))? -1 : cellSize/5;
        if ( playerMarker.getAnnotation() != null ) {
            g2.setColor( Color.black );
            g2.setFont( font );
            g2.drawString( playerMarker.getAnnotation(), pos.x - 2*cellSize, pos.y - 3*offset);
        }

        //System.out.println("location ="+position.getLocation());
        if (!playerMarker.getOwner().hasFolded())
            renderHand(g2, position.getLocation(), playerMarker.getOwner().getHand(), cellSize);
        else {
            // they have folded. Cover with a gray rectangle to indicate.
            g2.setColor(FOLDED_COLOR);
            g2.fillRect( pos.x - cellSize, pos.y - cellSize, 6*pieceSize , 6*pieceSize );
        }

        renderChips(g2, position.getLocation(), playerMarker.getOwner().getCash(), cellSize);
    }


    private static final float CARD_WIDTH = 1.7f;
    private static final float CARD_HEIGHT = 3.0f;
    private static final float CARD_ARC = 0.28f;
    private static final int POKER_CARD_FONT_SIZE = 11;
    private static Font POKER_CARD_FONT = new Font( "Sans-serif", Font.PLAIN, POKER_CARD_FONT_SIZE );
    private static final Color CARD_BG_COLOR = Color.white;
    private static final Color CARD_BACK_COLOR = new Color(170, 220, 255);
    private static final Color RED_COLOR = new Color(200, 0, 0);
    private static final Color BLACK_COLOR   = Color.black;

    public void renderHand(Graphics2D g2, Location location, PokerHand hand, int cellSize) {

        assert (hand!=null): "Did you forget to deal cards to one of the players?";
        int x = (int) ((location.col-1) * cellSize);
        int y = (int) ((location.row + 1.6) * cellSize);
        int cardArc = (int)(cellSize * CARD_ARC);
        Font font = POKER_CHIP_FONT.deriveFont(cellSize/(float)GameBoardViewer.MINIMUM_CELL_SIZE  * POKER_CARD_FONT_SIZE);

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
                ImageIcon imgIcon = suitImages_[c.suit().ordinal()];
                float rat = imgIcon.getIconHeight() / imgIcon.getIconWidth();
                w = (int)(cellSize * .85 *CARD_WIDTH);
                g2.drawImage(imgIcon.getImage(), (int)(x + .7*cardArc), (int)(y + cellSize*(CARD_HEIGHT/2)),
                             w, (int)(rat * w), null);

                g2.setFont(font);
                g2.setColor((c.suit() == Card.Suit.HEARTS || c.suit() == Card.Suit.DIAMONDS)? RED_COLOR : BLACK_COLOR);

                String symbol = c.rank().getSymbol();
                Rectangle2D r = font.getStringBounds(symbol, g2.getFontRenderContext());
                double symbWidth = r.getWidth();
                g2.drawString(symbol, x + (int)((cellSize * CARD_WIDTH - symbWidth)/1.7), (int)(y + .7*r.getHeight() + cardArc));
            }

            x += cellSize * CARD_WIDTH;
        }
    }


    private static final double CHIP_PILE_WIDTH = .9;
    private static final double CHIP_HEIGHT = .15;
    private static final int POKER_CHIP_FONT_SIZE = 6;
    private static final Font POKER_CHIP_FONT = new Font( "Sans-serif", Font.PLAIN, POKER_CHIP_FONT_SIZE );

    public void renderChips(Graphics2D g2, Location location, int amount, int cellSize) {
        int[] numChips = PokerChip.getChips(amount);
        int i,x=0, width=0, height=0, firstNonZeroPile=0;
        int y = (int)(cellSize * (location.row));
        //System.out.println("chips stacks = "+numChips[1]+" "+numChips[2] + " "+numChips[3] + " "+numChips[4]);
        for (i=0; i<numChips.length; i++) {

            if (numChips[i] > 0) {
                if (firstNonZeroPile == 0) {
                   firstNonZeroPile = i;
                }
                height = (int)(cellSize * numChips[i] * CHIP_HEIGHT);
                width = (int)(CHIP_PILE_WIDTH * cellSize);
                g2.setColor(PokerChip.values()[i].getColor());
                x = (int)(((float)i*CHIP_PILE_WIDTH + location.col +1) * cellSize);
                y = location.row * cellSize - height;
                //System.out.println("x="+x+"  y="+y+"   w="+width+" ht="+height);
                g2.fillRect(x, y, width, height);
                g2.setColor(BLACK_COLOR);
                g2.drawRect(x, y, width, height);
                for (int j=1; j<numChips[i]; j++) {
                     y = (int)(cellSize * (location.row - j * CHIP_HEIGHT));
                     g2.drawLine(x, y, (int)(x + cellSize*CHIP_PILE_WIDTH), y);
                }
            }

        }
        // amount of cash represented by chips
        g2.setColor(BLACK_COLOR);
        NumberFormat currencyFormat = NumberFormat.getCurrencyInstance(GameContext.getDefaultLocaleType().getLocale());
        String cashAmount = currencyFormat.format(amount);
        x = (int)((location.col +1 +firstNonZeroPile*CHIP_PILE_WIDTH) * cellSize);
        Font f = POKER_CHIP_FONT.deriveFont(cellSize/(float)GameBoardViewer.MINIMUM_CELL_SIZE*POKER_CHIP_FONT_SIZE);
        g2.setFont(f);
        g2.drawString(cashAmount, x , (int)(y + height + cellSize/1.2));
    }
}
