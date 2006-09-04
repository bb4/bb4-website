package com.becker.game.multiplayer.galactic.ui;

import ca.dj.jigo.sgf.tokens.*;
import com.becker.game.common.*;
import com.becker.game.common.ui.*;
import com.becker.game.multiplayer.galactic.*;
import com.becker.common.*;

import java.awt.*;
import java.awt.event.*;
import java.awt.geom.*;
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

    private boolean winnerDialogShown_ = false;

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
         GalacticTallyDialog tallyDialog = new GalacticTallyDialog(parent_, (GalacticController)controller_);
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
        GalacticController gc = (GalacticController) controller_;
        robot.makeOrders((Galaxy)getBoard(), gc.getNumberOfYearsRemaining());

        /*
        // records the result on the board.
        Move lastMove = getBoard().getLastMove();
        GalacticTurn gmove = GalacticTurn.createMove((lastMove==null)? 0 : lastMove.moveNumber + 1);
        gc.makeMove(gmove);
        */
        this.refresh();

        gc.advanceToNextPlayer();

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
            this.showWinnerDialog();
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
    public GalacticTurn createMove(Move lastMove)
    {
        GalacticTurn gmove = GalacticTurn.createMove();

        // for each order of each player, apply it for one year
        // if there are battles, show them in the battle dialog and record the result in the move.
        Player[] players = controller_.getPlayers();

        for (final Player player : players) {
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
                    if (!controller_.allPlayersComputer()) {

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
     * Draw the pieces and possibly other game markers for both players.
     */
    protected void drawMarkers( int nrows, int ncols, Graphics2D g2 )
    {

        // before we draw the planets, draw the fleets and their paths
        Player[] players = controller_.getPlayers();
        for (final Player player : players) {
            List orders = ((GalacticPlayer) player).getOrders();
            Iterator orderIt = orders.iterator();
            while (orderIt.hasNext()) {
                Order order = (Order) orderIt.next();
                int margin = GameBoardViewer.BOARD_MARGIN;

                Location begin = order.getOrigin().getLocation();
                Point2D end = order.getCurrentLocation();

                g2.setColor(order.getOwner().getColor());
                int beginX = (int) (margin + cellSize_ * (begin.getCol() - 0.5));
                int beginY = (int) (margin + cellSize_ * begin.getRow() - 0.5);
                int endX = (int) (margin + cellSize_ * (end.getX() - 0.5));
                int endY = (int) (margin + cellSize_ * (end.getY() - 0.5));

                g2.drawLine(beginX, beginY,  endX, endY);

                // the glyph at the end of the line representing the fleet
                int rad = (int) Math.round(Math.sqrt(order.getFleetSize()));
                g2.drawOval((int) (endX - rad / 2.0), (int) (endY - rad / 2.0), rad, rad);
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
