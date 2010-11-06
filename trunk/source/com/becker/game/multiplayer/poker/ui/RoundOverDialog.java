package com.becker.game.multiplayer.poker.ui;

import com.becker.game.common.GameContext;
import com.becker.game.multiplayer.common.ui.PlayerLabel;
import com.becker.game.multiplayer.poker.player.PokerPlayer;
import com.becker.ui.components.GradientButton;
import com.becker.ui.dialogs.OptionsDialog;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.text.NumberFormat;


/**
 * Show a summary of the final results.
 * The winner is the player with al the chips.
 *
 * @author Barry Becker
 */
class RoundOverDialog extends OptionsDialog
{
    private GradientButton closeButton_;

    private PokerPlayer winner_;
    private int winnings_;
    private JLabel winLabel_;


    /**
     * constructor - create the tree dialog.
     * @param parent frame to display relative to
     */
    public RoundOverDialog( JFrame parent, PokerPlayer winner, int winnings )
    {
        super( parent );
        winner_ = winner;
        winnings_ = winnings;
        showContent();
    }

    @Override
    protected JComponent createDialogContent() {
        setResizable( true );
        JPanel mainPanel =  new JPanel();
        mainPanel.setLayout( new BorderLayout() );
        mainPanel.setBorder(BorderFactory.createCompoundBorder(BorderFactory.createEtchedBorder(),
                                                               BorderFactory.createEmptyBorder(10, 10, 10, 10)));
        JPanel buttonsPanel = createButtonsPanel();
        JPanel instructions = createInstructionsPanel();

        mainPanel.add(instructions, BorderLayout.CENTER);
        mainPanel.add(buttonsPanel, BorderLayout.SOUTH);

        return mainPanel;
    }

    private JPanel createInstructionsPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        PlayerLabel playerLabel = new PlayerLabel();
        playerLabel.setPlayer(winner_);

        winLabel_ = new JLabel();
        initWonMessage();

        //panel.setPreferredSize(new Dimension(400, 100));
        panel.add(playerLabel, BorderLayout.NORTH);
        panel.add(winLabel_, BorderLayout.CENTER);
        //panel.add(amountToCall, BorderLayout.SOUTH);
        return panel;
    }

    private void initWonMessage() {
         NumberFormat cf = BettingDialog.getCurrencyFormat();
        String cash = cf.format(winnings_);
        winLabel_.setText("won " + cash + " from the pot!");
        //JLabel amountToCall = new JLabel("To call, you need to add " + cf.format(callAmount_));
    }

    @Override
    public String getTitle() {
       return "Round Over";
    }

    @Override
    protected JPanel createButtonsPanel(){
        JPanel buttonsPanel = new JPanel( new FlowLayout() );

        closeButton_ = new GradientButton();
        initBottomButton( closeButton_, GameContext.getLabel("CLOSE"), GameContext.getLabel("CLOSE_TIP") );

        buttonsPanel.add( closeButton_ );
        return buttonsPanel;
    }


    @Override
    public void actionPerformed(ActionEvent evt) {
        if (evt.getSource() == closeButton_) {
            this.setVisible(false);
        }
    }

}

