package com.becker.game.twoplayer.chess.ui;

import com.becker.game.common.*;
import com.becker.game.common.ui.*;
import com.becker.game.twoplayer.chess.ChessController;
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

    public String getTitle()
    {
        return GameContext.getLabel("CHESS_TITLE");
    }


    protected GameBoardViewer createBoardViewer()
    {
        return new ChessBoardViewer();
    }

    protected NewGameDialog createNewGameDialog( JFrame parent, GameBoardViewer viewer )
    {
        return new ChessNewGameDialog( parent, viewer );
    }

    protected GameInfoPanel createInfoPanel( GameController controller )
    {
        return new TwoPlayerInfoPanel( controller );   // make ChessInfoPanel
    }

    // Display the help dialog to give instructions
    protected void showHelpDialog()
    {
        String name = getTitle();
        String comments = GameContext.getLabel("CHESS_COMMENTS");
        String overview =GameContext.getLabel("CHESS_OVERVIEW");
        showHelpDialog( name, comments, overview );
    }

}



