package com.becker.game.multiplayer.poker.ui;

import com.becker.common.Location;
import com.becker.game.common.GameContext;
import com.becker.game.multiplayer.common.ui.ActionDialog;
import com.becker.game.multiplayer.poker.PokerAction;
import com.becker.game.multiplayer.poker.PokerController;
import com.becker.game.multiplayer.poker.PokerHand;
import com.becker.game.multiplayer.poker.PokerOptions;
import com.becker.game.multiplayer.poker.player.PokerHumanPlayer;
import com.becker.game.multiplayer.poker.player.PokerPlayer;
import com.becker.ui.components.GradientButton;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.text.NumberFormat;

/**
 * Allow the user to specify a poker action
 * @author Barry Becker
 */
public final class BettingDialog extends ActionDialog
{

    private GradientButton foldButton_;
    private GradientButton callButton_;    // call or check
    private GradientButton raiseButton_;

    private int callAmount_;
    private int contributeAmount_;
    private int raiseAmount_ = 0;

    /**
     * constructor - create the tree dialog.
     * @param pc pokerController
     */
    public BettingDialog(PokerController pc, Component parent)
    {
        super(pc, parent);
        callAmount_ = ((PokerPlayer)player_).getCallAmount(pc);
        contributeAmount_ = 0;
    }


    /**
     * ui initialization of the tree control.
     */
    @Override
    protected JPanel createPersonalInfoPanel() {

        return new PokerHandPanel(((PokerPlayer)player_).getHand());
    }


    @Override
    protected JPanel createGameInstructionsPanel() {

        NumberFormat cf = getCurrencyFormat();
        String cash = cf.format(((PokerPlayer)player_).getCash());
        JPanel instr = new JPanel();
        instr.setBorder(BorderFactory.createEmptyBorder(5, 10, 5, 10));
        JLabel currentCash = new JLabel("You currently have "+cash);

        JLabel amountToCall = new JLabel("To call, you need to add "+cf.format(callAmount_));


        JPanel gameInstructions = new JPanel(new BorderLayout());
        gameInstructions.add(currentCash, BorderLayout.CENTER);
        if (callAmount_ > 0)  {
            gameInstructions.add(amountToCall, BorderLayout.SOUTH);
        }
        return gameInstructions;
    }


    /**
     *  create the OK/Cancel buttons that go at the bottom.
     */
    @Override
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
        //@@ fix i18n
        return NumberFormat.getCurrencyInstance(JComponent.getDefaultLocale());
    }

    @Override
    public String getTitle()
    {
        return GameContext.getLabel("MAKE_YOUR_BET");
    }


    /**
     * called when one of the buttons at the bottom have been pressed.
     * @param e
     */
    @Override
    public void actionPerformed( ActionEvent e )
    {
        Object source = e.getSource();
        PokerAction.Name actionName = null;
        if (source == foldButton_) {
            actionName = PokerAction.Name.FOLD;
            ((PokerPlayer)player_).setFold(true);
            this.setVisible(false);
        }
        else if ( source == callButton_ ) {
            actionName = PokerAction.Name.CALL;
            // add the amount of money needed to call
            contributeAmount_ = callAmount_;
            this.setVisible(false);
        }
        else if ( source == raiseButton_ ) {
            actionName = PokerAction.Name.RAISE;
            showRaiseDialog();
        }
        else {
            assert false :"actionPerformed source="+source+". not recognized";
        }

        ((PokerHumanPlayer)player_).setAction(new PokerAction(player_.getName(), actionName, raiseAmount_));
    }


    void showRaiseDialog() {
        // open a dlg to get an order
        PokerController pc = (PokerController)controller_;
        PokerOptions options = (PokerOptions)controller_.getOptions();
        RaiseDialog raiseDialog =
                new RaiseDialog((PokerPlayer)player_, callAmount_, pc.getAllInAmount(),
                                options.getMaxAbsoluteRaise(), options.getAnte());

        raiseDialog.setLocation((int)(this.getLocation().getX() + 40), (int)(this.getLocation().getY() +170));

        boolean canceled = raiseDialog.showDialog();

        if ( !canceled ) {
            raiseAmount_ = raiseDialog.getRaiseAmount();
            contributeAmount_  = callAmount_ + raiseAmount_;
            this.setVisible(false);
        }
    }

    public int getContributeAmount() {
        return contributeAmount_;
    }

    /**
     * this panel shows the player the contents of their hand so they can bet on it.
     */
    private static class PokerHandPanel extends JPanel {
        PokerHand hand_;

        public PokerHandPanel(PokerHand hand) {
            hand_ = new PokerHand(hand.getCards());
            hand_.setFaceUp(true);
            this.setPreferredSize(new Dimension(400, 120));
        }

        @Override
        protected void paintComponent(Graphics g) {
             PokerPlayerRenderer renderer = (PokerPlayerRenderer) PokerPlayerRenderer.getRenderer();
             renderer.renderHand((Graphics2D)g, new Location(0, 2), hand_, 22);
        }
    }

}

