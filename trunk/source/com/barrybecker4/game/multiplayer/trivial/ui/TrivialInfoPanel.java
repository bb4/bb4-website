/** Copyright by Barry G. Becker, 2000-2011. Licensed under MIT License: http://www.opensource.org/licenses/MIT  */
package com.barrybecker4.game.multiplayer.trivial.ui;

import com.barrybecker4.game.common.GameContext;
import com.barrybecker4.game.common.GameController;
import com.barrybecker4.game.common.Move;
import com.barrybecker4.game.common.player.Player;
import com.barrybecker4.game.common.ui.panel.GameChangedEvent;
import com.barrybecker4.game.common.ui.panel.GameChangedListener;
import com.barrybecker4.game.common.ui.panel.GameInfoPanel;
import com.barrybecker4.game.multiplayer.common.online.SurrogateMultiPlayer;
import com.barrybecker4.game.multiplayer.trivial.TrivialAction;
import com.barrybecker4.game.multiplayer.trivial.TrivialController;
import com.barrybecker4.game.multiplayer.trivial.player.TrivialPlayer;
import com.barrybecker4.ui.components.GradientButton;

import javax.swing.*;
import javax.swing.border.TitledBorder;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.text.MessageFormat;

/**
 *  Show information and statistics about the game.
 *
 *  @author Barry Becker
 */
class TrivialInfoPanel extends GameInfoPanel
                       implements GameChangedListener, ActionListener {

    // buttons to either give commands or pass
    private JButton commandButton_;
    private JPanel commandPanel_;

    /**
     * Constructor
     */
    TrivialInfoPanel( GameController controller ) {
        super(controller);
    }

    @Override
    protected void createSubPanels() {
        add( createGeneralInfoPanel() );

        // the custom panel shows game specific info. In this case, the command button.
        // if all the players are robots, don't even show this panel.
        if (!controller_.getPlayers().allPlayersComputer())   {
            add( createCustomInfoPanel() );
        }
    }

    /**
     * This panel shows information that is specific to the game type.
     * For Trivial, we have a button that allows the current player to enter his commands
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

        commandPanel_.add(bp);
        return commandPanel_;
    }

    private void setCommandPanelTitle() {
        Object[] args = {controller_.getCurrentPlayer().getName()};
        String title = MessageFormat.format(GameContext.getLabel("MAKE_YOUR_MOVE"), args);

        TitledBorder b = (TitledBorder)commandPanel_.getBorder();
        b.setTitle(title);
    }


    @Override
    protected String getMoveNumLabel() {
        return GameContext.getLabel("CURRENT_ROUND" + COLON);
    }

    /**
     * The Orders button was pressed.
     * open the Orders dialog to get the players directives for their move.
     * @param e
     */
    public void actionPerformed(ActionEvent e) {

        if (e.getSource() == commandButton_)  {
            TrivialController pc = (TrivialController)controller_;
            gameChanged(null); // update the current player in the label

           // open the command dialog to get the players commands
           Player p = pc.getCurrentPlayer();
           TrivialPlayer currentPlayer = (TrivialPlayer) p.getActualPlayer();

           RevealDialog bettingDialog = new RevealDialog(pc, getParent());

           boolean canceled = bettingDialog.showDialog();
           if ( !canceled ) {
               TrivialAction action = (TrivialAction) currentPlayer.getAction(pc);
               // apply the players action : to review their cards or not
               switch (action.getActionName()) {
                    case KEEP_HIDDEN :
                        break;
                    case REVEAL :
                        currentPlayer.revealValue();
                        break;
                }
                // tell the server that we have moved. All the surrogates need to then make their moves.
                controller_.getServerConnection().playerActionPerformed(action);
                pc.advanceToNextPlayer();
           }
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
    public void gameChanged( GameChangedEvent gce ) {
        if ( controller_ == null ) {
            return;
        }

        setPlayerLabel();
        Move lastMove =  controller_.getLastMove();
        if (lastMove != null)  {
            moveNumLabel_.setText( (controller_.getNumMoves() + 2) + " " );
        }
        else {
            moveNumLabel_.setText( 1 + " " );
        }

        // disable if the game is done or the current player is a surrogate
        boolean enabled = !controller_.isDone() && !controller_.getCurrentPlayer().isSurrogate();
        commandButton_.setEnabled(enabled);
    }

}