package com.becker.game.twoplayer.common.ui;

import com.becker.game.common.ui.GameBoardRenderer;

import java.awt.*;

import com.becker.game.common.*;
import com.becker.game.twoplayer.common.TwoPlayerMove;

/**
 * Singleton class that takes a game board and renders it for the GameBoardViewer.
 * Having the board renderer separate from the viewer helps to separate out the rendering logic
 * from other features of the GameBoardViewer.
 *
 * @author Barry Becker
 */
public abstract class TwoPlayerBoardRenderer extends GameBoardRenderer
{

    /**
     * private constructor because this class is a singleton.
     * Use getRenderer instead
     */
    protected TwoPlayerBoardRenderer()
    {}

    protected void drawLastMoveMarker(Graphics2D g2, Move lastMove)
    {
        // this draws a small indicator on the last move to show where it was played
        TwoPlayerMove last = (TwoPlayerMove) lastMove;
        if ( last != null ) {
            g2.setColor( LAST_MOVE_INDICATOR_COLOR );
            g2.setStroke(LAST_MOVE_INDICATOR_STROKE);
            int cellSize = getCellSize();
            int xpos = getMargin() + (last.getToCol() - 1) * cellSize + 1;
            int ypos = getMargin() + (last.getToRow() - 1) * cellSize + 1;
            g2.drawOval( xpos, ypos, cellSize - 2, cellSize - 2 );
        }
    }

}

