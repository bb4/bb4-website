package com.becker.puzzle.sudoku;

import com.becker.puzzle.common.PuzzleRenderer;
import java.awt.*;

import java.util.List;

/**
 * Renders the the sudoku puzzle onscreen.
 * @author Barry Becker
 */
public class SudokuRenderer extends PuzzleRenderer<Board> {

    private static final int MARGIN = 50;

    private static final Color CELL_ORIG_TEXT_COLOR = Color.BLACK;
    private static final Color CELL_TEXT_COLOR = Color.BLUE;

    private static final Color CELL_ORIG_BACKGROUND_COLOR = new Color(240, 240, 250);
    private static final Color CELL_BACKGROUND_COLOR = new Color(245, 245, 255);
    private static final Color GRID_COLOR = new Color(10, 0, 100);
    private static final Color TEXT_COLOR = new Color(0, 10, 10);
    private static final Color BACKGROUND_COLOR = new Color(220, 220, 240);
    private static final Color CANDIDATE_TEXT_COLOR = new Color(160, 160, 210);


    private static final Stroke CELL_STROKE = new BasicStroke(0.5f);
    private static final Stroke BIG_CELL_STROKE = new BasicStroke(3.0f);

    /**
     * Constructor
     */
    public SudokuRenderer()
    {
    }


    /**
     * This renders the current state of the Board to the screen.
     */
    public void render(Graphics g, Board board, String status, int width, int height )
    {

        int minEdge = (Math.min(width, height) - 20 - MARGIN);
        int pieceSize = minEdge / board.getEdgeLength();
        // erase what's there and redraw.
        g.setColor( BACKGROUND_COLOR );
        g.fillRect( 0, 0, width, height );

        g.setColor( TEXT_COLOR );
        g.drawString( "Number of tries: " + board.getNumIterations(),
                MARGIN, MARGIN - 24 );

        int len =  board.getEdgeLength();

        int xpos, ypos;

        for ( int i = 0; i < len; i++ ) {
            for ( int j = 0; j < len; j++ ) {
                Cell c = board.getCell(i, j);

                xpos = MARGIN + j * pieceSize;
                ypos = MARGIN + i * pieceSize;

                drawCell(g, c, xpos, ypos, pieceSize);
            }
        }
        drawCellBoundaryGrid(g, len, pieceSize);
    }


    /**
     * Draw a cell at the specified location.
     */
    private static void drawCell(Graphics g, Cell cell, int xpos, int ypos, int pieceSize) {

        int s = (int) (pieceSize * 0.4);

        Font font = new Font("Sans Serif", Font.PLAIN, pieceSize >> 1);
        Font candidateFont = new Font("Sans Serif", Font.PLAIN, pieceSize >> 2);
        g.setFont(font);
        g.setColor( cell.isOriginal() ? CELL_ORIG_BACKGROUND_COLOR : CELL_BACKGROUND_COLOR );
        g.fillRect( xpos + 1, ypos + 1, pieceSize - 3, pieceSize - 2 );

        g.setColor( cell.isOriginal() ? CELL_ORIG_TEXT_COLOR : CELL_TEXT_COLOR );
        if (cell.getValue() > 0) {
            g.drawString( Integer.toString(cell.getValue()), xpos + (int)(0.8 * s), (int)(ypos + s * 1.7) );
        }

        // draw the first 4 numbers in the candidate list, if there are any.
        List<Integer> candidates = cell.getCandidates();
        if (candidates != null) {
            g.setColor(CANDIDATE_TEXT_COLOR);
            g.setFont(candidateFont);

            int xOffsetLow =  (int) (0.4 * s);
            int xOffsetHi =  (int) (1.5 * s);
            int yOffsetLow =  (int) (0.9 * s);
            int yOffsetHi =  (int) (2.1 * s);

            drawHintNumber(g, 0, candidates, xpos + xOffsetLow, ypos + yOffsetLow);
            drawHintNumber(g, 1, candidates, xpos + xOffsetHi, ypos + yOffsetLow);
            drawHintNumber(g, 2, candidates, xpos + xOffsetLow, ypos + yOffsetHi);
            drawHintNumber(g, 3, candidates, xpos + xOffsetHi, ypos + yOffsetHi);
        }
    }

    private static void drawHintNumber(Graphics g, int cellNum, List<Integer> cands, int x, int y) {
        synchronized (cands) {
            if (cellNum < cands.size() && !cands.isEmpty()) {
                Integer v = cands.get(cellNum);
                g.drawString(v.toString(), x, y);
            }
        }
    }


    /**
     * draw the borders around each piece.
     */
    private static void drawCellBoundaryGrid(Graphics g, int edgeLen, int pieceSize) {

        Graphics2D g2 = (Graphics2D) g;
        int xpos, ypos;

        int rightEdgePos = MARGIN + pieceSize * edgeLen;
        int bottomEdgePos = MARGIN + pieceSize * edgeLen;
        int bigCellLen = (int) Math.sqrt(edgeLen);

        // draw the hatches which deliniate the cells
        g.setColor( GRID_COLOR );
        for ( int i = 0; i <= edgeLen; i++ )  //   -----
        {
            ypos = MARGIN + i * pieceSize;
            g2.setStroke((i % bigCellLen == 0) ? BIG_CELL_STROKE : CELL_STROKE);
            g2.drawLine( MARGIN, ypos, rightEdgePos, ypos );
        }
        for ( int i = 0; i <= edgeLen; i++ )  //   ||||
        {
            xpos = MARGIN + i * pieceSize;
            g2.setStroke((i % bigCellLen == 0) ? BIG_CELL_STROKE : CELL_STROKE);
            g2.drawLine( xpos, MARGIN, xpos, bottomEdgePos );
        }
    }

}