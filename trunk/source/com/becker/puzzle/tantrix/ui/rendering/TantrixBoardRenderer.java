// Copyright by Barry G. Becker, 2012. Licensed under MIT License: http://www.opensource.org/licenses/MIT
package com.becker.puzzle.tantrix.ui.rendering;

import com.becker.common.geometry.Location;
import com.becker.puzzle.tantrix.model.TantrixBoard;
import com.becker.puzzle.tantrix.model.TilePlacement;

import static com.becker.puzzle.tantrix.ui.rendering.HexUtil.*;

import java.awt.*;

/**
 * Renders the the sudoku puzzle onscreen.
 * @author Barry Becker
 */
public class TantrixBoardRenderer {

    static final double MARGIN_FRAC = 0.6;

    private static final Color BACKGROUND_COLOR = new Color(245, 245, 255);
    private static final Color GRID_COLOR = new Color(60, 90, 120);

    private TantrixBoard board_;
    private double hexRadius;
    private HexTileRenderer tileRenderer;

    /**
     * Constructor
     */
    public TantrixBoardRenderer(TantrixBoard board) {
        board_ = board;
        tileRenderer = new HexTileRenderer();
    }

    public TantrixBoard getBoard() {
        return board_;
    }

    /**
     * This renders the current state of the TantrixBoard to the screen.
     */
    public void render(Graphics g, int width, int height)  {

        Graphics2D g2 = (Graphics2D) g;
        int minEdge = Math.min(width, height);
        hexRadius = minEdge / (board_.getEdgeLength() * ROOT3);
        int margin = (int)(MARGIN_FRAC * hexRadius);

        initializeAndClear(g2, width, height);
        drawGrid(g2, margin);

        int len =  board_.getEdgeLength();
        Location topLeftCorner = board_.getBoundingBox().getTopLeftCorner();

        for (Location loc : board_.getTantrixLocations()) {

            TilePlacement placement = board_.getTilePlacement(loc);
            tileRenderer.render(g2, placement, topLeftCorner, hexRadius, margin);
        }
    }

    private void initializeAndClear(Graphics2D g2, int width, int height) {
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                RenderingHints.VALUE_ANTIALIAS_ON);
        // erase what's there and redraw.
        g2.setColor( BACKGROUND_COLOR );
        g2.fillRect(0, 0, width, height);
    }

    /**
     * Draw the gridlines over the background.
     */
    protected void drawGrid(Graphics2D g2, int margin) {

        int edgeLen = board_.getEdgeLength();
        int xpos, ypos;
        int i;
        int start = 0;
        int startPos = margin;
        double hexWidth = 2 * hexRadius * ROOT3D2;
        int rightEdgePos = (int)(margin + hexWidth * edgeLen);
        int bottomEdgePos = (int)(margin + hexWidth * edgeLen);
        g2.setColor( BACKGROUND_COLOR );
        g2.fillRect(0, 0, rightEdgePos, bottomEdgePos);

        g2.setColor( GRID_COLOR );
        for ( i = start; i <= edgeLen; i++ )  //   -----
        {
            ypos = (int)(margin + i * hexWidth);
            g2.drawLine( startPos, ypos, rightEdgePos, ypos );
        }
        for ( i = start; i <= edgeLen; i++ )  //   ||||
        {
            xpos = (int)(margin + i * hexWidth);
            g2.drawLine( xpos, startPos, xpos, bottomEdgePos );
        }
    }
}