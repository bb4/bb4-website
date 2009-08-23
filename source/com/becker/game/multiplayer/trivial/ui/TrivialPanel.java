package com.becker.game.multiplayer.trivial.ui;

import com.becker.game.common.*;
import com.becker.game.common.ui.*;

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


    public String getTitle() {
        return  GameContext.getLabel("TRIVIAL_TITLE");
    }


    protected GameBoardViewer createBoardViewer() {
        return new TrivialGameViewer();
    }

    protected NewGameDialog createNewGameDialog( JFrame parent, GameViewable viewer ) {
        return new TrivialNewGameDialog( parent, viewer );
    }

    protected GameOptionsDialog createOptionsDialog( JFrame parent, GameController controller ) {
        return new TrivialOptionsDialog( parent, controller );
    }

    protected GameInfoPanel createInfoPanel(GameController controller) {
        return new TrivialInfoPanel( controller);
    }

    // Display the help dialog to give instructions
    protected void showHelpDialog()  {
        String name = getTitle();
        String comments = GameContext.getLabel("TRIVIAL_TITLE");
        String overview = GameContext.getLabel("TRIVIAL_OVERVIEW");
        showHelpDialog( name, comments, overview );
    }

}



