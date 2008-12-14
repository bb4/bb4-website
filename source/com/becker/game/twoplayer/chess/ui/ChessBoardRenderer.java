package com.becker.game.twoplayer.chess.ui;

import com.becker.game.common.ui.GameBoardRenderer;
import com.becker.game.common.GameControllerInterface;
import com.becker.game.twoplayer.checkers.ui.CheckersBoardRenderer;

import java.awt.*;

/**
 * Singleton class that takes a game board and renders it for the GameBoardViewer.
 * Having the board renderer separate from the viewer helps to separate out the rendering logic
 * from other features of the GameBoardViewer.
 *
 * @author Barry Becker
 */
public class ChessBoardRenderer extends CheckersBoardRenderer
{
    private  static GameBoardRenderer renderer_;


    /**
     * private constructor because this class is a singleton.
     * Use getRenderer instead
     */
    private ChessBoardRenderer()
    {
        pieceRenderer_ = ChessPieceRenderer.getRenderer();
    }

    public static GameBoardRenderer getRenderer()
    {
        if (renderer_ == null)
            renderer_ = new ChessBoardRenderer();
        return renderer_;
    }

    protected void drawLastMoveMarker(Graphics2D g2, GameControllerInterface controller)
    {}
}

