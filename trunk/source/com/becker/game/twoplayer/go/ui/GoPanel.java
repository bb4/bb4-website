package com.becker.game.twoplayer.go.ui;

import com.becker.game.common.*;
import com.becker.game.common.ui.*;
import com.becker.game.twoplayer.common.ui.TwoPlayerPanel;
import com.becker.ui.GradientButton;

import javax.swing.*;
import java.awt.event.ActionEvent;

/**
 *  This class defines the main UI for the Go game panel.
 *
 *  @author Barry Becker
 */
public final class GoPanel extends TwoPlayerPanel
{

    // go needs an extra button for passing
    // do not initiallize to null or it will not work because of the way initialization happens
    private GradientButton passButton_;

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
    protected final GameBoardViewer createBoardViewer()
    {
        return new GoBoardViewer();
    }

    protected final NewGameDialog createNewGameDialog( JFrame parent, GameBoardViewer viewer )
    {
        return new GoNewGameDialog( parent, viewer );
    }

    protected final GameInfoPanel createInfoPanel( GameController controller )
    {
        return new GoInfoPanel( controller );
    }

    /**
     * Display the help dialog to give instructions
     */
    protected final void showHelpDialog()
    {
        String name = getTitle();
        String comments = GameContext.getLabel("GO_COMMENTS");
        String overview = GameContext.getLabel("GO_OVERVIEW");
        showHelpDialog( name, comments, overview );
    }

    /**
     * add the button for passing.
     */
    protected final void addCustomToolBarButtons()
    {
        passButton_ = createToolBarButton( GameContext.getLabel("PASS_BTN"),
                                           GameContext.getLabel("PASS_BTN_TIP"),
                                           null/*passImage_*/ );
        toolBar_.add( passButton_ );
    }

    /**
     * handle the pass button and the regular ones too.
     */
    public void actionPerformed( ActionEvent e )
    {
        Object source = e.getSource();
        if ( source == passButton_ ) {
            ((GoBoardViewer) boardViewer_).pass();
        }
        super.actionPerformed( e );
    }

}



