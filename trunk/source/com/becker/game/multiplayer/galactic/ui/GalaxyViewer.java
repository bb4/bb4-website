package com.becker.game.multiplayer.galactic.ui;

import com.becker.common.Location;
import com.becker.game.common.*;
import com.becker.game.common.board.BoardPosition;
import com.becker.game.common.player.Player;
import com.becker.game.common.player.PlayerAction;
import com.becker.game.common.player.PlayerList;
import com.becker.game.common.ui.viewer.GameBoardRenderer;
import com.becker.game.multiplayer.common.MultiGameController;
import com.becker.game.multiplayer.common.online.SurrogateMultiPlayer;
import com.becker.game.multiplayer.common.ui.MultiGameViewer;
import com.becker.game.multiplayer.galactic.*;
import com.becker.game.multiplayer.galactic.player.GalacticPlayer;
import com.becker.game.multiplayer.galactic.player.GalacticRobotPlayer;

import java.awt.*;
import java.awt.event.MouseEvent;
import java.util.Iterator;
import java.util.List;

/**
 *  Takes a GalacticController as input and displays the
 *  current state of the Galactic Empire Game. The GalacticController contains a Galaxy object
 *  which describes this state.
 *
 *  @author Barry Becker
 */
public class GalaxyViewer extends MultiGameViewer
{

    /** Construct the application   */
    public GalaxyViewer()
    {}

    @Override
    protected MultiGameController createController()
    {
        return new GalacticController();
    }

    @Override
    protected GameBoardRenderer getBoardRenderer() {
        return GalaxyRenderer.getRenderer();
    }
     /**
      * display a dialog at the end of the game showing who won and other relevant
      * game specific information.
      */
    @Override
    public void showWinnerDialog()
    {
        //String message = getGameOverMessage();
        GalacticTallyDialog tallyDialog = new GalacticTallyDialog(parent_, (GalacticController)controller_);
        tallyDialog.showDialog();
    }


    /**
     * @return   the message to display at the completion of the game.
     */
    @Override
    protected String getGameOverMessage()
    {
        return "Game Over";
    }


    /**
     * make the computer move and show it on the screen.
     *
     * @param player computer player to move
     * @return done return true if the game is over after moving
     */
    @Override
    public boolean doComputerMove(Player player)
    {
        assert(!player.isHuman());
        GalacticRobotPlayer robot = (GalacticRobotPlayer)player;
        GalacticController gc = (GalacticController) controller_;
        GameContext.log(1, "now doing computer move. about to make orders");

        robot.makeOrders((Galaxy)getBoard(), gc.getNumberOfYearsRemaining());

        /*
        // records the result on the board.
        Move lastMove = getController().getLastMove();
        GalacticTurn gmove = GalacticTurn.createMove((lastMove==null)? 0 : lastMove.moveNumber + 1);
        gc.makeMove(gmove);
        */
        this.refresh();

        gc.advanceToNextPlayer();
        return false;
    }

    /**
     * make the computer move and show it on the screen.
     *
     * @param player computer player to move
     * @return done return true if the game is over after moving
     */
    @Override
    public boolean doSurrogateMove(SurrogateMultiPlayer player)
    {
        GalacticController pc = (GalacticController) controller_;
        // simply blocks until action set?
        PlayerAction action = player.getAction(pc);
        pc.advanceToNextPlayer();
        return false;
    }


    /**
     * This will run all the battle simulations needed to calculate the result and put it in the new move.
     * Simulations may actually be a reinforcements instead of a battle.
     * @param lastMove the move to show (but now record)
     */
    @Override
    public GalacticTurn createMove(Move lastMove)
    {
        GalacticTurn gmove = GalacticTurn.createMove();

        // for each order of each player, apply it for one year
        // if there are battles, show them in the battle dialog and record the result in the move.
        PlayerList players = controller_.getPlayers();

        for (final Player player : players) {
            //GalacticPlayer gp = (GalacticPlayer) player;
            //GalacticAction ga =  (GalacticAction)gp.getAction((MultiGameController)controller_);
            //List orders = ga.getOrders();
            List orders = ((GalacticPlayer) player).getOrders();
            Iterator orderIt = orders.iterator();
            while (orderIt.hasNext()) {
                Order order = (Order) orderIt.next();
                // have we reached our destination?
                // if so show and record the battle, and then remove the order from the list.
                // If not adjust the distance remaining.
                order.incrementYear();
                if (order.hasArrived()) {

                    Planet destPlanet = order.getDestination();
                    BattleSimulation battle = new BattleSimulation(order, destPlanet);
                    gmove.addSimulation(battle);

                    //  show battle dialog if not all computers playing
                    if (!controller_.getPlayers().allPlayersComputer()) {

                        BattleDialog bDlg = new BattleDialog(parent_, battle, this);
                        //bDlg.setLocationRelativeTo(this);

                        Point p = this.getParent().getLocationOnScreen();
                        // offset the dlg so the Galaxy grid is visible as a reference.
                        bDlg.setLocation((int) (p.getX() + getParent().getWidth()),
                                         (int) (p.getY() + 0.65 * getParent().getHeight()));
                        bDlg.setModal(true);
                        bDlg.setVisible(true);
                    }

                    destPlanet.setOwner(battle.getOwnerAfterAttack());
                    destPlanet.setNumShips(battle.getNumShipsAfterAttack());

                    // remove this order as it has arrived.
                    orderIt.remove();
                }
            }
        }
        return gmove;
    }

    public void showPlanetUnderAttack(Planet planet, boolean showAttacked)
    {
        planet.setUnderAttack(showAttacked);
        this.refresh();
    }

    public void highlightPlanet(Planet planet, boolean hightlighted)
    {
        planet.setHighlighted(hightlighted);
        this.refresh();
    }

    /**
     * @return the tooltip for the panel given a mouse event
     */
    @Override
    public String getToolTipText( MouseEvent e )
    {
        Location loc = getBoardRenderer().createLocation(e);
        StringBuilder sb = new StringBuilder( "<html><font=-3>" );

        BoardPosition space = controller_.getBoard().getPosition( loc );
        if ( space != null && space.isOccupied() && GameContext.getDebugMode() >= 0 ) {
            sb.append(((Planet)space.getPiece()).toHtml());
            sb.append("<br>");
            sb.append( loc );
        }
        sb.append( "</font></html>" );
        return sb.toString();
    }

}
