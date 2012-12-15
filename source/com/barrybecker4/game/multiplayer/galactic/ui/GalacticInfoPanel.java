/** Copyright by Barry G. Becker, 2000-2011. Licensed under MIT License: http://www.opensource.org/licenses/MIT  */
package com.barrybecker4.game.multiplayer.galactic.ui;

import com.barrybecker4.game.common.GameContext;
import com.barrybecker4.game.common.GameController;
import com.barrybecker4.game.common.Move;
import com.barrybecker4.game.common.player.Player;
import com.barrybecker4.game.common.ui.panel.GameChangedEvent;
import com.barrybecker4.game.common.ui.panel.GameChangedListener;
import com.barrybecker4.game.common.ui.panel.GameInfoPanel;
import com.barrybecker4.game.multiplayer.galactic.GalacticController;
import com.barrybecker4.game.multiplayer.galactic.Galaxy;
import com.barrybecker4.game.multiplayer.galactic.player.GalacticPlayer;
import com.barrybecker4.game.multiplayer.galactic.ui.dialog.OrdersDialog;
import com.barrybecker4.ui.components.GradientButton;

import javax.swing.*;
import javax.swing.border.TitledBorder;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.text.MessageFormat;


/**
 *  Show information and statistics about the game.
 *  Also allow the player to enter their commmands for the turn.
 *
 *  @author Barry Becker
 */
class GalacticInfoPanel extends GameInfoPanel
                        implements GameChangedListener, ActionListener {

    /** buttons to either give commands or pass  */
    private JButton commandButton_;
    private JButton passButton_;
    private JPanel commandPanel_;


    /**
     * Constructor
     */
    GalacticInfoPanel( GameController controller ) {
        super(controller);
    }

    @Override
    protected void createSubPanels() {
        this.add( createGeneralInfoPanel() );

        // the custom panel shows game specific info. In this case the command button.
        // if all the players are robots, don't even show this panel.
        if (!controller_.getPlayers().allPlayersComputer())
            this.add( createCustomInfoPanel() );
    }

    /**
     * This panel shows information that is specific to the game type.
     * For Galactic Empire we have a button that allows the current player to enter his commands
     * Should split this out into a separate PlayerCommandPanel class.
     */
    @Override
    protected JPanel createCustomInfoPanel() {

        commandPanel_ = createSectionPanel("");
        setCommandPanelTitle();

        // the command button
        JPanel bp = createPanel();
        bp.setBorder(createMarginBorder());

        commandButton_ = new GradientButton(GameContext.getLabel("ORDERS"));
        commandButton_.addActionListener(this);
        bp.add(commandButton_);

        passButton_ = new GradientButton(GameContext.getLabel("PASS"));
        passButton_.addActionListener(this);
        bp.add(passButton_);

        commandPanel_.add(bp);
        return commandPanel_;
    }

    private void setCommandPanelTitle() {
        Object[] args = {controller_.getCurrentPlayer().getName()};
        String title = MessageFormat.format(GameContext.getLabel("GIVE_YOUR_ORDERS"), args);

        TitledBorder b = (TitledBorder)commandPanel_.getBorder();
        b.setTitle(title);
    }


    @Override
    protected String getMoveNumLabel() {
        return GameContext.getLabel("CURRENT_YEAR" + COLON);
    }

    /**
     * The Orders button was pressed.
     * open the Orders dialog to get the players commands
     * @param e
     */
    public void actionPerformed(ActionEvent e) {
        GalacticController gc = (GalacticController)controller_;
        gameChanged(null); // update the current player in the label

        if (e.getSource() == commandButton_) {

           // open the command dialog to get the players commands
           GalacticPlayer currentPlayer = (GalacticPlayer)gc.getCurrentPlayer();

           // if the current player does not own any planets, then advance to the next player
           if (Galaxy.getPlanets(currentPlayer).size() == 0)
              gc.advanceToNextPlayer();


           OrdersDialog ordersDialog =
                   new OrdersDialog(null, currentPlayer, gc.getNumberOfYearsRemaining());
           //ordersDialog.setLocationRelativeTo( this );
           Point p = this.getParent().getLocationOnScreen();

           // offset the dlg so the Galaxy grid is visible as a reference
           ordersDialog.setLocation((int)(p.getX()+0.7*getParent().getWidth()), (int)(p.getY()+getParent().getHeight()/3.0));

           boolean canceled = ordersDialog.showDialog();
           if ( !canceled ) { // newGame a game with the newly defined options
               currentPlayer.setOrders( ordersDialog.getOrders() );
               gc.advanceToNextPlayer();
           }
        }
        else if (e.getSource() == passButton_) {
           gc.advanceToNextPlayer();
        }
    }

    /**
     * set the appropriate text and color for the player label.
     */
    @Override
    protected void setPlayerLabel() {
        Player player = controller_.getCurrentPlayer();

        String playerName = player.getName();
        playerLabel_.setText(' ' + playerName + ' ');

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
    @Override
    public void gameChanged(GameChangedEvent gce) {
        if ( controller_ == null )
            return;
        setPlayerLabel();
        Move lastMove =  controller_.getLastMove();
        if (lastMove != null)  {
            moveNumLabel_.setText( (controller_.getPlayers().getNumPlayers() + 2) + " " );
        }
        else {
            moveNumLabel_.setText( 1 + " " );
        }
    }

}