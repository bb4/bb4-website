package com.becker.game.multiplayer.poker.ui;

import com.becker.game.common.*;
import com.becker.game.multiplayer.galactic.*;
import com.becker.game.multiplayer.poker.PokerPlayer;
import com.becker.game.multiplayer.poker.PokerHand;
import com.becker.game.multiplayer.poker.PokerController;
import com.becker.ui.*;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.text.MessageFormat;
import java.text.NumberFormat;
import java.util.List;
import java.util.HashMap;

import sun.font.TextLabel;

/**
 * Allow the user to specify a single order
 * @author Barry Becker
 */
public final class BettingDialog extends OptionsDialog
                               implements ActionListener
{
    private PokerPlayer player_;

    private GradientButton foldButton_;
    private GradientButton callButton_;    // call or check
    private GradientButton raiseButton_;

    private PokerController pc_;
    private int callAmount_;
    private int contributeAmount_;

    private JPanel pokerHandPanel_;
    private static NumberFormat currencyFormat_;

    // PokerPlayer player, int callAmount, int allInAmount)

    /**
     * constructor - create the tree dialog.
     * @param pc pokerController
     */
    public BettingDialog(PokerController pc)
    {
        pc_ = pc;
        player_ = (PokerPlayer)pc_.getCurrentPlayer();
        callAmount_ = player_.getCallAmount(pc_);
        contributeAmount_ = 0;

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

        pokerHandPanel_ = new PokerHandPanel(player_.getHand());
        JPanel buttonsPanel = createButtonsPanel();

        JPanel instructions = createInstructionsPanel();

        mainPanel_.add(pokerHandPanel_, BorderLayout.NORTH);
        mainPanel_.add(instructions, BorderLayout.CENTER);
        mainPanel_.add(buttonsPanel, BorderLayout.SOUTH);

        getContentPane().add( mainPanel_ );
        getContentPane().repaint();
        pack();
    }


    private JPanel createInstructionsPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        JPanel playerPanel = createPlayerLabel(player_);

        NumberFormat cf = getCurrencyFormat();
        String cash = cf.format(player_.getCash());
        JPanel instr = new JPanel();
        instr.setBorder(BorderFactory.createEmptyBorder(5, 10, 5, 10));
        JLabel currentCash = new JLabel("You currently have "+cash);

        JLabel amountToCall = new JLabel("To call, you need to add "+cf.format(callAmount_));

        //panel.setPreferredSize(new Dimension(400, 100));
        panel.add(playerPanel, BorderLayout.NORTH);
        panel.add(currentCash, BorderLayout.CENTER);
        if (callAmount_ > 0)  {
            panel.add(amountToCall, BorderLayout.SOUTH);
        }
        return panel;
    }

    public static JPanel createPlayerLabel(PokerPlayer player) {
        JPanel p = new JPanel();
        JPanel swatch = new JPanel();
        swatch.setPreferredSize(new Dimension(10, 10));
        swatch.setBackground(player.getColor());
        JLabel playerLabel = new JLabel(player.getName());
        p.add(swatch);
        p.add(playerLabel);
        return p;
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


    public static NumberFormat getCurrencyFormat() {
        if (currencyFormat_ == null) {
            currencyFormat_ =  NumberFormat.getCurrencyInstance(GameContext.getLocale());
        }
        return currencyFormat_;
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
            player_.setFold(true);
            this.setVisible(false);
        }
        else if ( source == callButton_ ) {
            // add the amount of money needed to call
            contributeAmount_ = callAmount_;
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
                new RaiseDialog(player_, callAmount_, pc_.getAllInAmount(), pc_.getMaxAbsoluteRaise());

        raiseDialog.setLocation((int)(this.getLocation().getX() + 40), (int)(this.getLocation().getY() +170));

        boolean canceled = raiseDialog.showDialog();

        if ( !canceled ) { // newGame a game with the newly defined options
            contributeAmount_  = callAmount_ + raiseDialog.getRaiseAmount();
            this.setVisible(false);
        }
    }

    public int getContributeAmount() {
        return contributeAmount_;
    }

    /**
     * this panel shows the player the contents of their hand so they can bet on it.
     */
    private class PokerHandPanel extends JPanel {
        PokerHand hand_;

        public PokerHandPanel(PokerHand hand) {
            hand_ = new PokerHand(hand.getCards());
            hand_.setFaceUp(true);
            this.setPreferredSize(new Dimension(400, 120));
        }

        protected void paintComponent(Graphics g) {
             PokerRenderer renderer = (PokerRenderer)PokerRenderer.getRenderer();
             renderer.renderHand((Graphics2D)g, new Location(0, 2), hand_, 22);
        }
    }

}

