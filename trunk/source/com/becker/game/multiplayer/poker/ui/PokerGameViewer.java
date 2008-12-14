package com.becker.game.multiplayer.poker.ui;

import com.becker.common.*;
import com.becker.game.common.*;
import com.becker.game.common.ui.*;
import com.becker.game.multiplayer.common.online.SurrogatePlayer;
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
    {}

    protected PokerController createController()
    {
        return new PokerController();
    }

    protected GameBoardRenderer getBoardRenderer() {
        return PokerGameRenderer.getRenderer();
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

        String msg = applyAction(robot.getAction(pc), robot);

        JOptionPane.showMessageDialog(parent_, msg, robot.getName(), JOptionPane.INFORMATION_MESSAGE);
        refresh();
        pc.advanceToNextPlayer();

        return false;
    }


    /**
     * make the computer move and show it on the screen.
     *
     * @param player computer player to move
     * @return done return true if the game is over after moving
     */
    public boolean doSurrogateMove(SurrogatePlayer player)
    {
        PokerController pc = (PokerController) controller_;
        PlayerAction action = player.getAction(pc);

        applyAction(action, player.getPlayer());

        pc.advanceToNextPlayer();

        return false;
    }

    /**
     * @param action to take
     * @param player to apply it to
     * @return message to show if on client.
     */
    protected String applyAction(PlayerAction action,  Player player) {

        PokerPlayer p = (PokerPlayer) player;
        PokerAction act = (PokerAction) action;
        PokerController pc = (PokerController) controller_;

        String msg = null;
        int callAmount = pc.getCurrentMaxContribution() - p.getContribution();

        switch (act.getActionName()) {
            case FOLD :
                p.setFold(true);
                msg = p.getName() + " folded.";
                break;
            case CALL :
                // System.out.println("PGV: robot call amount = currentMaxContrib - robot.getContrib) = "
                //                   + pc.getCurrentMaxContribution()+" - "+robot.getContribution());
                if (callAmount <= p.getCash())  {
                    p.contributeToPot(pc, callAmount);
                    msg = p.getName() + " has called by adding "+ callAmount + " to the pot.";
                } else {
                    p.setFold(true);
                    msg = p.getName() + " folded.";
                }
                break;
            case RAISE :
                p.contributeToPot(pc, callAmount);
                int raise = act.getRaiseAmount();
                p.contributeToPot(pc, raise);
                msg = p.getName() + " has met the "+callAmount + ", and rasied the pot by " + raise;
                break;
        }
        return msg;
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
}
