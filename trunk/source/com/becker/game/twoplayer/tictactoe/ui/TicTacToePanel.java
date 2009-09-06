package com.becker.game.twoplayer.tictactoe.ui;

import com.becker.game.common.*;
import com.becker.game.common.ui.*;
import com.becker.game.twoplayer.common.ui.TwoPlayerInfoPanel;

import com.becker.game.twoplayer.pente.ui.PentePanel;
import javax.swing.*;

/**
 *  This class defines the main UI for the Pente game applet.
 *  It can be run as an applet or application.
 *
 *  @author Barry Becker
 */
public class TicTacToePanel extends PentePanel
{

    /**
     *  Construct the panel.
     */
    public TicTacToePanel() {}


    @Override
    public String getTitle()
    {
        return  GameContext.getLabel("TIC_TAC_TOE_TITLE");
    }

    @Override
    protected GameBoardViewer createBoardViewer()
    {
        return new TicTacToeBoardViewer();
    }

    @Override
    protected NewGameDialog createNewGameDialog( JFrame parent, GameViewable viewer )
    {
        return new TicTacToeNewGameDialog( parent, viewer );
    }

    @Override
    protected GameInfoPanel createInfoPanel(GameController controller)
    {
        return new TwoPlayerInfoPanel( controller );  
    }

    /**
     * Display the help dialog to give instructions
     */
    @Override
    protected void showHelpDialog()
    {
        String name = getTitle();
        String comments = GameContext.getLabel("TIC_TAC_TOE_TITLE");
        String overview = GameContext.getLabel("TIC_TAC_TOE_OVERVIEW");
        showHelpDialog( name, comments, overview );
    }

}



