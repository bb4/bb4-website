package com.becker.game.multiplayer.poker.ui;

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
 *  This class defines the main UI for the Poker game applet.
 *  It can be run as an applet or application.
 *
 *  @author Barry Becker
 */
public class PokerPanel extends GamePanel
{

    /**
     *  Construct the panel.
     */
    public PokerPanel()
    {}


    @Override
    public String getTitle() {
        return  GameContext.getLabel("POKER_TITLE");
    }


    @Override
    protected GameBoardViewer createBoardViewer() {
        return new PokerGameViewer();
    }

    @Override
    protected NewGameDialog createNewGameDialog( JFrame parent, GameViewable viewer ) {
        return new PokerNewGameDialog( parent, viewer );
    }

    @Override
    protected GameOptionsDialog createOptionsDialog( JFrame parent, GameController controller ) {
        return new PokerOptionsDialog( parent, controller );
    }

    @Override
    protected GameInfoPanel createInfoPanel(GameController controller) {
        return new PokerInfoPanel( controller);
    }

    // Display the help dialog to give instructions
    @Override
    protected void showHelpDialog()  {
        String name = getTitle();
        String comments = GameContext.getLabel("POKER_TITLE");
        String overview = GameContext.getLabel("POKER_OVERVIEW");
        showHelpDialog( name, comments, overview );
    }

}



