package com.becker.game.twoplayer.blockade.ui;

import com.becker.game.common.GameContext;
import com.becker.game.common.GameController;
import com.becker.game.common.GameViewable;
import com.becker.game.common.ui.GameInfoPanel;
import com.becker.game.common.ui.dialogs.NewGameDialog;
import com.becker.game.common.ui.viewer.GameBoardViewer;
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

    @Override
    public String getTitle()
    {
        return GameContext.getLabel("BLOCKADE_TITLE");
    }

    @Override
    protected GameBoardViewer createBoardViewer()
    {
        return new BlockadeBoardViewer();
    }

    @Override
    protected NewGameDialog createNewGameDialog( JFrame parent, GameViewable viewer )
    {
        return new BlockadeNewGameDialog( parent, viewer );
    }

    @Override
    protected GameInfoPanel createInfoPanel(GameController controller)
    {
        return new TwoPlayerInfoPanel( controller );
    }

    /**
     *  Display the help dialog to give instructions
     */
    @Override
    protected void showHelpDialog()
    {
        String name = getTitle();
        String comments =GameContext.getLabel("BLOCKADE_COMMENTS");
        String overview = GameContext.getLabel("BLOCKADE_OVERVIEW");

        showHelpDialog( name, comments, overview );
    }

}



