package com.becker.game.twoplayer.pente.ui;

import com.becker.game.common.ui.GameBoardRenderer;
import com.becker.game.twoplayer.checkers.ui.CheckersBoardRenderer;
import com.becker.game.twoplayer.common.ui.TwoPlayerPieceRenderer;
import com.becker.game.twoplayer.common.ui.TwoPlayerBoardRenderer;

/**
 * Singleton class that takes a game board and renders it for the GameBoardViewer.
 * Having the board renderer separate from the viewer helps to separate out the rendering logic
 * from other features of the GameBoardViewer.
 *
 * @author Barry Becker
 */
public class PenteBoardRenderer extends TwoPlayerBoardRenderer
{
    private  static GameBoardRenderer renderer_;


    /**
     * private constructor because this class is a singleton.
     * Use getRenderer instead
     */
    protected PenteBoardRenderer()
    {
        pieceRenderer_ = TwoPlayerPieceRenderer.getRenderer();
    }

    public static GameBoardRenderer getRenderer()
    {
        if (renderer_ == null)
            renderer_ = new PenteBoardRenderer();
        return renderer_;
    }

}

