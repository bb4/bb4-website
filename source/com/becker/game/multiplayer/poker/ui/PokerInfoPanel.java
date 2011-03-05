package com.becker.game.multiplayer.poker.ui;

import com.becker.game.common.GameContext;
import com.becker.game.common.GameController;
import com.becker.game.common.Move;
import com.becker.game.common.player.Player;
import com.becker.game.common.ui.panel.GameChangedEvent;
import com.becker.game.common.ui.panel.GameChangedListener;
import com.becker.game.common.ui.panel.GameInfoPanel;
import com.becker.game.multiplayer.poker.PokerAction;
import com.becker.game.multiplayer.poker.PokerController;
import com.becker.game.multiplayer.poker.player.PokerPlayer;
import com.becker.ui.components.GradientButton;
import com.becker.ui.legend.DiscreteColorLegend;

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
class PokerInfoPanel extends GameInfoPanel implements GameChangedListener, ActionListener
{

    //  buttons to either give comands or pass
    private JButton commandButton_;
    private JPanel commandPanel_;


    /**
     * Constructor
     */
    PokerInfoPanel( GameController controller )
    {
        super(controller);
    }

    /**
     * The custom panel shows game specific info. In this case, the command button.
     * if all the players are robots, don't even show this panel.
     */
    @Override
    protected void createSubPanels()
    {
        add( createGeneralInfoPanel() );

        if (!controller_.getPlayers().allPlayersComputer())   {
            add( createCustomInfoPanel() );
        }

        add( createChipLegendPanel());
    }

    /**
     * This panel shows information that is specific to the game type.
     * For Poker, we have a button that allows the current player to enter his commands
     */
    @Override
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


    /**
     * This panel shows a discrete color legend for the poker chip values
     */
    JPanel createChipLegendPanel()
    {
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


    private void setCommandPanelTitle()
    {
        Object[] args = {controller_.getCurrentPlayer().getName()};
        String title = MessageFormat.format(GameContext.getLabel("MAKE_YOUR_MOVE"), args);

        TitledBorder b = (TitledBorder)commandPanel_.getBorder();
        b.setTitle(title);
    }


    @Override
    protected String getMoveNumLabel()
    {
        return GameContext.getLabel("CURRENT_ROUND" + COLON);
    }


    /**
     * The Comman button was pressed.
     * open the dialog to get the players command.
     * @param e
     */
    public void actionPerformed(ActionEvent e)
    {
        if (e.getSource() == commandButton_)
        {
            PokerController pc = (PokerController)controller_;
            gameChanged(null); // update the current player in the label

           // open the command dialog to get the players commands
           PokerPlayer currentPlayer = (PokerPlayer)pc.getCurrentPlayer();

           
           // if the current player has folded, then advance to the next player.
           if (currentPlayer.hasFolded())  {
              pc.advanceToNextPlayer();
           }

           int callAmount = pc.getCurrentMaxContribution() - currentPlayer.getContribution();
           
           BettingDialog bettingDialog = new BettingDialog(pc, getParent());

           boolean canceled = bettingDialog.showDialog();
           if ( !canceled ) {
               PokerAction action = (PokerAction)currentPlayer.getAction(pc);
               // apply the players action : fold, check, call, raise
               switch (action.getActionName()) {
                    case FOLD :
                        currentPlayer.setFold(true);                  
                        break;
                    case CALL :               
                        if (callAmount <= currentPlayer.getCash())  {
                            currentPlayer.contributeToPot(pc, callAmount);                     
                        } else {                            
                            currentPlayer.setFold(true);
                            // if this happens it was probably because someone was allowed to raise by more than the all in amount.
                            assert false:"callAmount=" + callAmount +" currentPlayer cash="+currentPlayer.getCash(); 
                        }
                        break;
                    case RAISE :
                        currentPlayer.contributeToPot(pc, callAmount);
                        int raise = action.getRaiseAmount();
                        currentPlayer.contributeToPot(pc, raise);
                        break;
                }               
                             
               pc.advanceToNextPlayer();
           }
        }
    }


    /**
     * set the appropriate text and color for the player label.
     */
    @Override
    protected void setPlayerLabel()
    {
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
    public void gameChanged( GameChangedEvent gce )
    {
        if ( controller_ == null )
            return;

        //Player currentPlayer = controller_.getCurrentPlayer();
        setPlayerLabel();
        //Galaxy g = (Galaxy)controller_.getBoard();
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