package com.becker.game.multiplayer.poker.ui;

import com.becker.game.common.*;
import com.becker.game.common.ui.*;
import com.becker.game.online.ui.*;


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


    public String getTitle()
    {
        return  GameContext.getLabel("POKER_TITLE");
    }

    /**
     * Poker supports online play if the server is available
     * @return true if the game supports online play and there is a server available
     */
    protected boolean isOnlinePlayAvailable()
    {

        return onlineGameDialog_.isServerAvailable();
    }


    protected GameBoardViewer createBoardViewer()
    {
        return new PokerGameViewer();
    }

    protected NewGameDialog createNewGameDialog( JFrame parent, ViewerCallbackInterface viewer )
    {
        return new PokerNewGameDialog( parent, viewer );
    }

    protected OnlineGameDialog createOnlineGameDialog( JFrame parent, ViewerCallbackInterface viewer ) {
        return new OnlinePokerDialog( parent, viewer );
    }

    protected GameOptionsDialog createOptionsDialog( JFrame parent, GameController controller )
    {
        return new PokerOptionsDialog( parent, controller );
    }

    protected GameInfoPanel createInfoPanel(GameController controller)
    {
        return new PokerInfoPanel( controller);
    }

    // Display the help dialog to give instructions
    protected void showHelpDialog()
    {
        String name = getTitle();
        String comments = GameContext.getLabel("POKER_TITLE");
        String overview = GameContext.getLabel("POKER_OVERVIEW");
        showHelpDialog( name, comments, overview );
    }

}



