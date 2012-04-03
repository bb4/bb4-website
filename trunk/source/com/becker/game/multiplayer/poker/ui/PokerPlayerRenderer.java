/** Copyright by Barry G. Becker, 2000-2011. Licensed under MIT License: http://www.opensource.org/licenses/MIT  */
package com.becker.game.multiplayer.poker.ui;

import com.becker.game.common.board.Board;
import com.becker.game.common.board.BoardPosition;
import com.becker.game.common.board.GamePiece;
import com.becker.game.common.ui.viewer.GamePieceRenderer;
import com.becker.game.multiplayer.poker.PokerPlayerMarker;
import com.becker.game.multiplayer.poker.PokerTable;
import com.becker.game.multiplayer.poker.player.PokerPlayer;
import com.becker.game.twoplayer.common.ui.TwoPlayerBoardRenderer;
import com.becker.java2d.gradient.RoundGradientPaint;
import com.becker.ui.util.GUIUtil;

import java.awt.*;
import java.awt.geom.Ellipse2D;

/**
 *  A singleton class that takes a poker player and renders it for the PokerGameViewer.
 *
 * @see PokerTable
 * @author Barry Becker
 */
public class PokerPlayerRenderer extends GamePieceRenderer {

    static final int FONT_SIZE = 6;
    static final Font POKER_FONT = new Font(GUIUtil.DEFAULT_FONT_FAMILY, Font.PLAIN, FONT_SIZE );

    private static GamePieceRenderer renderer_ = null;

    private HandRenderer handRenderer = new HandRenderer();
    private ChipRenderer chipRenderer = new ChipRenderer();

    public static final Color HIGHLIGHT_COLOR = new Color(245, 255, 0, 50);
    private static final Color FOLDED_COLOR = new Color(50, 50, 55, 30);

    /**
     * private constructor because this class is a singleton.
     * Use getRenderer instead
     */
    private PokerPlayerRenderer()
    {}

    public static GamePieceRenderer getRenderer() {
        if (renderer_ == null)
            renderer_ = new PokerPlayerRenderer();
        return renderer_;
    }

    @Override
    protected int getPieceSize(int cellSize, GamePiece piece) {
        return (int) (0.85f * cellSize * 2);
    }

    @Override
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
    @Override
    public void render( Graphics2D g2, BoardPosition position, int cellSize, int margin, Board b) {
        PokerPlayerMarker playerMarker = (PokerPlayerMarker)position.getPiece();
        if (playerMarker == null)
            return; // nothing to render

        int pieceSize = getPieceSize(cellSize, playerMarker);
        Point pos = getPosition(position, cellSize, pieceSize, margin);
        Ellipse2D circle = new Ellipse2D.Float( pos.x, pos.y, pieceSize + 1, pieceSize + 1 );
        int hlOffset = (int) (pieceSize / 2.3 + 0.5);  //spec highlight offset
        Color c = getPieceColor(playerMarker);

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
            handRenderer.render(g2, position.getLocation(), p.getHand(), cellSize);
        else {
            // they have folded. Cover with a gray rectangle to indicate.
            g2.setColor(FOLDED_COLOR);
            g2.fillRect( pos.x - cellSize, pos.y - cellSize, 6*pieceSize , 6*pieceSize );
        }

        chipRenderer.render(g2, position.getLocation(), p.getCash(), cellSize);
    }
}
