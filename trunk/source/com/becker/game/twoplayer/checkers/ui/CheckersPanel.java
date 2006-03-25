package com.becker.game.twoplayer.checkers.ui;

import com.becker.game.common.*;
import com.becker.game.common.ui.*;
import com.becker.game.twoplayer.common.ui.*;

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

    public String getTitle()
    {
        return GameContext.getLabel("CHECKERS_TITLE");
    }

    protected GameBoardViewer createBoardViewer()
    {
        return new CheckersBoardViewer();
    }

    protected NewGameDialog createNewGameDialog( JFrame parent, ViewerCallbackInterface viewer )
    {
        return new CheckersNewGameDialog( parent, viewer );
    }

    protected GameInfoPanel createInfoPanel(GameController controller)
    {
        return new TwoPlayerInfoPanel( controller );
    }

    /**
     * Display the help dialog to give instructions.
     */
    protected void showHelpDialog()
    {
        String name = getTitle();
        String comments = GameContext.getLabel("CHECKERS_COMMENTS");
        showHelpDialog( name, comments, GameContext.getLabel("CHECKERS_OVERVIEW" ));
    }

}



