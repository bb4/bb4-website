package com.becker.game.twoplayer.tictactoe.ui;

import com.becker.common.*;
import com.becker.game.common.*;
import com.becker.game.common.ui.GameBoardRenderer;
import com.becker.game.twoplayer.common.*;
import com.becker.game.twoplayer.common.ui.*;
import com.becker.game.twoplayer.pente.*;

import com.becker.game.twoplayer.pente.ui.PenteBoardViewer;
import com.becker.game.twoplayer.tictactoe.TicTacToeController;
import java.awt.event.*;

/**
 *  Takes a PenteController as input and displays the
 *  current state of the Pente Game. The PenteController contains a PenteBoard
 *  which describes this state.
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

    protected GameBoardRenderer getBoardRenderer() {
        return TicTacToeBoardRenderer.getRenderer();
    }

    protected int getDefaultCellSize()
    {
        return 3;
    }
}
