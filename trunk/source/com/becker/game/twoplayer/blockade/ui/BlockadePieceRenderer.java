package com.becker.game.twoplayer.blockade.ui;

import com.becker.game.twoplayer.blockade.BlockadeBoardPosition;
import com.becker.game.common.BoardPosition;
import com.becker.game.common.GamePiece;
import com.becker.game.common.Board;
import com.becker.game.common.GameContext;
import com.becker.game.twoplayer.common.ui.TwoPlayerBoardViewer;
import com.becker.game.twoplayer.common.ui.TwoPlayerPieceRenderer;

import java.awt.*;

/**
 *  a singleton class that takes a checkers piece and renders it for the BlockadeBoardViewer.
 * @see com.becker.game.twoplayer.blockade.ui.BlockadeBoardViewer
 * @author Barry Becker
 */
class BlockadePieceRenderer extends TwoPlayerPieceRenderer
{
    private static TwoPlayerPieceRenderer renderer_ = null;

    private static final Color EAST_WALL_COLOR = new Color(160, 110, 120);
    private static final Color SOUTH_WALL_COLOR = new Color(110, 160, 120);
    private static final double WALL_WIDTH_FRAC = 0.15;

    /**
     * private constructor because this class is a singleton.
     * Use getPieceRenderer instead
     */
    protected BlockadePieceRenderer()
    {}

    public static TwoPlayerPieceRenderer getRenderer()
    {
        if (renderer_ == null)
            renderer_ = new BlockadePieceRenderer();
        return renderer_;
    }

    /**
     * this draws the actual piece.
     */
    public void render( Graphics2D g2, BoardPosition position, int cellSize, Board b)
    {
        GamePiece piece = position.getPiece();
        if (piece != null)  {
            // render the piece as normal
            super.render( g2, position, cellSize, b);
        }
        // render the south and east walls if present
        BlockadeBoardPosition bpos = (BlockadeBoardPosition)position;

        renderWallAtPosition(g2, bpos, cellSize);
    }

    /**
     * @param g2 graphic context
     * @param bpos board position
     * @param cellSize
     * @return true if at least one wall was rendered.
     */
    static boolean renderWallAtPosition( Graphics2D g2, BlockadeBoardPosition bpos, int cellSize )
    {
        int xpos = TwoPlayerBoardViewer.BOARD_MARGIN + cellSize*(bpos.getCol());
        int ypos = TwoPlayerBoardViewer.BOARD_MARGIN + cellSize*(bpos.getRow());

        int wallWidthD2 = (int)(WALL_WIDTH_FRAC * cellSize);
        int wallWidth =  (int)(2.1 * wallWidthD2);

        boolean drewWall = false;
        if (bpos.getEastWall()!=null) {
            g2.setColor(EAST_WALL_COLOR);
            g2.fill3DRect(xpos-wallWidthD2, ypos-cellSize, wallWidth, cellSize, true);
            drewWall = true;
        }
        if (bpos.getSouthWall()!=null) {
            g2.setColor(SOUTH_WALL_COLOR);
            g2.fill3DRect(xpos-cellSize, ypos-wallWidthD2, cellSize, wallWidth, true);
            drewWall = true;
        }
        return drewWall;
    }

    /**
     * @param g2 graphic context
     * @param bpos board position
     * @param cellSize
     * @return true if at least one wall was rendered.
     */
    static boolean renderWallAtPosition1( Graphics2D g2, BlockadeBoardPosition bpos, int cellSize )
    {
        int xpos = TwoPlayerBoardViewer.BOARD_MARGIN + cellSize*(bpos.getCol());
        int ypos = TwoPlayerBoardViewer.BOARD_MARGIN + cellSize*(bpos.getRow());
        GameContext.log(2, "wall xpos="+xpos+" ypos="+ypos+" bpos="+bpos );

        int wallWidthD2 = (int)(WALL_WIDTH_FRAC * cellSize);
        int wallWidth =  (int)(2.1 * wallWidthD2);

        boolean drewWall = false;
        if (bpos.getEastWall() != null) {
            GameContext.log(2, "east wall" );
            g2.setColor(EAST_WALL_COLOR);
            g2.fill3DRect(xpos-wallWidthD2, ypos-cellSize, wallWidth, cellSize, true);
            drewWall = true;
        }
        if (bpos.getSouthWall() != null) {
            GameContext.log(2, "south wall" );
            g2.setColor(SOUTH_WALL_COLOR);
            g2.fill3DRect(xpos-cellSize, ypos-wallWidthD2, cellSize, wallWidth, true);
            drewWall = true;
        }
        return drewWall;
    }

}
