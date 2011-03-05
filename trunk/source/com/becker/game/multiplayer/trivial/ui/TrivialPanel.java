package com.becker.game.multiplayer.trivial.ui;

import com.becker.game.common.GameContext;
import com.becker.game.common.GameController;
import com.becker.game.common.GameViewable;
import com.becker.game.common.ui.dialogs.GameOptionsDialog;
import com.becker.game.common.ui.dialogs.NewGameDialog;
import com.becker.game.common.ui.panel.GameInfoPanel;
import com.becker.game.common.ui.panel.GamePanel;
import com.becker.game.common.ui.viewer.GameBoardViewer;

import javax.swing.*;

/**
 *  This class defines the main UI for the Trivial game applet.
 *  It can be run as an applet or application.
 *
 *  @author Barry Becker
 */
public class TrivialPanel extends GamePanel
{

    /**
     *  Construct the panel.
     */
    public TrivialPanel()
    {}


    @Override
    public String getTitle() {
        return  GameContext.getLabel("TRIVIAL_TITLE");
    }


    @Override
    protected GameBoardViewer createBoardViewer() {
        return new TrivialGameViewer();
    }

    @Override
    protected NewGameDialog createNewGameDialog( JFrame parent, GameViewable viewer ) {
        return new TrivialNewGameDialog( parent, viewer );
    }

    @Override
    protected GameOptionsDialog createOptionsDialog( JFrame parent, GameController controller ) {
        return new TrivialOptionsDialog( parent, controller );
    }

    @Override
    protected GameInfoPanel createInfoPanel(GameController controller) {
        return new TrivialInfoPanel( controller);
    }

    // Display the help dialog to give instructions
    @Override
    protected void showHelpDialog()  {
        String name = getTitle();
        String comments = GameContext.getLabel("TRIVIAL_TITLE");
        String overview = GameContext.getLabel("TRIVIAL_OVERVIEW");
        showHelpDialog( name, comments, overview );
    }

}



