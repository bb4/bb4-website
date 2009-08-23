package com.becker.game.multiplayer.set.ui;

import com.becker.game.common.*;
import com.becker.game.common.ui.*;
import com.becker.game.multiplayer.set.*;

import javax.swing.*;
import java.awt.event.*;

/**
 * Main panel for set game
 *
 * It contains a dockable toolbar and a canvas for showing the cards.
 * new game, add card, solve, and help.
 *  @see SetToolBar
 *
 *  @author Barry Becker
 */
public class SetPanel extends GamePanel
                      implements ActionListener
{

    public SetPanel()
    {}

    public String getTitle() {
        return "Set Game";
    }


    protected GameToolBar createToolbar() {
         return new SetToolBar(BG_TEXTURE, this);
    }

    protected GameBoardViewer createBoardViewer()
    {
        return new SetGameViewer();
    }

    protected NewGameDialog createNewGameDialog( JFrame parent, GameViewable viewer )
    {
        return new SetNewGameDialog( parent, viewer );
    }

    protected GameOptionsDialog createOptionsDialog( JFrame parent, GameController controller )
    {
        return new SetOptionsDialog( parent, controller );
    }

    protected GameInfoPanel createInfoPanel(GameController controller)
    {
        return new SetInfoPanel( controller);
    }

    /**
     * implements the GameChangedListener interface.
     * This method called whenever a move has been made.
     */
    @Override
    public void gameChanged( GameChangedEvent gce )
    {
        // do nothing for this.
    }


    /**
     * Display a help dialog.
     * This dialog should tell about the game and give instructions on how to play.
     */
    protected void showHelpDialog()
    {
        String name = getTitle();
        String comments = "a Set game simulation by Barry Becker.";
        showHelpDialog( name, comments, "Click on the cards to find sets (of three cards). "+
                                        "Each of the attributes (color, shape, fill, and number) must be all the same or all different for the 3 cards.");
    }

    /**
     * handle button click actions.
     * If you add your own custom buttons, you should override this, but be sure the first line is
     * <P>
     * super.actionPerformed(e);
     */
    @Override
    public void actionPerformed( ActionEvent e )
    {
        Object source = e.getSource();

        SetToolBar setToolBar = ((SetToolBar)toolBar_);
        SetController c = ((SetController)boardViewer_.getController());

        if ( source == setToolBar.getAddButton()) {
            c.addCards(1);
        }
        else if ( source == setToolBar.getRemoveButton()) {
            c.removeCard();

        }
        else if ( source == setToolBar.getSolveButton()) {
             //JOptionPane.showMessageDialog(this, "Solution Requested");
            SolutionDialog solutionDialog = new SolutionDialog(null, (SetController) boardViewer_.getController());
            solutionDialog.setVisible(true); //showDialog();
            solutionDialog.pack();
        }

        setToolBar.getRemoveButton().setEnabled(c.canRemoveCards());
        setToolBar.getAddButton().setEnabled(c.hasCardsToAdd());

        super.actionPerformed( e );

        boardViewer_.repaint();
    }

}
