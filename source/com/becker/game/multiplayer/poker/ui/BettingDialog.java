package com.becker.game.multiplayer.poker.ui;

import com.becker.game.common.*;
import com.becker.game.multiplayer.galactic.*;
import com.becker.game.multiplayer.poker.PokerPlayer;
import com.becker.ui.*;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.text.MessageFormat;
import java.util.List;
import java.util.HashMap;

import sun.font.TextLabel;

/**
 * Allow the user to specify a single order
 * @@ should show the distance when they have both origin and dest specified.
 *
 * @author Barry Becker
 */
public final class BettingDialog extends OptionsDialog
                               implements ActionListener
{
    private PokerPlayer player_;

    private GradientButton foldButton_;
    private GradientButton callButton_;    // call or check
    private GradientButton raiseButton_;

    private JLabel availableCash_;

    private JPanel pokerHandPanel_;

    /**
     * constructor - create the tree dialog.
     */
    public BettingDialog(PokerPlayer player)
    {
        player_ = player;
 
        initUI();
    }


    /**
     * ui initialization of the tree control.
     */
    protected void initUI()
    {
        setResizable( true );
        mainPanel_ =  new JPanel();
        mainPanel_.setLayout( new BorderLayout() );

        pokerHandPanel_ = createPokerHandPanel();
        JPanel buttonsPanel = createButtonsPanel();

        JPanel instructions = createInstructionsPanel();

        mainPanel_.add(pokerHandPanel_, BorderLayout.NORTH);
        mainPanel_.add(instructions, BorderLayout.CENTER);
        //mainPanel_.add(new JLabel(" "), BorderLayout.SOUTH);
        mainPanel_.add(buttonsPanel, BorderLayout.SOUTH);

        getContentPane().add( mainPanel_ );
        getContentPane().repaint();
        pack();
    }

    private JPanel createPokerHandPanel() {
        JPanel panel = new JPanel();
        panel.setPreferredSize(new Dimension(400, 100));
        return panel;
    }

    private JPanel createInstructionsPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        JLabel currentCash = new JLabel("You currently have "+player_.getCash());
        JLabel amountToCall = new JLabel("To call, you need to add ???");
        //panel.setPreferredSize(new Dimension(400, 100));
        panel.add(currentCash, BorderLayout.NORTH);
        panel.add(amountToCall, BorderLayout.CENTER);
        return panel;
    }

    /**
     *  create the OK/Cancel buttons that go at the bottom.
     */
    protected JPanel createButtonsPanel()
    {
        JPanel buttonsPanel = new JPanel( new FlowLayout() );

        foldButton_ = new GradientButton();
        initBottomButton( foldButton_, GameContext.getLabel("FOLD"), GameContext.getLabel("FOLD_TIP") );

        callButton_ = new GradientButton();
        initBottomButton( callButton_, GameContext.getLabel("CALL"), GameContext.getLabel("CALL_TIP") );

        raiseButton_ = new GradientButton();
        initBottomButton( raiseButton_, GameContext.getLabel("RAISE"), GameContext.getLabel("RAISE_TIP") );

        buttonsPanel.add( foldButton_ );
        buttonsPanel.add( callButton_ );
        buttonsPanel.add( raiseButton_ );

        return buttonsPanel;
    }


    public String getTitle()
    {
        return GameContext.getLabel("MAKE_YOUR_BET");
    }



    /**
     * called when one of the buttons at the bottom have been pressed.
     * @param e
     */
    public void actionPerformed( ActionEvent e )
    {
        Object source = e.getSource();
        if (source == foldButton_) {
            // if there is not enough time to reach the planet, warn the user, and don't close the dlg.
            player_.fold();
            this.setVisible(false);
        }
        else if ( source == callButton_ ) {
            // @@ add the amount of money needed to call

            this.setVisible(false);
        }
        else if ( source == raiseButton_ ) {
            showRaiseDialog();

        }
        else {
           System.out.println( "actionPerformed source="+source+". not recognized" );
        }
    }


    public void showRaiseDialog() {
              // open a dlg to get an order
        RaiseDialog raiseDialog =
                new RaiseDialog(player_);

        raiseDialog.setLocation((int)(this.getLocation().getX() + 40), (int)(this.getLocation().getY() +170));


        boolean canceled = raiseDialog.showDialog();

        if ( !canceled ) { // newGame a game with the newly defined options
            int raise   = raiseDialog.getRaiseAmount();
            // @@ add the raise to the pot
        }
    }

}

