package com.becker.game.multiplayer.poker.ui;

import com.becker.game.common.*;
import com.becker.game.multiplayer.poker.PokerPlayer;
import com.becker.ui.*;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.text.MessageFormat;
import java.util.List;
import java.util.HashMap;

/**
 * Allow the user to specify a single order
 * @@ should show the distance when they have both origin and dest specified.
 *
 * @author Barry Becker
 */
public final class RaiseDialog extends OptionsDialog
                               implements ActionListener
{
    private PokerPlayer player_;

    private GradientButton okButton_;
    private JTextField raiseAmount_;

    private static final int DEFAULT_RAISE_AMOUNT = 5; // dollars

    /**
     * constructor - create the tree dialog.
     */
    public RaiseDialog(PokerPlayer player)
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

        JPanel buttonsPanel = createButtonsPanel();

        // add the form elements

        String labelText = GameContext.getLabel("ORIGIN");

        JPanel instructionsPanel = new JPanel(new BorderLayout());
        instructionsPanel.setMinimumSize(new Dimension(30,60));

        JLabel instr = new JLabel("You currently have $"+player_.getCash());
        instructionsPanel.add(instr, BorderLayout.CENTER);

        raiseAmount_ = new JTextField(DEFAULT_RAISE_AMOUNT);
        NumberInputPanel raiseInput = new NumberInputPanel(GameContext.getLabel("AMOUNT_TO_RAISE"), raiseAmount_);

        mainPanel_.add(instructionsPanel, BorderLayout.NORTH);
        mainPanel_.add(raiseInput, BorderLayout.CENTER);
        //mainPanel_.add(new JLabel(" "), BorderLayout.SOUTH);
        mainPanel_.add(buttonsPanel, BorderLayout.SOUTH);

        getContentPane().add( mainPanel_ );
        getContentPane().repaint();
        pack();
    }


    /**
     *  create the OK/Cancel buttons that go at the bottom.
     */
    protected JPanel createButtonsPanel()
    {
        JPanel buttonsPanel = new JPanel( new FlowLayout() );

        okButton_ = new GradientButton();
        initBottomButton( okButton_, GameContext.getLabel("OK"), GameContext.getLabel("PLACE_ORDER_TIP") );
        initBottomButton( cancelButton_, GameContext.getLabel("CANCEL"), GameContext.getLabel("CANCEL") );

        buttonsPanel.add( okButton_ );
        buttonsPanel.add( cancelButton_ );

        return buttonsPanel;
    }

    public String getTitle()
    {
        return GameContext.getLabel("MAKE_RAISE");
    }


    /**
     * called when one of the buttons at the bottom have been pressed.
     * @param e
     */
    public void actionPerformed( ActionEvent e )
    {
        Object source = e.getSource();
        if (source == okButton_) {
            this.setVisible(false);
        }
        else if ( source == cancelButton_ ) {
            cancel();
        }
        else {
           System.out.println( "actionPerformed source="+source+". not cancel and not ok" );
        }
    }


    /**
     * @return retrieve the specified order.
     */
    public int getRaiseAmount()
    {
         return Integer.parseInt(raiseAmount_.getText());
    }



}

