package com.becker.game.twoplayer.go.ui;

import com.becker.game.common.BoardPosition;
import com.becker.game.common.GameContext;
import com.becker.game.common.Board;
import com.becker.game.twoplayer.common.ui.TwoPlayerBoardViewer;
import com.becker.game.twoplayer.common.ui.TwoPlayerPieceRenderer;
import com.becker.game.twoplayer.go.GoStone;
import com.becker.game.twoplayer.go.GoBoardPosition;
import com.becker.game.twoplayer.go.GoBoard;
import com.becker.ui.GUIUtil;

import javax.swing.*;
import java.awt.*;

/**
 * Singleton class that takes a checkers piece and renders it for the ChessBoardViewer.
 * @see com.becker.game.twoplayer.chess.ui.ChessBoardViewer
 * @author Barry Becker
 */
public final class GoStoneRenderer extends TwoPlayerPieceRenderer
{
    private static TwoPlayerPieceRenderer renderer_ = null;

    // if rendering the stones we use these colors
    // the stone colors ( a specular highlight is added to the stones when rendering )
    private static final Color PLAYER1_STONE_COLOR = new Color( 90, 90, 90 );
    private static final Color PLAYER2_STONE_COLOR = new Color( 230, 230, 230 );  // off-white

    private static final Color ATARI_COLOR = new Color( 255, 210, 90, 255 );  // bright red
    private static final int ATARI_MARKER_RADIUS = 6;

    // instead of rendering we can just show image icons which look even better.
    // gets the images from resources or the filesystem depending if we are running as an applet or application respectively.
    private static final String DIR = GameContext.GAME_ROOT+"twoplayer/go/ui/images/";
    public static final ImageIcon BLACK_STONE_IMG = GUIUtil.getIcon(DIR+"goStoneBlack.png");
    public static final ImageIcon WHITE_STONE_IMG = GUIUtil.getIcon(DIR+"goStoneWhite.png");
    private static final ImageIcon BLACK_STONE_DEAD_IMG = GUIUtil.getIcon(DIR+"goStoneBlackDead.png");
    private static final ImageIcon WHITE_STONE_DEAD_IMG = GUIUtil.getIcon(DIR+"goStoneWhiteDead.png");

    /**
     * protected constructor because this class is a singleton.
     * Use getPieceRenderer instead
     */
    private GoStoneRenderer()
    {}

    public static TwoPlayerPieceRenderer getRenderer()
    {
        if (renderer_ == null)
            renderer_ = new GoStoneRenderer();
        return renderer_;
    }

    /**
     * @return the color the pieces for player1.   (black)
     */
    public final Color getPlayer1Color()
    {
        return PLAYER1_STONE_COLOR;
    }

     /**
     * @return the color the pieces for player2.   (white)
     */
    public final Color getPlayer2Color()
    {
        return PLAYER2_STONE_COLOR;
    }

    /**
     * @return  the image to show for the graphical represention of the go stone
     */
    private static Image getImage(GoStone stone)
    {
        if (stone.isDead())
            return (stone.isOwnedByPlayer1() ? BLACK_STONE_DEAD_IMG.getImage(): WHITE_STONE_DEAD_IMG.getImage());
        else
            return (stone.isOwnedByPlayer1() ? BLACK_STONE_IMG.getImage(): WHITE_STONE_IMG.getImage());
    }

    /**
     * this draws the actual piece
     * Uses the RoundGradientFill from Knudsen to put a specular highlight on the stone
     *
     * @param g2 graphics context
     * @param position of the piece to render
     */
    public final void render( Graphics2D g2, BoardPosition position, int cellSize, Board board)
    {
        GoBoardPosition stonePos = (GoBoardPosition)position;
        if (GameContext.getDebugMode() > 0)  {
            //  as a debugging aid draw the background as a function of the territorial score (-1 : 1)
            double score = ((GoBoardPosition)position).scoreContribution;
            Color pc = (score > 0? PLAYER1_STONE_COLOR : PLAYER2_STONE_COLOR);
            int op = (int)((100 * Math.abs(score)));
            if (op >255) {
                System.out.println("error score too big ="+score);
            }
            Color c = new Color(pc.getRed(), pc.getGreen(), pc.getBlue(),
                         Math.min(255, op));    // @@ should not need min
            g2.setColor(c);
            g2.fillRect(TwoPlayerBoardViewer.BOARD_MARGIN + cellSize*(position.getCol()-1),
                        TwoPlayerBoardViewer.BOARD_MARGIN + cellSize*(position.getRow()-1),
                         cellSize, cellSize );
        }

        GoStone stone = (GoStone)position.getPiece();
        if (stone == null)
            return; // nothing to render
        int pieceSize = getPieceSize(cellSize, stone);
        Point pos = getPosition(position, cellSize, pieceSize);
        g2.drawImage(getImage(stone), pos.x, pos.y, pieceSize, pieceSize , null);
        if (GameContext.getDebugMode() > 0 && stonePos.isInAtari((GoBoard)board)) {
            g2.setColor(ATARI_COLOR);
            g2.fillOval(pos.x, pos.y, ATARI_MARKER_RADIUS, ATARI_MARKER_RADIUS);
        }
    }

}
