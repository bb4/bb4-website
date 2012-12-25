/** Copyright by Barry G. Becker, 2000-2011. Licensed under MIT License: http://www.opensource.org/licenses/MIT  */
package com.barrybecker4.game.multiplayer.trivial.ui;

import com.barrybecker4.game.common.IGameController;
import com.barrybecker4.game.common.board.Board;
import com.barrybecker4.game.common.ui.viewer.GameBoardRenderer;
import com.barrybecker4.game.multiplayer.common.MultiGamePlayer;
import com.barrybecker4.game.multiplayer.common.MultiPlayerMarker;
import com.barrybecker4.game.multiplayer.common.ui.MultiGameBoardRenderer;

import java.awt.*;

/**
 * Singleton class that takes a game board and renders it for the GameBoardViewer.
 * Having the board renderer separate from the viewer helps to separate out the rendering logic
 * from other features of the GameBoardViewer.
 *
 * @author Barry Becker
 */
public class TrivialGameRenderer extends MultiGameBoardRenderer {

    private  static GameBoardRenderer renderer_;


    /**
     * private constructor because this class is a singleton.
     * Use getRenderer instead
     */
    private TrivialGameRenderer() {
        pieceRenderer_ = TrivialPlayerRenderer.getRenderer();
    }

    public static GameBoardRenderer getRenderer() {
        if (renderer_ == null)
            renderer_ = new TrivialGameRenderer();
        return renderer_;
    }

    /**
     * draw a grid of some sort if there is one.
     * none by default.
     */
    @Override
    protected void drawGrid(Graphics2D g2, int startPos, int rightEdgePos, int bottomEdgePos, int start,
                            int nrows1, int ncols1, int gridOffset) {}

    @Override
    protected void drawBackground( Graphics g, Board board, int startPos, int rightEdgePos, int bottomEdgePos,
                                   int panelWidth, int panelHeight ) {
        super.drawBackground(g, board, startPos, rightEdgePos, bottomEdgePos, panelWidth, panelHeight);
        drawTable(g, board, panelWidth, panelHeight);
    }

    /**
     * Draw the pieces and possibly other game markers for both players.
     * Draw a cicle on the background  for the player whose turn it is.
     */
    @Override
    protected void drawMarkers( IGameController controller, Graphics2D g2 ) {

        MultiGamePlayer player = (MultiGamePlayer)controller.getCurrentPlayer();
        MultiPlayerMarker m = ((MultiGamePlayer) player.getActualPlayer()).getPiece();
        assert m != null;
        assert g2 != null;
        assert m.getLocation() != null;
        g2.setColor(TrivialPlayerRenderer.HIGHLIGHT_COLOR);
        g2.fillOval(cellSize *(m.getLocation().getCol()-2), cellSize *(m.getLocation().getRow()-2), 10* cellSize, 10* cellSize);

        // now draw the players and their stuff (face, name, chips, cards, etc)
        super.drawMarkers(controller, g2);
    }

}
