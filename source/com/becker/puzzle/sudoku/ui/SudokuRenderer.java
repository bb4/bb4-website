package com.becker.puzzle.sudoku.ui;

import com.becker.puzzle.common.PuzzleRenderer;
import com.becker.puzzle.sudoku.model.board.Board;
import com.becker.puzzle.sudoku.model.board.Candidates;
import com.becker.puzzle.sudoku.model.ValueConverter;
import com.becker.puzzle.sudoku.model.board.Cell;

import java.awt.*;
import java.util.Iterator;

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

    private boolean showCandidates = false;
    private int pieceSize;

    /**
     * Constructor
     */
    public SudokuRenderer() {}

    public void setShowCandidates(boolean show) {
         showCandidates = show;
    }

    /**
     * This renders the current state of the Board to the screen.
     */
    @Override
    public void render(Graphics g, Board board, String status, int width, int height )  {

        int minEdge = (Math.min(width, height) - 20 - MARGIN);
        pieceSize = minEdge / board.getEdgeLength();
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

                drawCell(g, c, xpos, ypos);
            }
        }
        drawCellBoundaryGrid(g, len);
    }


    /**
     * Draw a cell at the specified location.
     */
    private void drawCell(Graphics g, Cell cell, int xpos, int ypos) {

        int s = getScale(pieceSize);

        int jittered_xpos = xpos + (int)(Math.random() * 3 - 1);
        int jittered_ypos = ypos + (int)(Math.random() * 3 - 1);
        Font font = new Font("Sans Serif", Font.PLAIN, pieceSize >> 1);

        g.setFont(font);
        g.setColor( cell.isOriginal() ? CELL_ORIG_BACKGROUND_COLOR : CELL_BACKGROUND_COLOR );
        g.fillRect( xpos + 1, ypos + 1, pieceSize - 3, pieceSize - 2 );

        g.setColor( cell.isOriginal() ? CELL_ORIG_TEXT_COLOR : CELL_TEXT_COLOR );
        if (cell.getValue() > 0) {
            g.drawString(ValueConverter.getSymbol(cell.getValue()),
                    jittered_xpos + (int)(0.8 * s), (int)(jittered_ypos + s * 1.7) );
        }

        // draw the first 9 numbers in the candidate list, if there are any.
        if (showCandidates) {
            drawCandidates(g, cell.getCandidates(), xpos, ypos);
        }
    }

    private void drawCandidates(Graphics g,Candidates candidates, int xpos, int ypos) {

        if (candidates != null ) {
            g.setColor(CANDIDATE_TEXT_COLOR);
            Font candidateFont = new Font("Sans Serif", Font.PLAIN, (pieceSize >> 2) - 2);
            g.setFont(candidateFont);

            drawHints(g, candidates, xpos, ypos, getScale(pieceSize));
        }
    }

    private int getScale(int pieceSize) {
       return (int) (pieceSize * 0.4);
    }
    private void drawHints(Graphics g, Candidates candidates, int x, int y, int scale) {
        int xOffsetLow =  (int) (0.3 * scale);
        int xOffsetMed =  (int) (1.1 * scale);
        int xOffsetHi =  (int) (1.9 * scale);
        int yOffsetLow =  (int) (0.7 * scale);
        int yOffsetMed =  (int) (1.5 * scale);
        int yOffsetHi =  (int) (2.3 * scale);
        int[][] offsets = {
                {xOffsetLow, yOffsetLow},
                {xOffsetMed, yOffsetLow},
                {xOffsetHi, yOffsetLow},
                {xOffsetLow, yOffsetMed},
                {xOffsetMed, yOffsetMed},
                {xOffsetHi, yOffsetMed},
                {xOffsetLow, yOffsetHi},
                {xOffsetMed, yOffsetHi},
                {xOffsetHi, yOffsetHi}
        };

        int ct = 0;
        Iterator<Integer> cit = candidates.iterator();
        while (cit.hasNext() && ct < 9)  {
            g.drawString(ValueConverter.getSymbol(cit.next()), x + offsets[ct][0], y + offsets[ct][1]);
            ct++;
        }
    }

    /**
     * draw the borders around each piece.
     */
    private void drawCellBoundaryGrid(Graphics g, int edgeLen) {

        Graphics2D g2 = (Graphics2D) g;
        int xpos, ypos;

        int rightEdgePos = MARGIN + pieceSize * edgeLen;
        int bottomEdgePos = MARGIN + pieceSize * edgeLen;
        int bigCellLen = (int) Math.sqrt(edgeLen);

        // draw the hatches which delineate the cells
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