package com.becker.game.twoplayer.checkers.ui;

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
 *  This class defines the main UI for the Checkers game panel.
 *  It can be run as an applet or application.
 *
 *  @author Barry Becker
 */
public class CheckersPanel extends TwoPlayerPanel
{

    /**
     * Construct the panel.
     */
    public CheckersPanel()
    {}

    @Override
    public String getTitle()
    {
        return GameContext.getLabel("CHECKERS_TITLE");
    }

    @Override
    protected GameBoardViewer createBoardViewer()
    {
        return new CheckersBoardViewer();
    }

    @Override
    protected NewGameDialog createNewGameDialog( JFrame parent, GameViewable viewer )
    {
        return new CheckersNewGameDialog( parent, viewer );
    }

    @Override
    protected GameInfoPanel createInfoPanel(GameController controller)
    {
        return new TwoPlayerInfoPanel( controller );
    }

    /**
     * Display the help dialog to give instructions.
     */
    @Override
    protected void showHelpDialog()
    {
        String name = getTitle();
        String comments = GameContext.getLabel("CHECKERS_COMMENTS");
        showHelpDialog( name, comments, GameContext.getLabel("CHECKERS_OVERVIEW" ));
    }

}



