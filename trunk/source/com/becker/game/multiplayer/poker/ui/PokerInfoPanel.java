package com.becker.game.multiplayer.poker.ui;

import com.becker.game.common.*;
import com.becker.game.common.ui.*;
import com.becker.game.multiplayer.poker.PokerController;
import com.becker.game.multiplayer.poker.PokerPlayer;
import com.becker.game.multiplayer.poker.PokerBoard;
import com.becker.ui.GradientButton;
import com.becker.ui.GUIUtil;

import javax.swing.*;
import javax.swing.border.TitledBorder;
import javax.swing.border.Border;
import java.text.MessageFormat;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import java.awt.*;


/**
 *  Show information and statistics about the game.
 *
 *  @author Barry Becker
 */
class PokerInfoPanel extends GameInfoPanel implements GameChangedListener, ActionListener
{

    //  buttons to either give comands or pass
    private JButton commandButton_;

    private JPanel commandPanel_;


    /**
     * Constructor
     */
    public PokerInfoPanel( GameController controller )
    {
        super(controller);
    }

    protected void createSubPanels()
    {
        this.add( createGeneralInfoPanel() );

        // the custom panel shows game specific info. In this case the command button.
        // if all the players are robots, don't even show this panel.
        if (!controller_.allPlayersComputer())
            this.add( createCustomInfoPanel() );
    }

    /**
     * This panel shows information that is specific to the game type.
     * For Poker, we have a button that allows the current player to enter his commands
     */
    protected JPanel createCustomInfoPanel()
    {

        commandPanel_ = createSectionPanel("");
        setCommandPanelTitle();

        // the command button
        JPanel bp = createPanel();
        bp.setBorder(createMarginBorder());

        commandButton_ = new GradientButton(GameContext.getLabel("ORDERS"));
        commandButton_.addActionListener(this);
        bp.add(commandButton_);

        commandPanel_.add(bp);
        return commandPanel_;
    }

    private void setCommandPanelTitle()
    {
        Object[] args = {controller_.getCurrentPlayer().getName()};
        String title = MessageFormat.format(GameContext.getLabel("MAKE_YOUR_MOVE"), args);

        TitledBorder b = (TitledBorder)commandPanel_.getBorder();
        b.setTitle(title);
    }


    protected String getMoveNumLabel()
    {
        return GameContext.getLabel("CURRENT_ROUND_COLON");
    }


    /**
     * The Orders button was pressed.
     * open the Orders dialog to get the players commands
     * @param e
     */
    public void actionPerformed(ActionEvent e)
    {
        PokerController gc = (PokerController)controller_;
        gameChanged(null); // update the current player in the label

        if (e.getSource() == commandButton_)
        {

           // open the command dialog to get the players commands
           PokerPlayer currentPlayer = (PokerPlayer)gc.getCurrentPlayer();

           // skip the player if he has folded

           // if the current player does not own any planets, then advance to the next player
           if (currentPlayer.hasFolded())  {
              gc.advanceToNextPlayer();
           }


           // @@ replace with betting dialog

           BettingDialog bettingDialog =
                   new BettingDialog(currentPlayer);
           //ordersDialog.setLocationRelativeTo( this );
           Point p = this.getParent().getLocationOnScreen();

           // offset the dlg so the Galaxy grid is visible as a reference
           bettingDialog.setLocation((int)(p.getX()+.7*getParent().getWidth()), (int)(p.getY()+getParent().getHeight()/3));


           boolean canceled = bettingDialog.showDialog();
           if ( !canceled ) {
               // apply the players action : fold, check, call, raise
               //currentPlayer.setOrders( ordersDialog.getOrders() );
               gc.advanceToNextPlayer();
           }

        }
    }

    /**
     * this is general information that is applicable to every 2 player game.
     */
    protected JPanel createGeneralInfoPanel()
    {
        JPanel generalPanel = createSectionPanel(GameContext.getLabel("GENERAL_INFO"));

        JLabel turnLabel = createLabel(GameContext.getLabel("PLAYER_TO_MOVE_COLON"));
        playerLabel_ = new JLabel();
        playerLabel_.setOpaque(true);
        playerLabel_.setFont(BOLD_FONT);
        setPlayerLabel();

        JLabel moveNumTextLabel = createLabel( GameContext.getLabel("CURRENT_MOVE_NUM_COLON"));
        moveNumTextLabel.setHorizontalAlignment(JLabel.LEFT);
        moveNumLabel_ = createLabel( "  " );

        generalPanel.add( createRowEntryPanel( turnLabel, playerLabel_ ) );
        generalPanel.add( createRowEntryPanel( moveNumTextLabel, moveNumLabel_ ) );

        // add this back in when it is implemented
        //generalPanel.add( createRowEntryPanel(showRecommendedMove_) );
        generalPanel.add( Box.createGlue() );

        return generalPanel;
    }


    /**
     * set the appropriate text and color for the player label.
     */
    protected void setPlayerLabel()
    {
        Player player = controller_.getCurrentPlayer();

        String playerName = player.getName();
        playerLabel_.setText(" " + playerName + " ");

        Color pColor = player.getColor();

        //Border playerLabelBorder = BorderFactory.createLineBorder(pColor, 2);
        playerLabel_.setBorder(getPlayerLabelBorder(pColor));

        if (commandPanel_ != null) {
            commandPanel_.setForeground(pColor);
            setCommandPanelTitle();
        }
        this.repaint();
    }


    /**
     * implements the GameChangedListener interface.
     * This method called whenever a move has been made.
     */
    public void gameChanged( GameChangedEvent gce )
    {
        if ( controller_ == null )
            return;
        //Player currentPlayer = controller_.getCurrentPlayer();
        setPlayerLabel();
        //Galaxy g = (Galaxy)controller_.getBoard();
        Move lastMove =  controller_.getLastMove();
        if (lastMove != null)  {
            //moveNumLabel_.setText( lastMove.moveNumber * controller_.getNumPlayers()
            //                      + ((PokerController)controller_).getCurrentPlayerIndex()+" " );
            moveNumLabel_.setText( (lastMove.moveNumber+2) + " " );
        }
        else {
            moveNumLabel_.setText( 1 + " " );
        }
    }

}