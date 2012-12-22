/** Copyright by Barry G. Becker, 2000-2011. Licensed under MIT License: http://www.opensource.org/licenses/MIT  */
package com.barrybecker4.game.multiplayer.poker.ui;

import com.barrybecker4.game.common.GameContext;
import com.barrybecker4.game.common.GameController;
import com.barrybecker4.game.common.Move;
import com.barrybecker4.game.common.player.Player;
import com.barrybecker4.game.common.ui.panel.GameChangedEvent;
import com.barrybecker4.game.common.ui.panel.GameChangedListener;
import com.barrybecker4.game.common.ui.panel.GameInfoPanel;
import com.barrybecker4.game.multiplayer.common.online.SurrogateMultiPlayer;
import com.barrybecker4.game.multiplayer.poker.PokerAction;
import com.barrybecker4.game.multiplayer.poker.PokerController;
import com.barrybecker4.game.multiplayer.poker.player.PokerPlayer;
import com.barrybecker4.ui.components.GradientButton;
import com.barrybecker4.ui.legend.DiscreteColorLegend;

import javax.swing.*;
import javax.swing.border.TitledBorder;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.text.MessageFormat;

/**
 * Show information and statistics about the game.
 *
 * @author Barry Becker
 */
class PokerInfoPanel extends GameInfoPanel
                     implements GameChangedListener, ActionListener {

    //  buttons to either give commands or pass
    private JButton commandButton_;
    private JPanel commandPanel_;


    /**
     * Constructor
     */
    PokerInfoPanel( GameController controller ) {
        super(controller);
    }

    /**
     * The custom panel shows game specific info. In this case, the command button.
     * if all the players are robots, don't even show this panel.
     */
    @Override
    protected void createSubPanels() {
        add( createGeneralInfoPanel() );

        if (!controller_.getPlayers().allPlayersComputer())  {
            add( createCustomInfoPanel() );
        }

        add( createChipLegendPanel());
    }

    /**
     * This panel shows information that is specific to the game type.
     * For Poker, we have a button that allows the current player to enter his commands
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

    /**
     * This panel shows a discrete color legend for the poker chip values
     */
    JPanel createChipLegendPanel() {
        JPanel legendPanel = createSectionPanel("Chip Values");
        PokerChip[] chipTypes = PokerChip.values();
        int n = chipTypes.length;
        Color[] colors = new Color[n];
        String[] values = new String[n];
        for (int i = n; i > 0; i--) {
            colors[n-i] = chipTypes[i-1].getColor();
            values[n-i] = chipTypes[i-1].getLabel();
        }
        JPanel legend = new DiscreteColorLegend(null, colors, values);
        legend.setPreferredSize(new Dimension(500, 100));
        legendPanel.add(legend);

        return legendPanel;
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
     * The Command button was pressed.
     * open the dialog to get the players command.
     * @param e
     */
    public void actionPerformed(ActionEvent e) {

        if (e.getSource() == commandButton_) {
            PokerController pc = (PokerController)controller_;
            if (!pc.getCurrentPlayer().isHuman()) {
                JOptionPane.showMessageDialog(this, "It's not your turn", "Warning", JOptionPane.INFORMATION_MESSAGE);
                return;
            }
            gameChanged(null); // update the current player in the label

           // open the command dialog to get the players commands
           PokerPlayer currentPlayer = (PokerPlayer) pc.getCurrentPlayer().getActualPlayer();

           // if the current player has folded, then advance to the next player.
           if (currentPlayer.hasFolded())  {
               pc.advanceToNextPlayer();
           }

           BettingDialog bettingDialog = new BettingDialog(pc, getParent());

           boolean canceled = bettingDialog.showDialog();
           if ( !canceled ) {
               PokerAction action = (PokerAction)currentPlayer.getAction(pc);
               applyPokerAction(action, currentPlayer);

               pc.advanceToNextPlayer();
           }
        }
    }

    /**  apply the players action : fold, check, call, raise */
    private void applyPokerAction(PokerAction action, PokerPlayer currentPlayer) {

         PokerController pc = (PokerController)controller_;
         int callAmount = pc.getCurrentMaxContribution() - currentPlayer.getContribution();

         switch (action.getActionName()) {
             case FOLD :
                 currentPlayer.setFold(true);
                 break;
             case CALL :
                 if (callAmount <= currentPlayer.getCash())  {
                     currentPlayer.contributeToPot(pc, callAmount);
                 } else {
                     currentPlayer.setFold(true);
                     // if this happens it was probably because someone was allowed
                     // to raise by more than the all in amount.
                     assert false:"callAmount=" + callAmount +" currentPlayer cash="+currentPlayer.getCash();
                 }
                 break;
             case RAISE :
                 currentPlayer.contributeToPot(pc, callAmount);
                 int raise = action.getRaiseAmount();
                 currentPlayer.contributeToPot(pc, raise);
                 break;
          }
    }

    /**
     * Set the appropriate text and color for the player label.
     */
    @Override
    protected void setPlayerLabel() {
        Player player = controller_.getCurrentPlayer();

        String playerName = player.getName();
        playerLabel_.setText(' ' + playerName + ' ');

        Color pColor = player.getColor();

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
        if ( controller_ == null )  {
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

        // don't allow any more actions when the game is done.
        commandButton_.setEnabled(!controller_.isDone());
    }

}