package com.becker.game.twoplayer.checkers.ui;

import com.becker.game.twoplayer.checkers.CheckersPiece;
import com.becker.game.common.BoardPosition;
import com.becker.game.common.Board;
import com.becker.game.common.ui.GamePieceRenderer;

import java.awt.*;

/**
 *  a singleton class that takes a checkers piece and renders it for the CheckersBoardViewer.
 * @see com.becker.game.twoplayer.checkers.ui.CheckersBoardViewer
 * @author Barry Becker
 */
public class CheckersPieceRenderer extends GamePieceRenderer
{
    private static GamePieceRenderer renderer_ = null;

    /**
     * private constructor because this class is a singleton.
     * Use getPieceRenderer instead
     */
    protected CheckersPieceRenderer()
    {}

    public static GamePieceRenderer getRenderer()
    {
        if (renderer_ == null)
            renderer_ = new CheckersPieceRenderer();
        return renderer_;
    }

    /**
     * this draws the actual piece.
     */
    public void render( Graphics2D g2, BoardPosition position, int cellSize, Board b)
    {
        CheckersPiece piece = (CheckersPiece)position.getPiece();
        if (piece == null)
            return; // nothing to render

        int pieceSize = getPieceSize(cellSize, piece);
        if ( piece.getType() == CheckersPiece.REGULAR_PIECE )
            super.render( g2, position, cellSize, b);
        else {  //draw a KING
            g2.setColor( getPieceColor(piece) );
            Point pos = getPosition(position, cellSize, pieceSize);

            g2.fillRect( pos.x + 1, pos.y + 1,
                         pieceSize, pieceSize );

            if ( piece.getTransparency() == 0 ) {
                g2.setColor( Color.black );
                g2.drawRect( pos.x + 1, pos.y + 1, pieceSize + 1, pieceSize + 1 );
            }
        }
    }
}
