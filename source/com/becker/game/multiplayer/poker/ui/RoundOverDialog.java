package com.becker.game.multiplayer.poker.ui;

import com.becker.ui.components.GradientButton;
import com.becker.ui.dialogs.OptionsDialog;
import com.becker.game.common.*;
import com.becker.game.multiplayer.common.ui.ActionDialog;
import com.becker.game.multiplayer.poker.player.PokerPlayer;
import com.becker.game.multiplayer.poker.player.*;
import com.becker.ui.*;

import javax.swing.*;
import java.awt.event.*;
import java.awt.*;
import java.text.NumberFormat;


/**
 * Show a summary of the final results.
 * The winner is the player with al the chips.
 *
 * @author Barry Becker
 */
public class RoundOverDialog extends OptionsDialog
{
    GradientButton closeButton_;

    PokerPlayer winner_;
    int winnings_;

    /**
     * constructor - create the tree dialog.
     * @param parent frame to display relative to
     */
    public RoundOverDialog( Frame parent, PokerPlayer winner, int winnings )
    {
        super( parent );
        winner_ = winner;
        winnings_ = winnings;
        initUI();
    }

    protected void initUI() {
        setResizable( true );
        JPanel mainPanel =  new JPanel();
        mainPanel.setLayout( new BorderLayout() );
        mainPanel.setBorder(BorderFactory.createCompoundBorder(BorderFactory.createEtchedBorder(),
                                                               BorderFactory.createEmptyBorder(10, 10, 10, 10)));
        JPanel buttonsPanel = createButtonsPanel();
        JPanel instructions = createInstructionsPanel();

        mainPanel.add(instructions, BorderLayout.CENTER);
        mainPanel.add(buttonsPanel, BorderLayout.SOUTH);

        getContentPane().add( mainPanel );
        getContentPane().repaint();
        pack();
    }


    private JPanel createInstructionsPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        JPanel playerPanel = ActionDialog.createPlayerLabel(winner_);

        NumberFormat cf = BettingDialog.getCurrencyFormat();
        String cash = cf.format(winnings_);
        JLabel winLabel = new JLabel("won " + cash + " from the pot!");
        //JLabel amountToCall = new JLabel("To call, you need to add " + cf.format(callAmount_));

        //panel.setPreferredSize(new Dimension(400, 100));
        panel.add(playerPanel, BorderLayout.NORTH);
        panel.add(winLabel, BorderLayout.CENTER);
        //panel.add(amountToCall, BorderLayout.SOUTH);
        return panel;
    }

    public String getTitle() {
       return "Round Over";
    }

    protected JPanel createButtonsPanel(){
        JPanel buttonsPanel = new JPanel( new FlowLayout() );

        closeButton_ = new GradientButton();
        initBottomButton( closeButton_, GameContext.getLabel("CLOSE"), GameContext.getLabel("CLOSE_TIP") );

        buttonsPanel.add( closeButton_ );
        return buttonsPanel;
    }


    public void actionPerformed(ActionEvent evt) {
        if (evt.getSource() == closeButton_) {
            this.setVisible(false);
        }
    }

}

