package com.becker.game.twoplayer.pente.ui;

import com.becker.game.common.GameController;
import com.becker.game.common.ui.viewer.GameBoardRenderer;
import com.becker.game.common.ui.viewer.ViewerMouseListener;
import com.becker.game.twoplayer.common.ui.AbstractTwoPlayerBoardViewer;
import com.becker.game.twoplayer.pente.PenteController;

/**
 *  Takes a PenteController as input and displays the
 *  current state of the Pente Game. The PenteController contains a PenteBoard
 *  which describes this state.
 *
 *  @author Barry Becker
 */
public class PenteBoardViewer extends AbstractTwoPlayerBoardViewer
{

    public PenteBoardViewer() {}

    @Override
    protected GameController createController()
    {
        return new PenteController();
    }

    @Override
    protected GameBoardRenderer getBoardRenderer() {
        return PenteBoardRenderer.getRenderer();
    }

    @Override
    protected ViewerMouseListener createViewerMouseListener() {
        return new PenteViewerMouseListener(this);
    }

}
