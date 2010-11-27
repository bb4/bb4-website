package com.becker.game.twoplayer.tictactoe.ui;

import com.becker.game.common.GameController;
import com.becker.game.common.ui.viewer.GameBoardRenderer;
import com.becker.game.twoplayer.pente.ui.PenteBoardViewer;
import com.becker.game.twoplayer.tictactoe.TicTacToeController;

/**
 *  Takes a TicTacToeController as input and displays the
 *  current state of the Pente Game.
 *
 *  @author Barry Becker
 */
public class TicTacToeBoardViewer extends PenteBoardViewer
{

    /**
      *  Constructor
      */
    public TicTacToeBoardViewer() {
    }

    @Override
    protected GameController createController() {
        return new TicTacToeController();
    }

    @Override
    protected GameBoardRenderer getBoardRenderer() {
        return TicTacToeBoardRenderer.getRenderer();
    }
}
