package com.becker.game.multiplayer.galactic.ui;

import com.becker.game.common.*;
import com.becker.game.common.ui.GamePieceRenderer;
import com.becker.game.multiplayer.galactic.Planet;
import com.becker.java2d.RoundGradientPaint;

import java.awt.*;
import java.awt.geom.Ellipse2D;

/**
 *  a singleton class that takes a checkers piece and renders it for the CheckersBoardViewer.
 * @see com.becker.game.multiplayer.galactic.Galaxy
 * @author Barry Becker
 */
public class PlanetRenderer extends GamePieceRenderer
{
    private static GamePieceRenderer renderer_ = null;

    private static final Color ATTACK_COLOR = new Color(255, 100, 0);

    private static final BasicStroke ATTACK_STROKE = new BasicStroke(3);
    /**
     * private constructor because this class is a singleton.
     * Use getPieceRenderer instead
     */
    private PlanetRenderer()
    {}

    public static GamePieceRenderer getRenderer()
    {
        if (renderer_ == null)
            renderer_ = new PlanetRenderer();
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
        Planet planet = (Planet)position.getPiece();
        if (planet == null)
            return; // nothing to render


        int pieceSize = getPieceSize(cellSize, planet);
        Point pos = getPosition(position, cellSize, pieceSize);
        Ellipse2D circle = new Ellipse2D.Float( pos.x, pos.y, pieceSize + 1, pieceSize + 1 );
        int hlOffset = (int) (pieceSize / 2.3 + .5);  //spec highlight offset
        Color c= planet.getColor();

        RoundGradientPaint rgp = new RoundGradientPaint(
                pos.x + hlOffset, pos.y + hlOffset, Color.white, SPEC_HIGHLIGHT_RADIUS, c );

        g2.setPaint( rgp );
        g2.fill( circle );

        if ( planet.isUnderAttack() ) {
            g2.setStroke(ATTACK_STROKE);
            g2.setColor( ATTACK_COLOR );
            g2.drawOval( pos.x, pos.y, pieceSize + 1, pieceSize + 1 );
        }

        int offset = (pieceSize<(.6*cellSize))? -1 : cellSize/5;
        if ( planet.getAnnotation() != null ) {
                g2.setColor( Color.black );
                g2.setFont( BASE_FONT );
                g2.drawString( planet.getAnnotation(), pos.x + 2*offset, pos.y + 3*offset);
        }
    }
}
