package com.becker.game.twoplayer.common.ui;

import com.becker.game.common.*;
import com.becker.game.common.ui.*;
import com.becker.game.twoplayer.common.ui.TwoPlayerBoardViewer;
import com.becker.game.twoplayer.common.TwoPlayerController;
import com.becker.sound.SpeechSynthesizer;
import com.becker.ui.*;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 * This is an abstract base class for a Game UI.
 * See derived classes for specific game implementations.
 *
 * It contains a dockable toolbar which shows at least 5 buttons:
 *  new game, undo, redo, options, and help.
 * It puts the game board viewer in a scrollable pane on the left.
 * There is an info window on the right that gives statistics about the current game state.
 * There is a brogress bar at the bottom that shows whenever the computer is thinking.
 *
 * This class is the main panel in the applet or application.
 * It contains everything related to acutally playing the board game.
 *
 *  @author Barry Becker
 */
public abstract class TwoPlayerPanel extends GamePanel
                                implements ActionListener, GameChangedListener
{

    // for showing progress while the computer thinks.
    protected JProgressBar progressBar_;

    protected GameTreeDialog treeDialog_;

    /**
     * Construct the panel.
     */
    public TwoPlayerPanel()
    {
        super();
    }

    private TwoPlayerController get2PlayerController()
    {
        return (TwoPlayerController)boardViewer_.getController();
    }

    /**
     *  UIComponent initialization.
     */
    protected void initGui()
    {
        super.initGui();

        // we create a separate controller for the TreeDialog so it can browse without
        // disturbing the state of the actual game.
        treeDialog_ = createGameTreeDialog();

       if ( get2PlayerController().getShowGameTree() )
          showGameTreeDialog();
    }

    protected JPanel createBottomDecorationPanel()
    {
        progressBar_ = createProgressBar();
        boardViewer_.setProgressBar(progressBar_);

        // put the progress bar in a panel so it does not cause a relayout
        JPanel progressPanel = new TexturedPanel(BG_TEXTURE);
        progressPanel.setLayout(new BorderLayout());
        //progressPanel.setPreferredSize(new Dimension(1000, 20));
        progressPanel.add(progressBar_, BorderLayout.CENTER);
        return progressPanel;
    }

    protected JProgressBar createProgressBar()
    {
        JProgressBar progressBar = new JProgressBar();
        progressBar.setOpaque(false);
        progressBar.setVisible(!get2PlayerController().allPlayersHuman());  // show only when used
        progressBar.setMinimum(0);
        progressBar.setMaximum(100);
        progressBar.setStringPainted(true);
        progressBar.setBorderPainted(false);
        progressBar.setString(" ");
        return progressBar;
    }

    /**
     * @return  the dialog used to specify various game options and parameters.
     */
    protected GameOptionsDialog createOptionsDialog( JFrame parent, GameController controller )
    {
        return new TwoPlayerOptionsDialog( parent, controller );
    }

    /**
     * The game tree dialog shows the game tree for better understanding.
     * but it also has its own (view only) viewer that is used to show any particular node in the game tree.
     * @return the new game tree dialog
     */
    private GameTreeDialog createGameTreeDialog()
    {
        TwoPlayerBoardViewer v =(TwoPlayerBoardViewer)createBoardViewer();
        v.setViewOnly( true ); // we don't want it to recieve click events
        return new GameTreeDialog( null, v );
    }


    private void showGameTreeDialog()
    {
         treeDialog_.reset();
         boardViewer_.addGameChangedListener( treeDialog_ );
         // take the root from the treeDialog and set it on the TwoPlayerController so it can
         // create the tree and allow the treeDialog to show it when the
         // change event happens.
         get2PlayerController().setGameTreeRoot( treeDialog_.getRootNode() );
         treeDialog_.setVisible(false);
    }


    /**
     * handle button click actions.
     * If you add your own custom buttons, you should override this, but be sure the first line is
     * <P>
     * super.actionPerformed(e);
     */
    public void actionPerformed( ActionEvent e )
    {
        Object source = e.getSource();
        TwoPlayerBoardViewer v = (TwoPlayerBoardViewer)boardViewer_;

        if ( source == newGameButton_ ) {
            newGameDialog_.setLocationRelativeTo( this );
            get2PlayerController().pause();
            boolean canceled = newGameDialog_.showDialog();
            if ( !canceled ) { // newGame a game with the newly defined options
                // the only case we don't show the progress bar is if both players are human.
                progressBar_.setVisible(!get2PlayerController().allPlayersHuman());
                v.startNewGame();
            }
            else {
                v.continueProcessing();
            }
        }
        else if ( source == undoButton_ ) {
            v.undoLastManMove();
            // gray it if there are now no more moves to undo
            undoButton_.setEnabled(boardViewer_.canUndoMove());
            redoButton_.setEnabled(true);
        }
        else if ( source == redoButton_ ) {
            v.redoLastManMove();
            // gray it if there are now no more moves to undo
            redoButton_.setEnabled(boardViewer_.canRedoMove());
            undoButton_.setEnabled(true);
        }
        if ( source == optionsButton_ ) {
            optionsDialog_.setLocationRelativeTo( this );
            boolean canceled = optionsDialog_.showDialog();
            //System.out.println( "options selected  canceled=" + canceled );
            if ( !canceled ) { // start a game with the newly defined options
                //System.out.println( "options selected not canceled  show game tree=" + controller_.getShowGameTree() );
                if ( get2PlayerController().getShowGameTree() ) {
                    showGameTreeDialog();

                }
                else
                    treeDialog_.setVisible(false);
            }
        }
        else if ( source == helpButton_ )
            showHelpDialog();
    }

}
