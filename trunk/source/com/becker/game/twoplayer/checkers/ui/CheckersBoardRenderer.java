package com.becker.game.twoplayer.checkers.ui;

import com.becker.game.common.Board;
import com.becker.game.common.BoardPosition;
import com.becker.game.common.GameControllerInterface;
import com.becker.game.common.ui.GameBoardRenderer;
import com.becker.game.twoplayer.checkers.CheckersMove;
import com.becker.game.twoplayer.common.ui.TwoPlayerBoardRenderer;

import java.awt.*;

/**
 * Singleton class that takes a game board and renders it for the GameBoardViewer.
 * Having the board renderer separate from the viewer helps to separate out the rendering logic
 * from other features of the GameBoardViewer.
 *
 * @author Barry Becker
 */
public class CheckersBoardRenderer extends TwoPlayerBoardRenderer
{
    private  static GameBoardRenderer renderer_;

    // colors of the squares on the chess board.
    // make them transparent so the background color shows through.
    protected static final Color BLACK_SQUARE_COLOR = new Color(2, 2, 2, 80);
    protected static final Color RED_SQUARE_COLOR = new Color(250, 0, 0, 80);

    /**
     * private constructor because this class is a singleton.
     * Use getRenderer instead
     */
    protected CheckersBoardRenderer()
    {
        pieceRenderer_ = CheckersPieceRenderer.getRenderer();
    }

    public static GameBoardRenderer getRenderer()
    {
        if (renderer_ == null)
            renderer_ = new CheckersBoardRenderer();
        return renderer_;
    }


    @Override
    protected int getPreferredCellSize()
    {
        return 34;
    }

    @Override
    protected void drawBackground( Graphics g, Board b, int startPos, int rightEdgePos, int bottomEdgePos,
                                   int panelWidth, int panelHeight)
    {
        super.drawBackground(g, b, startPos, rightEdgePos, bottomEdgePos, panelWidth, panelHeight);

        int nrows = b.getNumRows();
        int ncols = b.getNumCols();

        for (int i=0; i<nrows; i++) {
            for (int j=0; j<ncols; j++)  {
                g.setColor(((i+j)%2 == 0)? BLACK_SQUARE_COLOR : RED_SQUARE_COLOR);
                int ioff = getMargin() + cellSize_ * i;
                int joff = getMargin() + cellSize_ * j;
                g.fillRect( ioff, joff, cellSize_, cellSize_ );
            }
        }
    }


    /**
     * animate the last move so the player does not lose orientation.
     * @@ probably does not work.
     */
    @Override
    protected void drawLastMoveMarker(Graphics2D g2, GameControllerInterface controller)
    {
        Board board = controller.getBoard();
        CheckersMove m = (CheckersMove)board.getLastMove();
        // if we have captures, then we want to show each one
        if (m!=null && m.captureList != null) {
            controller.undoLastMove();
            BoardPosition origPos = board.getPosition(m.getFromRow(), m.getFromCol());
            draggedShowPiece_ = origPos.copy();
            origPos.setPiece(null);
            for (BoardPosition capPos : m.captureList) {
                int rOrig = draggedShowPiece_.getRow();
                int cOrig = draggedShowPiece_.getCol();
                int rdir = capPos.getRow() - rOrig;
                int cdir = capPos.getCol() - cOrig;
                draggedShowPiece_.setRow(rOrig + 2 * rdir);
                draggedShowPiece_.setCol(cOrig + 2 * cdir);
                board.getPosition(capPos.getLocation()).setPiece(null);
            }
            draggedShowPiece_ = null;
            controller.makeMove(m);
        }
    }


    /**
     * draw a grid of some sort if there is one.
     * none by default for poker.
     */
    @Override
    protected void drawGrid(Graphics2D g2, int startPos, int rightEdgePos, int bottomEdgePos, int start,
                            int nrows1, int ncols1, int gridOffset) {}

}

