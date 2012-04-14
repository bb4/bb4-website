/** Copyright by Barry G. Becker, 2000-2011. Licensed under MIT License: http://www.opensource.org/licenses/MIT  */
package com.becker.puzzle.hiq;

import com.becker.puzzle.common.PuzzleRenderer;

import java.awt.*;

/**
 * Singleton class that takes a PieceList and renders it for the PegBoardViewer.
 * Having the renderer separate from the viewer helps to separate out the rendering logic
 * from other features of the PegBoardViewer.
 *
 * @author Barry Becker
 */
public class PegBoardRenderer implements PuzzleRenderer<PegBoard> {

    public static final int INC = 10;

    private static final int LEFT_MARGIN = 50;
    private static final int TOP_MARGIN = 55;

    private static final Color FILLED_HOLE_COLOR = new Color(120, 0, 190);
    private static final Color EMPTY_HOLE_COLOR = new Color(55, 55, 65, 150);
    private static final int FILLED_HOLE_RAD = 16;
    private static final int EMPTY_HOLE_RAD = 9;

    /**
     * private constructor because this class is a singleton.
     * Use getPieceRenderer instead.
     */
    public PegBoardRenderer() {}

    /**
     * This renders the current state of the Board to the screen.
     */
    public void render( Graphics g, PegBoard board, int width, int height ) {

        int i, xpos, ypos;
        int size = PegBoard.SIZE;
        int rightEdgePos = LEFT_MARGIN + 3 * INC * size;
        int bottomEdgePos = TOP_MARGIN + 3 * INC * size;
        g.setColor( Color.black );

        // draw the hatches which delineate the cells
        g.setColor( Color.darkGray );
        for ( i = 0; i <= size; i++ )  //   -----
        {
            ypos = TOP_MARGIN + i * 3 * INC;
            g.drawLine( LEFT_MARGIN, ypos, rightEdgePos, ypos );
        }
        for ( i = 0; i <= size; i++ )  //   ||||
        {
            xpos = LEFT_MARGIN + i * 3 * INC;
            g.drawLine( xpos, TOP_MARGIN, xpos, bottomEdgePos );
        }

        // now draw the pieces that we have so far
        for (byte row = 0; row < size; row++) {
            for (byte col = 0; col < size; col++) {

                if (PegBoard.isValidPosition(row, col)) {

                    xpos = LEFT_MARGIN + col * 3 * INC + INC / 3;
                    ypos = TOP_MARGIN + row * 3 * INC + 2 * INC / 3;

                    boolean empty = board.isEmpty(row, col);
                    Color c = empty ?  EMPTY_HOLE_COLOR : FILLED_HOLE_COLOR;
                    int r = empty ? EMPTY_HOLE_RAD : FILLED_HOLE_RAD;
                    g.setColor(c);
                    int rr = r / 2;

                    g.fillOval(xpos + INC - rr, ypos + INC - rr, r, r);
                }
            }
        }
    }
}


