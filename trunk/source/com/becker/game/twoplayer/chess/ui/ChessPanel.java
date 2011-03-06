package com.becker.game.twoplayer.chess.ui;

import com.becker.game.common.GameContext;
import com.becker.game.common.GameController;
import com.becker.game.common.GameViewable;
import com.becker.game.common.ui.dialogs.NewGameDialog;
import com.becker.game.common.ui.panel.GameInfoPanel;
import com.becker.game.common.ui.viewer.GameBoardViewer;
import com.becker.game.twoplayer.common.ui.TwoPlayerInfoPanel;
import com.becker.game.twoplayer.common.ui.TwoPlayerPanel;

import javax.swing.*;

/**
 *  This class defines the main UI for the Chess game panel.
 *  It can be shown in an applet or application.
 *
 *  @author Barry Becker
 */
public class ChessPanel extends TwoPlayerPanel
{

    /**
     * Construct the panel.
     */
    public ChessPanel()
    {}

    @Override
    public String getTitle()
    {
        return GameContext.getLabel("CHESS_TITLE");
    }


    @Override
    protected GameBoardViewer createBoardViewer()
    {
        return new ChessBoardViewer();
    }

    @Override
    protected NewGameDialog createNewGameDialog( JFrame parent, GameViewable viewer )
    {
        return new ChessNewGameDialog( parent, viewer );
    }

    @Override
    protected GameInfoPanel createInfoPanel(GameController controller)
    {
        return new TwoPlayerInfoPanel( controller );   // make ChessInfoPanel
    }

    // Display the help dialog to give instructions
    @Override
    protected void showHelpDialog()
    {
        String name = getTitle();
        String comments = GameContext.getLabel("CHESS_COMMENTS");
        String overview =GameContext.getLabel("CHESS_OVERVIEW");
        showHelpDialog( name, comments, overview );
    }

}



