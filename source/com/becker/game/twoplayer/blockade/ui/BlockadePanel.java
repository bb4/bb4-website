package com.becker.game.twoplayer.blockade.ui;

import com.becker.game.common.*;
import com.becker.game.common.ui.*;
import com.becker.game.twoplayer.common.ui.TwoPlayerInfoPanel;
import com.becker.game.twoplayer.common.ui.TwoPlayerPanel;

import javax.swing.*;

/**
 *  This class defines the main UI for the Blockade game applet.
 *  It can be run as an applet or application.
 *  see also the game Quoridor
 *
 *  @author Barry Becker
 */
public class BlockadePanel extends TwoPlayerPanel
{

    /**
     * Construct the panel.
     */
    public BlockadePanel()
    {}

    public String getTitle()
    {
        return GameContext.getLabel("BLOCKADE_TITLE");
    }

    protected GameBoardViewer createBoardViewer()
    {
        return new BlockadeBoardViewer();
    }

    protected NewGameDialog createNewGameDialog( JFrame parent, GameViewable viewer )
    {
        return new BlockadeNewGameDialog( parent, viewer );
    }

    protected GameInfoPanel createInfoPanel(GameController controller)
    {
        return new TwoPlayerInfoPanel( controller );
    }

    /**
     *  Display the help dialog to give instructions
     */
    protected void showHelpDialog()
    {
        String name = getTitle();
        String comments =GameContext.getLabel("BLOCKADE_COMMENTS");
        String overview = GameContext.getLabel("BLOCKADE_OVERVIEW");

        showHelpDialog( name, comments, overview );
    }

}



