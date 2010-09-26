package com.becker.game.twoplayer.pente.ui;

import com.becker.game.common.GameContext;
import com.becker.game.common.GameController;
import com.becker.game.common.GameViewable;
import com.becker.game.common.ui.GameBoardViewer;
import com.becker.game.common.ui.GameInfoPanel;
import com.becker.game.common.ui.NewGameDialog;
import com.becker.game.twoplayer.common.ui.TwoPlayerInfoPanel;
import com.becker.game.twoplayer.common.ui.TwoPlayerPanel;

import javax.swing.*;

/**
 *  This class defines the main UI for the Pente game applet.
 *  It can be run as an applet or application.
 *
 *  @author Barry Becker
 */
public class PentePanel extends TwoPlayerPanel
{

    /**
     *  Construct the panel.
     */
    public PentePanel()
    {}


    @Override
    public String getTitle()
    {
        return  GameContext.getLabel("PENTE_TITLE");
    }


    @Override
    protected GameBoardViewer createBoardViewer()
    {
        return new PenteBoardViewer();
    }

    @Override
    protected NewGameDialog createNewGameDialog( JFrame parent, GameViewable viewer )
    {
        return new PenteNewGameDialog( parent, viewer );
    }

    @Override
    protected GameInfoPanel createInfoPanel(GameController controller)
    {
        return new TwoPlayerInfoPanel( controller );   // make PenteInfoPanel
    }

    // Display the help dialog to give instructions
    @Override
    protected void showHelpDialog()
    {
        String name = getTitle();
        String comments = GameContext.getLabel("PENTE_TITLE");
        String overview = GameContext.getLabel("PENTE_OVERVIEW");
        showHelpDialog( name, comments, overview );
    }

}



