package com.becker.game.multiplayer.set.ui;

import com.becker.ui.components.GradientButton;
import com.becker.ui.dialogs.OptionsDialog;
import com.becker.game.common.*;
import com.becker.game.multiplayer.set.*;
import com.becker.ui.*;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.*;
import java.util.List;

/**
 * Use this modal dialog to let the user choose from among the
 * different game options.
 *
 * @author Barry Becker
 */
public class SolutionDialog extends OptionsDialog
{

    private static final long serialVersionUID = 0L;

    protected GradientButton okButton_ = new GradientButton();
    private SolutionPanel solutionPanel_;

    // list of sets. Each consecutive set of 3 cards in this list is a set.
    List<Card> sets_;

    // constructor
    public SolutionDialog(JFrame parent, SetController controller )
    {
        super( parent);

        sets_ = controller.getSetsOnBoard();
        solutionPanel_ = new SolutionPanel(sets_, (SetGameViewer) controller.getViewer());
        initUI();
    }

    protected void initUI()
    {
        JPanel mainPanel = new JPanel();
        mainPanel.setLayout( new BorderLayout() );

        JPanel buttonsPanel = createButtonsPanel();

        mainPanel.add( solutionPanel_, BorderLayout.CENTER );
        mainPanel.add( buttonsPanel, BorderLayout.SOUTH );

        this.getContentPane().add( mainPanel );
        this.getContentPane().repaint();
        this.pack();
    }

    public String getTitle()
    {
        return GameContext.getLabel("SETS_ON_BOARD");
    }

    // create the OK Cancel buttons that go at the botton
    protected JPanel createButtonsPanel()
    {
        JPanel buttonsPanel = new JPanel( new FlowLayout() );

        initBottomButton( okButton_, GameContext.getLabel("OK"), GameContext.getLabel("OK") );
        buttonsPanel.add( okButton_ );

        return buttonsPanel;
    }


    /**
     * ok button pressed.
     */
    protected void ok()
    {
        solutionPanel_.closed();
        this.setVisible( false );
    }

    /**
     * called when a button has been pressed
     */
    public void actionPerformed( ActionEvent e )
    {
        Object source = e.getSource();
        if ( source == okButton_ ) {
            ok();
        }
    }

}