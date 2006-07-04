package com.becker.puzzle.sudoku;

import javax.swing.*;
import java.awt.*;
import java.util.List;

/**
 * Draws the current best solution to the puzzle in a panel.
 * The view in the model-view-controller pattern.
 *
 *  @author Barry Becker
 */
final class PuzzlePanel extends JPanel
{
    private static final int PIECE_SIZE = 40;

    private static final int MARGIN = 50;

    private static final Color CELL_ORIG_TEXT_COLOR = Color.BLACK;
    private static final Color CELL_TEXT_COLOR = Color.BLUE;

    private static final Color CELL_ORIG_BACKGROUND_COLOR = new Color(240, 240, 250);
    private static final Color CELL_BACKGROUND_COLOR = new Color(245, 245, 255);
    private static final Color GRID_COLOR = new Color(10, 0, 100);
    private static final Color TEXT_COLOR = new Color(0, 10, 10);
    private static final Color BACKGROUND_COLOR = new Color(220, 220, 240);
    private static final Font FONT = new Font("Sans Serif", Font.PLAIN, 24);
    private static final Font CANDIDATE_FONT = new Font("Sans Serif", Font.PLAIN, 12);
    private static final Color CANDIDATE_TEXT_COLOR = new Color(160, 160, 210);


    private static final Stroke CELL_STROKE = new BasicStroke(0.5f);
    private static final Stroke BIG_CELL_STROKE = new BasicStroke(3.0f);

    // the contorller that does the work of finding the solution.
    private PuzzleSolver solver_;
    private Board board_;


    /**
     * Constructor. Pass in data for initial Sudoku problem.
     */
    PuzzlePanel(int[][] initialData) {
        this(new Board(initialData));
    }

    /**
     * Constructor.
     */
    PuzzlePanel(Board b) {
        // this does all the heavy work of solving it.
        board_ = b;
        solver_ = new PuzzleSolver();

        int edgeLen = board_.getBaseSize() * board_.getBaseSize();
        this.setPreferredSize( new Dimension( edgeLen * PIECE_SIZE, edgeLen * PIECE_SIZE ));
    }

    public void startSolving() {
        // better would be to run in a different thread?
        boolean solved = solver_.solvePuzzle(this);

        if ( solved )
            System.out.println( "The final solution is shown. the number of iterations was:" + solver_.getNumIterations() );
        else
            System.out.println( "This puzzle is not solvable!" ); // guaranteed not to happen
    }

    public Board getBoard() {
        return board_;
    }

    /**
     *  This renders the current state of the PuzzlePanel to the screen.
     *  This method is part of the component interface.
     */
    protected void paintComponent( Graphics g ) {

        super.paintComponents( g );

        // erase what's there and redraw.
        g.setColor( BACKGROUND_COLOR );
        g.fillRect( 0, 0, this.getWidth(), this.getHeight() );

        g.setColor( TEXT_COLOR );
        g.drawString( "Number of tries: " + solver_.getNumIterations(),
                MARGIN, MARGIN - 24 );

        int len =  board_.getEdgeLength();



        int xpos, ypos;

        for ( int i = 0; i < len; i++ ) {
            for ( int j = 0; j < len; j++ ) {
                Cell c = board_.getCell(i, j);

                xpos = MARGIN + j * PIECE_SIZE;
                ypos = MARGIN + i * PIECE_SIZE;


                drawCell(g, c, xpos, ypos);
            }
        }
        drawCellBoundaryGrid(g, len);
    }

    /**
     * Draw a cell at the specified location.
     */
    private static void drawCell(Graphics g, Cell cell, int xpos, int ypos) {

        int s = (int) (PIECE_SIZE * 0.4);

        g.setFont(FONT);
        g.setColor( cell.isOriginal() ? CELL_ORIG_BACKGROUND_COLOR : CELL_BACKGROUND_COLOR );
        g.fillRect( xpos + 1, ypos + 1, PIECE_SIZE - 3, PIECE_SIZE - 2 );

        g.setColor( cell.isOriginal() ? CELL_ORIG_TEXT_COLOR : CELL_TEXT_COLOR );
        if (cell.getValue() > 0) {
            g.drawString( Integer.toString(cell.getValue()), xpos + (int)(0.8 *s), (int)(ypos + s * 1.7) );
        }

        // draw the first 4 numbers in the candidate list, if there are any.
        List candidates = cell.getCandidates();
        if (candidates != null) {
            g.setColor(CANDIDATE_TEXT_COLOR);
            g.setFont(CANDIDATE_FONT);
            int size = candidates.size();
            int xOffsetLow =  (int) (0.4 * s);
            int xOffsetHi =  (int) (1.5 * s);
            int yOffsetLow =  (int) (0.9 * s);
            int yOffsetHi =  (int) (2.1 * s);

            if (size > 0) {
                g.drawString( candidates.get(0).toString(), xpos + xOffsetLow, ypos + yOffsetLow );
            }
            if (size > 1) {
                g.drawString( candidates.get(1).toString(), xpos + xOffsetHi, ypos + yOffsetLow);
            }
            if (size > 2) {
                g.drawString( candidates.get(2).toString(), xpos + xOffsetLow, ypos + yOffsetHi);
            }
            if (size > 3) {
                g.drawString( candidates.get(3).toString(), xpos + xOffsetHi, ypos + yOffsetHi);
            }
        }
    }


    /**
     * draw the borders around each piece.
     */
    private static void drawCellBoundaryGrid(Graphics g, int edgeLen) {

        Graphics2D g2 = (Graphics2D) g;
        int xpos, ypos;

        int rightEdgePos = MARGIN + PIECE_SIZE * edgeLen;
        int bottomEdgePos = MARGIN + PIECE_SIZE * edgeLen;
        int bigCellLen = (int) Math.sqrt(edgeLen);

        // draw the hatches which deliniate the cells
        g.setColor( GRID_COLOR );
        for ( int i = 0; i <= edgeLen; i++ )  //   -----
        {
            ypos = MARGIN + i * PIECE_SIZE;
            g2.setStroke((i % bigCellLen == 0) ? BIG_CELL_STROKE : CELL_STROKE);
            g2.drawLine( MARGIN, ypos, rightEdgePos, ypos );
        }
        for ( int i = 0; i <= edgeLen; i++ )  //   ||||
        {
            xpos = MARGIN + i * PIECE_SIZE;
            g2.setStroke((i % bigCellLen == 0) ? BIG_CELL_STROKE : CELL_STROKE);
            g2.drawLine( xpos, MARGIN, xpos, bottomEdgePos );
        }
    }


}

