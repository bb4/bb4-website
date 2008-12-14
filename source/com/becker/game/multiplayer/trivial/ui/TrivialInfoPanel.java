package com.becker.game.multiplayer.trivial.ui;

import com.becker.game.common.*;
import com.becker.game.common.ui.*;
import com.becker.game.multiplayer.trivial.*;
import com.becker.game.multiplayer.trivial.player.*;
import com.becker.ui.*;
import com.becker.ui.legend.*;

import javax.swing.*;
import javax.swing.border.*;
import java.awt.*;
import java.awt.event.*;
import java.text.*;


/**
 *  Show information and statistics about the game.
 *
 *  @author Barry Becker
 */
class TrivialInfoPanel extends GameInfoPanel implements GameChangedListener, ActionListener
{

    //  buttons to either give comands or pass
    private JButton commandButton_;
    private JPanel commandPanel_;

    /**
     * Constructor
     */
    TrivialInfoPanel( GameController controller )
    {
        super(controller);
    }

    protected void createSubPanels()
    {
        add( createGeneralInfoPanel() );

        // the custom panel shows game specific info. In this case, the command button.
        // if all the players are robots, don't even show this panel.
        if (!controller_.allPlayersComputer())   {
            add( createCustomInfoPanel() );
        }     
    }

    /**
     * This panel shows information that is specific to the game type.
     * For Trivial, we have a button that allows the current player to enter his commands
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
        return GameContext.getLabel("CURRENT_ROUND" + COLON);
    }


    /**
     * The Orders button was pressed.
     * open the Orders dialog to get the players commands
     * @param e
     */
    public void actionPerformed(ActionEvent e)
    {

        if (e.getSource() == commandButton_)
        {
            TrivialController pc = (TrivialController)controller_;
            gameChanged(null); // update the current player in the label

           // open the command dialog to get the players commands
           TrivialPlayer currentPlayer = (TrivialPlayer)pc.getCurrentPlayer();

           RevealDialog bettingDialog = new RevealDialog(pc, getParent());

           boolean canceled = bettingDialog.showDialog();
           if ( !canceled ) {
               TrivialAction action = (TrivialAction) currentPlayer.getAction(pc);
               // apply the players action : fold, check, call, raise
               switch (action.getActionName()) {
                    case KEEP_HIDDEN :                       
                        break;
                    case REVEAL :               
                        currentPlayer.revealValue();
                        break;              
                }      
                // tell the server that we have moved. All the surrogates need to update.
                controller_.getServerConnection().playerActionPerformed(action);
                pc.advanceToNextPlayer();
           }           
        }
    }

    /**
     * set the appropriate text and color for the player label.
     */
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
    public void gameChanged( GameChangedEvent gce )
    {
        if ( controller_ == null )
            return;

        setPlayerLabel();
        Move lastMove =  controller_.getBoard().getLastMove();
        if (lastMove != null)  {
            moveNumLabel_.setText( (controller_.getNumMoves() + 2) + " " );
        }
        else {
            moveNumLabel_.setText( 1 + " " );
        }

        // disable if the game is done or the current player is a surrpgate        
        boolean enabled = !controller_.isDone() && !controller_.getCurrentPlayer().isSurrogate();               
        commandButton_.setEnabled(enabled);
    }

}