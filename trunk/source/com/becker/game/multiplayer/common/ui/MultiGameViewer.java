package com.becker.game.multiplayer.common.ui;

import com.becker.common.*;
import com.becker.game.common.*;
import com.becker.game.common.ui.*;
import com.becker.game.multiplayer.common.MultiGameController;
import com.becker.game.multiplayer.common.online.SurrogatePlayer;
import com.becker.game.multiplayer.trivial.*;
import com.becker.game.multiplayer.trivial.player.*;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.util.*;

import javax.swing.*;
import java.awt.event.*;

/**
 *  Takes a TrivialController as input and displays the
 *  current state of the Game. The TrivalController contains a TrivialTable object
 *  which describes this state.
 *
 *  @author Barry Becker
 */
public abstract class MultiGameViewer extends GameBoardViewer
{

    protected boolean winnerDialogShown_ = false;

    // Construct the application
    public MultiGameViewer() {}

    protected abstract MultiGameController createController();

    /**
     * start over with a new game using the current options.
     */
    public void startNewGame()
    {
        reset();
        winnerDialogShown_ = false;
        this.sendGameChangedEvent(null);  // get the info panel to refresh with 1st players name

        if (controller_.getFirstPlayer().isSurrogate()) {
            doSurrogateMove((SurrogatePlayer) controller_.getCurrentPlayer());
        }
        else if (!controller_.getFirstPlayer().isHuman()) {
            controller_.computerMovesFirst();
        }
    }

     /**
      * display a dialog at the end of the game showing who won and other relevant
      * game specific information.
      */
    protected void showWinnerDialog()
    {
        String message = getGameOverMessage();
        JOptionPane.showMessageDialog( this, message, GameContext.getLabel("GAME_OVER"),
                   JOptionPane.INFORMATION_MESSAGE );
    }


    /**
     * @return   the message to display at the completion of the game.
     */
    protected abstract String getGameOverMessage();


    /**
     * make the computer move and show it on the screen.
     *
     * @param player computer player to move
     * @return done return true if the game is over after moving
     */
    public abstract boolean doComputerMove(Player player);

    /**
     * make the computer move and show it on the screen.
     *
     * @param player computer player to move
     * @return done return true if the game is over after moving
     */
    public abstract boolean doSurrogateMove(SurrogatePlayer player);

    /**
     * Do nothting by default.
     * @param action to take
     * @param player to apply it to
     * @return message to show if on client.
     */
    protected String applyAction(PlayerAction action,  Player player) {
        return null;
    }

    /**
     * Implements the GameChangedListener interface.
     * Called when the game has changed in some way
     * @param evt
     */
    public void gameChanged(GameChangedEvent evt)
    {
        if (controller_.isDone() && !winnerDialogShown_)  {
            winnerDialogShown_ = true;
            showWinnerDialog();
        }
        else if (!winnerDialogShown_) {
             super.gameChanged(evt);
        }
    }


    /**
     * Many multiplayer games don't use this.
     * @param lastMove the move to show (but now record)
     */
    public Move createMove(Move lastMove)
    {
        // unused for now
        return null;
    }

    /**
     * show who won the round and dispurse the pot.
     * Don't show anything by default.
     */
    public void showRoundOver() {};


    public void highlightPlayer(Player player, boolean highlighted)
    {
        // player.setHighlighted(highlighted);
        this.refresh();
    }

    /**
     * @return the tooltip for the panel given a mouse event
     */
    public String getToolTipText( MouseEvent e )
    {
        Location loc = getBoardRenderer().createLocation(e);
        StringBuffer sb = new StringBuffer( "<html><font=-3>" );

        if (controller_.getBoard() != null) {
            BoardPosition space = controller_.getBoard().getPosition( loc );
            if ( space != null && space.isOccupied() && GameContext.getDebugMode() >= 0 ) {
                sb.append("<br>");
                sb.append( loc );
            }
            sb.append( "</font></html>" );
        }
        return sb.toString();
    }

}