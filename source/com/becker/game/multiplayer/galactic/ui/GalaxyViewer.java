package com.becker.game.multiplayer.galactic.ui;

import ca.dj.jigo.sgf.tokens.MoveToken;
import com.becker.game.common.*;
import com.becker.game.common.ui.GameBoardViewer;
import com.becker.game.common.ui.GameChangedEvent;
import com.becker.game.multiplayer.galactic.*;
import com.becker.game.common.Move;

import javax.swing.*;
import java.awt.event.MouseEvent;
import java.awt.*;
import java.awt.geom.Point2D;
import java.util.*;
import java.util.List;

/**
 *  Takes a GalacticController as input and displays the
 *  current state of the Galactic Empire Game. The GalacticController contains a Galaxy object
 *  which describes this state.
 *
 *  @author Barry Becker
 */
public class GalaxyViewer extends GameBoardViewer
{

    private static final Color GRID_COLOR = Color.GRAY;

    //Construct the application
    public GalaxyViewer()
    {
        pieceRenderer_ = PlanetRenderer.getRenderer();
    }

    protected GameController createController()
    {
        return new GalacticController();
    }

    protected int getDefaultCellSize()
    {
        return 16;
    }

    protected Color getDefaultGridColor()
    {
        return GRID_COLOR;
    }

    /**
     * start over with a new game using the current options.
     */
    public final void startNewGame()
    {
        reset();
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
    {
        //Location loc = createLocation(e, getCellSize());
        //Galaxy board = (Galaxy) controller_.getBoard();
        // nothing to do here really for this kind of game
    }



     /**
      * display a dialog at the end of the game showing who won and other relevant
      * game specific information.
      */
     protected void showWinnerDialog()
     {
         //String message = getGameOverMessage();
         TallyDialog tallyDialog = new TallyDialog(null, (GalacticController)controller_);
         tallyDialog.setLocationRelativeTo( this );
         tallyDialog.showDialog();

         //JOptionPane.showMessageDialog( this, message, GameContext.getLabel("GAME_OVER"),
         //          JOptionPane.INFORMATION_MESSAGE );
     }


    /**
     * @return   the message to display at the completion of the game.
     */
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
    public boolean doComputerMove(Player player)
    {
        assert(!player.isHuman());
        GalacticRobotPlayer robot = (GalacticRobotPlayer)player;
        robot.makeOrders((Galaxy)getBoard());

        // records the result on the board.
        Move lastMove = controller_.getLastMove();
        GalacticMove gmove = GalacticMove.createMove((lastMove==null)?0:lastMove.moveNumber+1);
        controller_.makeMove(gmove);

        gameChanged(null);
        ((GalacticController)controller_).advanceToNextPlayer();

        return false;
    }

    /**
     * Implements the GameChangedListener interface.
     * Called when the game has changed in some way
     * @param evt
     */
    public void gameChanged(GameChangedEvent evt)
    {
        if (controller_.done())
            this.showWinnerDialog();
        else {
            super.gameChanged(evt);
        }
    }


    /**
     * This will run all the battle similations needed to calculate the result and put it in the new move.
     * Simulations may actually be a reinforcements instead of a battle.
     * @param lastMove the move to show (but now record)
     */
    public GalacticMove createMove(Move lastMove)
    {
        GalacticMove gmove = GalacticMove.createMove((lastMove==null)?0:lastMove.moveNumber+1);

        // for each order of each player, apply it for one year
        // if there are battles, show them in the battle dialog and record the result in the move.
        Player[] players = controller_.getPlayers();

        for (int i=0; i< players.length; i++) {
            List orders = ((GalacticPlayer)players[i]).getOrders();
            Iterator orderIt = orders.iterator();
            while (orderIt.hasNext()) {
                Order order = (Order)orderIt.next();
                // have we reached our destination?
                // if so show and record the battle, and then remove the order from the list.
                // If not adjust the distance remaining.
                order.incrementYear();
                if (order.hasArrived()) {

                    Planet destPlanet = order.getDestination();
                    BattleSimulation battle = new BattleSimulation(order, destPlanet);
                    gmove.addSimulation(battle);

                    //  show battle dialog if not all computers playing
                    if (!controller_.allPlayersComputer()) {

                        BattleDialog bDlg = new BattleDialog(null, battle);
                        bDlg.setLocationRelativeTo(this);

                        Point p = this.getParent().getLocationOnScreen();
                        // offset the dlg so the Galaxy grid is visible as a reference.
                        bDlg.setLocation((int)(p.getX()+.7*getParent().getWidth()), (int)(p.getY()+getParent().getHeight()-10));
                        bDlg.setModal(true);
                        bDlg.setVisible(true);
                    }

                    destPlanet.setOwner( battle.getOwnerAfterAttack());
                    destPlanet.setNumShips( battle.getNumShipsAfterAttack() );

                    // remove this order as it has arrived.
                    orderIt.remove();
                }
            }
        }
        return gmove;
    }



    private static final float OFFSET = .25f;
    /**
     * Draw the pieces and possibly other game markers for both players.
     */
    protected void drawMarkers( int nrows, int ncols, Graphics2D g2 )
    {

        // before we draw the planets, draw the fleets and their paths
        Player[] players = controller_.getPlayers();
        for (int i=0; i< players.length; i++) {
            List orders = ((GalacticPlayer)players[i]).getOrders();
            Iterator orderIt = orders.iterator();
            while (orderIt.hasNext()) {
                Order order = (Order)orderIt.next();

                Location begin = order.getOrigin().getLocation();
                Point2D end = order.getCurrentLocation();

                g2.setColor(order.getOwner().getColor());
                int endX = (int)(cellSize_*(end.getX()-OFFSET ));
                int endY = (int)(cellSize_*(end.getY()-OFFSET ));
                g2.drawLine((int)(cellSize_*(begin.col-OFFSET )), (int)(cellSize_*begin.row-OFFSET ),  endX, endY);
                // the triangle at the end of the line representing the fleet
                int rad = (int)Math.sqrt(order.getFleetSize());
                g2.drawOval((int)(endX-rad/2.0), (int)(endY-rad/2.0), rad, rad);
            }
        }

        // now draw the planets on top
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
            sb.append(((Planet)space.getPiece()).toHtml());
            sb.append("<br>");
            sb.append( loc );
        }
        sb.append( "</font></html>" );
        return sb.toString();
    }

}
