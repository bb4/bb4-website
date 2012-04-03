// Copyright by Barry G. Becker, 2012. Licensed under MIT License: http://www.opensource.org/licenses/MIT
package com.becker.puzzle.tantrix.ui;

import com.becker.common.geometry.Location;
import com.becker.puzzle.tantrix.model.HexTile;
import com.becker.puzzle.tantrix.model.TantrixBoard;

import java.awt.*;

/**
 * Renders the the sudoku puzzle onscreen.
 * @author Barry Becker
 */
public class TantrixBoardRenderer {

    static final int MARGIN = 50;
    private static final double ROOT3D2 = Math.sqrt(3.0)/2.0;

    private static final Color BACKGROUND_COLOR = new Color(245, 245, 255, 100);
    private static final Color GRID_COLOR = new Color(10, 0, 100);

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

    public void setBoard(TantrixBoard board) {
        board_ = board;
    }

    public TantrixBoard getBoard() {
        return board_;
    }

    /**
     * This renders the current state of the TantrixBoard to the screen.
     */
    public void render(Graphics g, int width, int height)  {

        Graphics2D g2 = (Graphics2D) g;
        int minEdge = (Math.min(width, height) - 2 * MARGIN);
        hexRadius = minEdge / board_.getEdgeLength() / ROOT3D2 / 2.0;

        drawGrid(g2, board_.getEdgeLength());

        // erase what's there and redraw.
        g.setColor( BACKGROUND_COLOR );
        g.fillRect(0, 0, width, height);

        int len =  board_.getEdgeLength();

        for ( int i = 0; i < len; i++ ) {
            for ( int j = 0; j < len; j++ ) {

                HexTile tile = board_.getTile(i, j);
                if (tile != null) {
                    tileRenderer.render(g2, new Location(i, j), tile, hexRadius);
                }
            }
        }
    }


    /**
     * Draw the gridlines over the background.
     */
    protected void drawGrid(Graphics2D g2,
                            int edgeLen) {
        g2.setColor( GRID_COLOR );
        int xpos, ypos;
        int i;
        int start = 0;
        int startPos = MARGIN;
        double hexWidth = 2 * hexRadius * ROOT3D2;
        int rightEdgePos = (int)(MARGIN + hexWidth * edgeLen);
        int bottomEdgePos = (int)(MARGIN + hexWidth * edgeLen);

        for ( i = start; i <= edgeLen; i++ )  //   -----
        {
            ypos = (int)(MARGIN + i * hexWidth);
            g2.drawLine( startPos, ypos, rightEdgePos, ypos );
        }
        for ( i = start; i <= edgeLen; i++ )  //   ||||
        {
            xpos = (int)(MARGIN + i * hexWidth);
            g2.drawLine( xpos, startPos, xpos, bottomEdgePos );
        }
    }
}