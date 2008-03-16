package com.becker.game.multiplayer.poker.ui;

import com.becker.common.*;
import com.becker.game.common.*;
import com.becker.game.common.ui.*;
import com.becker.game.multiplayer.common.ui.MultiGameViewer;
import com.becker.game.multiplayer.poker.*;
import com.becker.game.multiplayer.poker.player.*;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.util.List;

import javax.swing.*;
import java.awt.event.*;

/**
 *  Takes a PokerController as input and displays the
 *  current state of the Poker Game. The PokerController contains a PokerTable object
 *  which describes this state.
 *
 *  @author Barry Becker
 */
public class PokerGameViewer extends MultiGameViewer
{

    //Construct the application
    public PokerGameViewer()
    {
        pieceRenderer_ = PokerRenderer.getRenderer();
    }

    protected PokerController createController()
    {
        return new PokerController();
    }

    /**
     * @return   the message to display at the completion of the game.
     */
    protected String getGameOverMessage()
    {
        StringBuffer buf = new StringBuffer("Game Over\n");

        // find the player with the most money. That's the winner.
        java.util.List<? extends Player> players = controller_.getPlayers();
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

        JOptionPane.showMessageDialog(parent_, msg, robot.getName(), JOptionPane.INFORMATION_MESSAGE);
        refresh();
        pc.advanceToNextPlayer();

        return false;
    }

    /**
     *
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

        List<? extends Player> players = controller_.getPlayers();
        for (final Player p : players) {
            PokerPlayer player = (PokerPlayer) p;
            player.getHand().setFaceUp(true);
        }
        refresh();

        RoundOverDialog roundOverDlg = new RoundOverDialog(null, winner, winnings);

        Point p = this.getParent().getLocationOnScreen();

        // offset the dlg so the board is visible as a reference
        roundOverDlg.setLocation((int)(p.getX()+ 0.9*getParent().getWidth()),
                                 (int)(p.getY()+getParent().getHeight()/3.0));

        roundOverDlg.setVisible(true);
    }
    
    /**
     * 
     * draw a grid of some sort if there is one.
     * none by default.
     */
    protected void drawGrid(Graphics2D g2, int startPos, int rightEdgePos, int bottomEdgePos, int start,
                            int nrows1, int ncols1, int gridOffset) {}

    
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
}
