package com.becker.game.multiplayer.poker.ui;

import ca.dj.jigo.sgf.tokens.MoveToken;
import com.becker.game.common.*;
import com.becker.game.common.ui.GameBoardViewer;
import com.becker.game.common.ui.GameChangedEvent;
import com.becker.game.common.Move;
import com.becker.game.multiplayer.poker.*;
import com.becker.game.multiplayer.poker.player.*;
import com.becker.common.*;

import javax.swing.*;
import java.awt.event.MouseEvent;
import java.awt.*;

/**
 *  Takes a PokerController as input and displays the
 *  current state of the Poker Game. The PokerController contains a PokerTable object
 *  which describes this state.
 *
 *  @author Barry Becker
 */
public class PokerGameViewer extends GameBoardViewer
{

    private static final Color DEFAULT_GRID_COLOR = Color.GRAY;
    private static final Color TABLE_COLOR = new Color(190, 160, 110);
    private boolean winnerDialogShown_ = false;

    //Construct the application
    public PokerGameViewer()
    {
        pieceRenderer_ = PokerRenderer.getRenderer();
    }

    protected PokerController createController()
    {
        return new PokerController();
    }

    protected int getDefaultCellSize()
    {
        return 8;
    }

    protected Color getDefaultGridColor()
    {
        return DEFAULT_GRID_COLOR;
    }

    /**
     * start over with a new game using the current options.
     */
    public final void startNewGame()
    {
        reset();
        winnerDialogShown_ = false;
        this.sendGameChangedEvent(null);  // get the info panel to refresh with 1st players name

        if (!controller_.getFirstPlayer().isHuman())
            controller_.computerMovesFirst();
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
     * This will create a move from an SGF token
     */
    protected Move createMoveFromToken( MoveToken token, int moveNum )
    {
        GameContext.log(0, "not implemented yet" );
        return null;
    }

    public void mousePressed( MouseEvent e )
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
    protected String getGameOverMessage()
    {
        StringBuffer buf = new StringBuffer("Game Over\n");

        // find the player with the most money. That's the winner.
        Player[] players = controller_.getPlayers();
        int max = -1;
        PokerPlayer winner = null;
        for (final Player p : players) {
            PokerPlayer pp = (PokerPlayer) p;
            if (pp.getCash() > max) {
                max = pp.getCash();
                winner = pp;
            }
        }

        buf.append(winner.getName() + " won the game with $"+ winner.getCash() +'.');
        return buf.toString();
    }


    /**
     * make the computer move and show it on the screen.
     *
     * @param player computer player to move
     * @return done return true if the game is over after moving
     */
    public boolean doComputerMove(Player player)
    {
        assert(!player.isHuman());
        PokerRobotPlayer robot = (PokerRobotPlayer)player;
        PokerController pc = (PokerController) controller_;

        String msg = null;
        int callAmount = pc.getCurrentMaxContribution() - robot.getContribution();

        PokerAction action = robot.getAction(pc);

        switch (action.getActionName()) {
            case FOLD :
                robot.setFold(true);
                msg = robot.getName() + " folded.";
                break;
            case CALL :
                // System.out.println("PGV: robot call amount = currentMaxContrib - robot.getContrib) = "
                //                   + pc.getCurrentMaxContribution()+" - "+robot.getContribution());
                if (callAmount <= robot.getCash())   {
                    robot.contributeToPot(pc, callAmount);
                    msg = robot.getName() + " has called by adding "+ callAmount + " to the pot.";
                } else {
                    robot.setFold(true);
                    msg = robot.getName() + " folded.";
                }
                break;
            case RAISE :
                robot.contributeToPot(pc, callAmount);
                int raise = action.getRaiseAmount();
                robot.contributeToPot(pc, raise);
                msg = robot.getName() + " has met the "+callAmount + ", and rasied the pot by " + raise;
                break;
        }

        JOptionPane.showMessageDialog(parent_, msg, "Robot Name", JOptionPane.INFORMATION_MESSAGE);
        refresh();
        pc.advanceToNextPlayer();

        return false;
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
     * This will run all the battle simulations needed to calculate the result and put it in the new move.
     * Simulations may actually be a reinforcements instead of a battle.
     * @param lastMove the move to show (but now record)
     */
    public PokerRound createMove(Move lastMove)
    {
        return PokerRound.createMove();
    }


    /**
     * show who won the round and dispurse the pot
     */
    public void showRoundOver(PokerPlayer winner, int winnings) {

        Player[] players = controller_.getPlayers();
        for (final Player p : players) {
            PokerPlayer player = (PokerPlayer) p;
            player.getHand().setFaceUp(true);
        }
        refresh();

        RoundOverDialog roundOverDlg = new RoundOverDialog(null, winner, winnings);

        Point p = this.getParent().getLocationOnScreen();

        // offset the dlg so the board is visible as a reference
        roundOverDlg.setLocation((int)(p.getX()+ 0.9*getParent().getWidth()),
                                 (int)(p.getY()+getParent().getHeight()/3));

        roundOverDlg.setVisible(true);
    }


    public void highlightPlayer(Player player, boolean hightlighted)
    {
        // player.setHighlighted(hightlighted);
        this.refresh();
    }


    protected void drawBackground(Graphics g, int startPos, int rightEdgePos, int bottomEdgePos )
    {
        super.drawBackground(g, startPos, rightEdgePos, bottomEdgePos);
        g.setColor( backgroundColor_ );
        int width = this.getBoard().getNumCols() * this.getCellSize();
        int height = this.getBoard().getNumRows() * this.getCellSize();
        g.setColor(TABLE_COLOR);
        g.fillOval((int)(0.05*width), (int)(0.05*height), (int)(0.9*width), (int)(0.9*height));
    }

    /**
     * no grid in poker
     */
    protected void drawGrid(Graphics2D g2, int startPos, int rightEdgePos, int bottomEdgePos, int start,
                            int nrows1, int ncols1, int gridOffset) {
    }

    /**
     * Draw the pieces and possibly other game markers for both players.
     */
    protected void drawMarkers( int nrows, int ncols, Graphics2D g2 )
    {
        // draw the pot in the middle
        Location loc = new Location(getBoard().getNumRows() >> 1, (getBoard().getNumCols() >> 1) - 3);
        int pot = ((PokerController)controller_).getPotValue();
        ((PokerRenderer)pieceRenderer_).renderChips(g2, loc, pot, this.getCellSize());

        // draw a backroung circle for the player whose turn it is
        PokerPlayer player = (PokerPlayer)controller_.getCurrentPlayer();
        PokerPlayerMarker m = player.getPiece();
        g2.setColor(PokerRenderer.HIGHLIGHT_COLOR);
        g2.fillOval(cellSize_*(m.getLocation().getCol()-2), cellSize_*(m.getLocation().getRow()-2), 10*cellSize_, 10*cellSize_);

        // now draw the players and their stuff (face, anme, chips, cards, etc)
        super.drawMarkers(nrows, ncols, g2);
    }

    /**
     * @return the tooltip for the panel given a mouse event
     */
    public String getToolTipText( MouseEvent e )
    {
        Location loc = createLocation(e, getCellSize());
        StringBuffer sb = new StringBuffer( "<html><font=-3>" );

        BoardPosition space = controller_.getBoard().getPosition( loc );
        if ( space != null && space.isOccupied() && GameContext.getDebugMode() >= 0 ) {
            //sb.append(((Planet)space.getPiece()).toHtml());
            sb.append("<br>");
            sb.append( loc );
        }
        sb.append( "</font></html>" );
        return sb.toString();
    }

}
