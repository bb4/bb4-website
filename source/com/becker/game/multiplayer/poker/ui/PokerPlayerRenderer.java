package com.becker.game.multiplayer.poker.ui;

import com.becker.game.common.*;
import com.becker.game.common.ui.GamePieceRenderer;
import com.becker.game.multiplayer.galactic.Planet;
import com.becker.game.multiplayer.galactic.ui.PlanetRenderer;
import com.becker.game.multiplayer.poker.PokerPlayerMarker;
import com.becker.java2d.RoundGradientPaint;

import java.awt.*;
import java.awt.geom.Ellipse2D;

/**
 *  a singleton class that takes a poker player and renders it for the PokerGameViewer.
 * @see com.becker.game.multiplayer.poker.PokerBoard
 * @author Barry Becker
 */
public class PokerPlayerRenderer extends GamePieceRenderer
{
    private static GamePieceRenderer renderer_ = null;


    private static final Color HIGHLIGHT_COLOR = new Color(245, 255, 0);
    private static final BasicStroke HIGHLIGHT_STROKE = new BasicStroke(2);

    /**
     * private constructor because this class is a singleton.
     * Use getPieceRenderer instead
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
        Planet planet = (Planet)piece;

        // if the production capacity is proportional to the volume, then the radius
        // should be proprotional to the cube root of the production capacity.
        // Normallize by an avg production of 10.
        double rad = Math.pow(planet.getProductionCapacity(), .333)/2.1;
        int pieceSize = (int) (.85f * cellSize * rad);

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
                g2.setStroke(HIGHLIGHT_STROKE);
                g2.setColor( HIGHLIGHT_COLOR );
                g2.drawOval( pos.x, pos.y, pieceSize + 1, pieceSize + 1 );
            }


        int offset = (pieceSize<(.6*cellSize))? -1 : cellSize/5;
        if ( playerMarker.getAnnotation() != null ) {
                g2.setColor( Color.black );
                g2.setFont( BASE_FONT );
                g2.drawString( playerMarker.getAnnotation(), pos.x + 2*offset, pos.y + 3*offset);
        }
    }
}
