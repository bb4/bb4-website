package com.becker.game.twoplayer.blockade.ui;

import com.becker.common.Location;
import com.becker.game.common.board.Board;
import com.becker.game.common.board.BoardPosition;
import com.becker.game.common.GameContext;
import com.becker.game.common.IGameController;
import com.becker.game.common.ui.viewer.GameBoardRenderer;
import com.becker.game.twoplayer.blockade.BlockadeBoard;
import com.becker.game.twoplayer.blockade.BlockadeBoardPosition;
import com.becker.game.twoplayer.blockade.BlockadeWall;
import com.becker.game.twoplayer.common.ui.TwoPlayerBoardRenderer;
import com.becker.game.twoplayer.common.ui.TwoPlayerPieceRenderer;

import java.awt.*;
import java.util.Iterator;
import java.util.Set;

/**
 * Singleton class that takes a game board and renders it for the GameBoardViewer.
 * Having the board renderer separate from the viewer helps to separate out the rendering logic
 * from other features of the GameBoardViewer.
 *
 * @author Barry Becker
 */
public class BlockadeBoardRenderer extends TwoPlayerBoardRenderer
{
    private static GameBoardRenderer renderer_;

    /** wall that gets dragged around until the player places it.   */
    private BlockadeWall draggedWall_;

    /**
     * private constructor because this class is a singleton.
     * Use getRenderer instead
     */
    protected BlockadeBoardRenderer()
    {
        pieceRenderer_ = BlockadePieceRenderer.getRenderer();
    }

    public static GameBoardRenderer getRenderer()
    {
        if (renderer_ == null)
            renderer_ = new BlockadeBoardRenderer();
        return renderer_;
    }

    public void setDraggedWall(BlockadeWall draggedWall) {
        draggedWall_ = draggedWall;
    }

    public BlockadeWall getDraggedWall() {
        return draggedWall_;
    }

    @Override
    protected int getPreferredCellSize()
    {
        return 24;
    }


    /**
     * returns an index corresponding to a triangular cell segment according to:
     * <pre>
     * \  3 | 2 /
     * 4 \  | /  1
     * -------------
     * 5 /  | \  0
     * /  6 | 7 \
     * </pre>
     *@@ We could make this an enum with values NEE, NNE, NNW, NWW, SWW, etc
     * @param xp x position
     * @param yp y position
     * @return wall index corresponding to specified position.
     */
    public int getWallIndexForPosition(int xp, int yp, Location loc, BlockadeBoard b)
    {
        int numRows = b.getNumRows();
        int numCols = b.getNumCols();
        float x = (float)xp/cellSize_ - (int)(xp / cellSize_);
        float y = (float)yp/cellSize_ - (int)(yp / cellSize_);

        if (loc.getCol() >= numCols)
           x = Math.min(0.499f, x);
        else if (loc.getCol() <= 1)
           x = Math.max(0.501f, x);

        if (loc.getRow() >= numRows)
           y = Math.min(0.499f, y);
        else if (loc.getRow() <=1 )
           y = Math.max(0.501f, y);

        if (x <= 0.5f) {
            if (y <= 0.5f) {    // upper left
                return (y > x)? 4 : 3;
            }
            else {  // y > .5  // lower left
                return ((y - 0.5f) > (0.5f - x))?  6 : 5;
            }
        }
        else { // x>.5
            if (y <= 0.5f) {    // upper right
                return (y < (1.0f-x))? 2 : 1;
            }
            else {  // y > .5  // lower right
                return (y < x)? 0 : 7;
            }
        }
    }


    @Override
    protected void drawBackground( Graphics g, Board b, int startPos, int rightEdgePos, int bottomEdgePos,
                                   int panelWidth, int panelHeight)
    {
        super.drawBackground(g, b, startPos, rightEdgePos, bottomEdgePos, panelWidth, panelHeight);

        BlockadeBoard bb = (BlockadeBoard)b;
        drawHomeBases(g, bb, true);
        drawHomeBases(g, bb, false);
    }


    /**
     * Draw the home bases for the specified player.
     */
    private void drawHomeBases(Graphics g, BlockadeBoard board, boolean player1)
    {
        // draw the home bases
        BoardPosition[] homes = player1? board.getPlayer1Homes() : board.getPlayer2Homes();

        int cellSize = this.getCellSize();
        int offset = Math.round((float)cellSize / 4.0f);
        TwoPlayerPieceRenderer renderer = (TwoPlayerPieceRenderer) pieceRenderer_;
        g.setColor(player1? renderer.getPlayer1Color(): renderer.getPlayer2Color());

        for (BoardPosition home : homes) {
            g.drawOval(getMargin() + (home.getCol() - 1) * cellSize + offset,
                       getMargin() + (home.getRow() - 1) * cellSize + offset,
                       2 * offset, 2 * offset);
        }
    }


    @Override
    protected void drawMarkers( IGameController controller, Graphics2D g2 )
    {
        BlockadeBoard board = (BlockadeBoard)controller.getBoard();
        int nrows = board.getNumRows();
        int ncols = board.getNumCols();
        int cellSize = this.getCellSize();

        boolean drewWall = false;
        for ( int i = 1; i <= nrows; i++ )  {
            for ( int j = 1; j <= ncols; j++ ) {
                BlockadeBoardPosition pos = (BlockadeBoardPosition)board.getPosition( i, j );
                drewWall = drewWall || BlockadePieceRenderer.renderWallAtPosition( g2,  pos, cellSize, getMargin() );
                if (pos.isOccupied() && GameContext.getDebugMode() > 0)
                   PathRenderer.drawShortestPaths(g2, pos, board, cellSize_);
            }
        }

        for ( int i = 1; i <= nrows; i++ )  {
            for ( int j = 1; j <= ncols; j++ ) {
                BlockadeBoardPosition pos = (BlockadeBoardPosition)board.getPosition( i, j );
                pieceRenderer_.render(g2, pos,  cellSize_, getMargin(), board);
            }
        }

        // if there is a wall being dragged, draw it
        if ( draggedWall_ != null ) {
            // first remember the walls currently there (if any) so they can be restored.
            Set<BlockadeBoardPosition> hsPositions = draggedWall_.getPositions();
            Iterator it = hsPositions.iterator();
            while (it.hasNext())  {
                BlockadeBoardPosition pos = (BlockadeBoardPosition)it.next();
                boolean vertical = draggedWall_.isVertical();
                BlockadeWall cWall = vertical? pos.getEastWall() : pos.getSouthWall() ;

                // temporarily set the dragged wall long enough to render it.
                if (vertical)
                    pos.setEastWall(draggedWall_);
                else
                    pos.setSouthWall(draggedWall_);

                BlockadePieceRenderer.renderWallAtPosition1( g2,  pos, cellSize, getMargin() );

                // restore the actual wall
                if (vertical)
                    pos.setEastWall(cWall);
                else
                    pos.setSouthWall(cWall);
            }
        }
    }


}

