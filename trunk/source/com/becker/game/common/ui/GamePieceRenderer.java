package com.becker.game.common.ui;

import com.becker.game.common.*;
import com.becker.java2d.RoundGradientPaint;

import java.awt.*;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Point2D;

/**
 * a singleton class that takes a game piece and renders it for the TwoPlayerBoardViewer.
 * We use a separate piece rendering class to avoid having ui in the piece class itself.
 * This allows us to more cleanly separate the client pieces from the server.
 *
 * @see com.becker.game.twoplayer.common.ui.TwoPlayerBoardViewer
 * @author Barry Becker
 */
public class GamePieceRenderer
{
    // there must be one of these for each derived class too.
    private static GamePieceRenderer renderer_ = null;

    private static final Color DEFAULT_PLAYER1_COLOR = new Color( 230, 100, 255);
    private static final Color DEFAULT_PLAYER2_COLOR = new Color( 100, 220, 255);

    private static final Color PLAYER1_TEXT_COLOR = new Color( 255, 250, 255 );
    private static final Color PLAYER2_TEXT_COLOR = new Color( 0, 50, 30 );

    protected static final Font BASE_FONT = new Font( "Sans-serif", Font.PLAIN, 11 );

    protected static final Point2D.Double SPEC_HIGHLIGHT_RADIUS = new Point2D.Double( 0, 7 );

    // use static to avoid creating a lot of new objects.
    private static Point position_ = new Point(0,0);



    /**
     * private constructor because this class is a singleton.
     * Use getPieceRenderer instead
     */
    protected GamePieceRenderer()
    {}

    public static GamePieceRenderer getRenderer()
    {
        if (renderer_ == null)
            renderer_ = new GamePieceRenderer();
        return renderer_;
    }

    /**
     *  determines what color the player1 pieces should be
     *  ignored if using icons to represent the pieces.
     */
    public Color getPlayer1Color()
    {
        return DEFAULT_PLAYER1_COLOR;
    }

    /**
     *  determines what color the player2 pieces should be
     *  ignored if using icons to represent the pieces.
     */
    public Color getPlayer2Color()
    {
        return DEFAULT_PLAYER2_COLOR;
    }


    /**
     * @return the game piece render color.
     */
    protected Color getPieceColor(GamePiece piece)
    {
        Color playerColor = null;
        Color c = null;
        if ( piece.isOwnedByPlayer1() ) {
            playerColor = getPlayer1Color();
            c = new Color( playerColor.getRed(), playerColor.getGreen(), playerColor.getBlue(),
                    255 - piece.getTransparency() );
        }
        else {
            playerColor = getPlayer2Color();
            c = new Color( playerColor.getRed(), playerColor.getGreen(), playerColor.getBlue(),
                    255 - piece.getTransparency() );
        }
        return c;
    }

    protected int getPieceSize(int cellSize, GamePiece piece)
    {
        int pieceSize = (int) (.85f * cellSize);
        // make the piece a little smaller in debug mode
        if ( GameContext.getDebugMode() > 0 )
            pieceSize = (int) (.75f * cellSize);
        return pieceSize;
    }

    /**
     * @return color for annotation text (if any).
     */
    private static Color getTextColor(GamePiece piece)
    {
        Color textColor = PLAYER2_TEXT_COLOR;
        if ( piece.isOwnedByPlayer1() ) {
            textColor = PLAYER1_TEXT_COLOR;
        }
        return textColor;
    }

    protected static Point getPosition(BoardPosition position, int cellSize, int pieceSize)
    {
        int offset = (cellSize - pieceSize) / 2;
        position_.x = GameBoardViewer.BOARD_MARGIN + cellSize*(position.getCol()-1) + offset;
        position_.y = GameBoardViewer.BOARD_MARGIN + cellSize*(position.getRow()-1) + offset;
        return position_;
    }

   /**
     * this draws the actual piece at this location (if there is one).
     * Uses the RoundGradientFill from Knudsen to put a specular highlight on the stone.
     *
     * @param g2 graphics context
     * @param position the position of the piece to render
     */
    public void render( Graphics2D g2, BoardPosition position, int cellSize)
    {
        GamePiece piece = position.getPiece();
        // if there is no piece, then nothing to render
        if (piece == null)
            return;

        int pieceSize = getPieceSize(cellSize, piece);
        Point pos = getPosition(position, cellSize, pieceSize);
        Ellipse2D circle = new Ellipse2D.Float( pos.x, pos.y, pieceSize + 1, pieceSize + 1 );
        int hlOffset = (int) (pieceSize / 2.3 + .5);  //spec highlight offset
        RoundGradientPaint rgp = new RoundGradientPaint(
                pos.x + hlOffset, pos.y + hlOffset, Color.white, SPEC_HIGHLIGHT_RADIUS, getPieceColor(piece) );

        g2.setPaint( rgp );
        g2.fill( circle );

        // only draw the outline if we are not in a debug mode.
        // when in debug mode we want to emphasize other annotations instead of the piece
        if ( piece.getTransparency() == 0 && (GameContext.getDebugMode() == 0) ) {
            g2.setColor( Color.black );
            g2.drawOval( pos.x, pos.y, pieceSize + 1, pieceSize + 1 );
        }
        int offset = (cellSize - pieceSize) / 2;
        if ( piece.getAnnotation() != null ) {
                g2.setColor( getTextColor(piece) );
                g2.setFont( BASE_FONT );
                g2.drawString( piece.getAnnotation(), pos.x + 2*offset, pos.y + 3*offset);
        }
    }

}

