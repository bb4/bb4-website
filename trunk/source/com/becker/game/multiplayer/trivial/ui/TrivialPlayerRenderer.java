package com.becker.game.multiplayer.trivial.ui;

import com.becker.game.common.*;
import com.becker.game.common.ui.*;
import com.becker.game.multiplayer.trivial.player.TrivialPlayer;
import com.becker.java2d.*;
import com.becker.common.*;

import com.becker.game.multiplayer.common.MultiPlayerMarker;

import java.awt.*;
import java.awt.geom.*;


/**
 *  A singleton class that takes a trivial player and renders it for the TrivialGameViewer.
 *
 * @author Barry Becker
 */
public class TrivialPlayerRenderer extends GamePieceRenderer
{
    private static GamePieceRenderer renderer_ = null;

    public static final Color HIGHLIGHT_COLOR = new Color(245, 255, 0, 50);

    private static final float CARD_WIDTH = 2.0f;
    private static final float CARD_HEIGHT = 3.3f;
    private static final float CARD_ARC = 0.28f;
    private static final Color CARD_BG_COLOR = Color.white;
    private static final Color BLACK_COLOR   = Color.black;

    /**
     * private constructor because this class is a singleton.
     * Use getRenderer instead.
     */
    private TrivialPlayerRenderer()
    {}

    public static GamePieceRenderer getRenderer()
    {
        if (renderer_ == null) {
            renderer_ = new TrivialPlayerRenderer();
        }
        return renderer_;
    }

    @Override
    protected int getPieceSize(int cellSize, GamePiece piece)
    {
        int pieceSize = (int) (0.85f * cellSize * 2);
        return pieceSize;
    }

    @Override
    protected Color getPieceColor(GamePiece piece) {
        MultiPlayerMarker marker = (MultiPlayerMarker)piece;
        return marker.getColor();
    }

    /**
     * this draws the actual card at this location (if there is one).
     *
     * @param g2 graphics context
     * @param position the position of the piece to render
     */
    @Override
    public void render( Graphics2D g2, BoardPosition position, int cellSize, int margin, Board b)
    {
        MultiPlayerMarker playerMarker = (MultiPlayerMarker)position.getPiece();
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
            g2.setColor( HIGHLIGHT_COLOR );
            g2.fillOval( pos.x, pos.y, 3*pieceSize , 3*pieceSize );
        }

        Font font = BASE_FONT.deriveFont(Font.BOLD, (float) cellSize / (float) GameBoardRenderer.MINIMUM_CELL_SIZE  * 8);
        int offset = (pieceSize<(0.6*cellSize))? -1 : cellSize/5;
        if ( playerMarker.getAnnotation() != null ) {
            g2.setColor( Color.black );
            g2.setFont( font );
            g2.drawString( playerMarker.getAnnotation(), pos.x - cellSize, pos.y - 3*offset);
        }

        TrivialPlayer p = (TrivialPlayer)playerMarker.getOwner();
        if (p.isRevealed())
        {
             renderValue(g2, position.getLocation(), p.getValue(), cellSize);
        }
    }

    /**
     * Draw the trivial hand (the cards are all face up or all face down)
     */
    public void renderValue(Graphics2D g2, Location location, int value, int cellSize) {

        int x = ((location.getCol()-1) * cellSize);
        int y = (int) ((location.getRow() + 1.6) * cellSize);
        int cardArc = (int)(cellSize * CARD_ARC);

        g2.setColor(CARD_BG_COLOR);

        int w = (int)(cellSize *(CARD_WIDTH + CARD_ARC));
        int h = (int)(cellSize*CARD_HEIGHT);
        g2.fillRoundRect(x, y, w, h, cardArc, cardArc);
        g2.setColor(BLACK_COLOR);
        g2.drawRoundRect(x, y, w, h, cardArc, cardArc);

        g2.setColor( BLACK_COLOR );
        Font font = BASE_FONT.deriveFont(Font.BOLD, (float) cellSize / (float) GameBoardRenderer.MINIMUM_CELL_SIZE  * 9);
        g2.setFont( font );
        g2.drawString((""+value), x + cardArc, y + 4* cardArc);
    }

}
