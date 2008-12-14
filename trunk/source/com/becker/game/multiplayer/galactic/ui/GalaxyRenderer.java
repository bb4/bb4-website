package com.becker.game.multiplayer.galactic.ui;

import com.becker.game.common.Board;
import com.becker.game.common.Player;
import com.becker.game.common.GameControllerInterface;

import java.awt.geom.Point2D;
import java.awt.*;
import java.util.*;
import java.util.List;

import com.becker.game.common.ui.GameBoardRenderer;
import com.becker.game.common.ui.GameBoardViewer;
import com.becker.game.multiplayer.common.ui.MultiGameBoardRenderer;
import com.becker.game.multiplayer.galactic.player.GalacticPlayer;
import com.becker.game.multiplayer.galactic.Order;
import com.becker.common.Location;


/**
 * Singleton class that takes a game board and renders it for the GameBoardViewer.
 * Having the board renderer separate from the viewer helps to separate out the rendering logic
 * from other features of the GameBoardViewer.
 *
 * @author Barry Becker
 */
public class GalaxyRenderer extends MultiGameBoardRenderer
{
    private  static GameBoardRenderer renderer_;


    /**
     * private constructor because this class is a singleton.
     * Use getRenderer instead
     */
    private GalaxyRenderer()
    {
        pieceRenderer_ = PlanetRenderer.getRenderer();
    }

    public static GameBoardRenderer getRenderer()
    {
        if (renderer_ == null)
            renderer_ = new GalaxyRenderer();
        return renderer_;
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
     * Draw the pieces and possibly other game markers for both players.
     */
    protected void drawMarkers(GameControllerInterface controller, Graphics2D g2 )
    {
        // before we draw the planets, draw the fleets and their paths
        java.util.List<? extends Player> players = controller.getPlayers();
        for (final Player player : players) {
            java.util.List orders = ((GalacticPlayer) player).getOrders();
            Iterator orderIt = orders.iterator();
            while (orderIt.hasNext()) {
                Order order = (Order) orderIt.next();
                int margin = GameBoardRenderer.BOARD_MARGIN;

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
        super.drawMarkers(controller, g2);
    }

}

