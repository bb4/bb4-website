package com.becker.game.twoplayer.go.ui;

import com.becker.game.common.*;
import com.becker.game.common.ui.GameBoardViewer;
import com.becker.game.common.ui.GameInfoPanel;
import com.becker.game.common.ui.GameToolBar;
import com.becker.game.common.ui.NewGameDialog;
import com.becker.game.twoplayer.common.ui.GameTreeDialog;
import com.becker.game.twoplayer.common.ui.TwoPlayerBoardViewer;
import com.becker.game.twoplayer.common.ui.TwoPlayerPanel;

import javax.swing.*;
import java.awt.event.ActionEvent;

/**
 *  This class defines the main UI for the Go game panel.
 *
 *  @author Barry Becker
 */
public final class GoPanel extends TwoPlayerPanel
{

    /**
     * Construct the panel.
     */
    public GoPanel()
    {}

    /**
     * @return the title for the applet/application window
     */
    public String getTitle()
    {
        return GameContext.getLabel("GO");
    }

    /**
     * creates a board viewer of the appropriate type given a game controller
     * @return the game board viewer
     */
    protected GameBoardViewer createBoardViewer()
    {
        return new GoBoardViewer();
    }

    protected NewGameDialog createNewGameDialog( JFrame parent, ViewerCallbackInterface viewer )
    {
        return new GoNewGameDialog( parent, viewer );
    }

    protected GameInfoPanel createInfoPanel(GameController controller)
    {
        return new GoInfoPanel( controller );
    }


    protected GameTreeDialog createGameTreeDialog()
    {
        TwoPlayerBoardViewer v =(TwoPlayerBoardViewer)createBoardViewer();
        v.setViewOnly( true ); // we don't want it to recieve click events
        return new GameTreeDialog( null, v, new GoTreeCellRenderer());
    }

    protected GameToolBar createToolbar() {
         return new GoToolBar(BG_TEXTURE, this);
    }

    /**
     * Display the help dialog to give instructions
     */
    protected void showHelpDialog()
    {
        String name = getTitle();
        String comments = GameContext.getLabel("GO_COMMENTS");
        String overview = GameContext.getLabel("GO_OVERVIEW");
        showHelpDialog( name, comments, overview );
    }


    /**
     * handle the pass button and the regular ones too.
     */
    public void actionPerformed( ActionEvent e )
    {
        Object source = e.getSource();
        if ( source == ((GoToolBar)toolBar_).getPassButton() ) {
            ((GoBoardViewer) boardViewer_).pass();
        }
        super.actionPerformed( e );
    }

}



