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

    private static final Color DEFAULT_GRID_COLOR = Color.GRAY;
    private static final Color DEFAULT_TABLE_COLOR = new Color(190, 160, 110);
    protected boolean winnerDialogShown_ = false;

    // Construct the application
    public MultiGameViewer() {}

    protected abstract MultiGameController createController();

    protected int getDefaultCellSize()
    {
        return 8;
    }

    protected Color getDefaultGridColor()
    {
        return DEFAULT_GRID_COLOR;
    }
    
    protected Color getDefaultTableColor()
    {
        return DEFAULT_TABLE_COLOR;
    }

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
     * whether or not to draw the pieces on cell centers or vertices (like go or pente, but not like checkers).
     */
    protected boolean offsetGrid()
    {
        return true;
    }

    protected void drawLastMoveMarker(Graphics2D g2)
    {}

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
     * Draw the background and a depiction of a circular game table
     * @param g
     * @param startPos
     * @param rightEdgePos
     * @param bottomEdgePos
     */
    protected void drawBackground(Graphics g, int startPos, int rightEdgePos, int bottomEdgePos )
    {
        super.drawBackground(g, startPos, rightEdgePos, bottomEdgePos);
        g.setColor( backgroundColor_ );
        int width = this.getBoard().getNumCols() * this.getCellSize();
        int height = this.getBoard().getNumRows() * this.getCellSize();
        g.setColor(getDefaultTableColor());
        g.fillOval((int)(0.05*width), (int)(0.05*height), (int)(0.9*width), (int)(0.9*height));
    }

    /**
     * @return the tooltip for the panel given a mouse event
     */
    public String getToolTipText( MouseEvent e )
    {
        Location loc = createLocation(e, getCellSize());
        StringBuffer sb = new StringBuffer( "<html><font=-3>" );

        if (controller_.getBoard() != null) {
            BoardPosition space = controller_.getBoard().getPosition( loc );
            if ( space != null && space.isOccupied() && GameContext.getDebugMode() >= 0 ) {
                //sb.append(((Planet)space.getPiece()).toHtml());
                sb.append("<br>");
                sb.append( loc );
            }
            sb.append( "</font></html>" );
        }
        return sb.toString();
    }

}
