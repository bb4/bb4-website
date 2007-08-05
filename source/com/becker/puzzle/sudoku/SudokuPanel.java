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
final class SudokuPanel extends JPanel
{
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

    // the contorller that does the work of finding the solution.

    private Board board_;


    /**
     * Constructor. Pass in data for initial Sudoku problem.
     */
    SudokuPanel(int[][] initialData) {
        this(new Board(initialData));
    }

    /**
     * Constructor.
     */
    SudokuPanel(Board b) {
        // this does all the heavy work of solving it.
        board_ = b;

        //int edgeLen = board_.getBaseSize() * board_.getBaseSize();
        //this.setPreferredSize( new Dimension( edgeLen * PIECE_SIZE, edgeLen * PIECE_SIZE ));
    }

    /**
     * reset to new puzzle with specified initial data.
     * @param initialData
     */
    public void reset(int[][] initialData) {
        board_ = new Board(initialData);
        repaint();
    }

    public void setBoard(Board b) {
        board_ = b;
    }

    public void startSolving() {
        // better would be to run in a different thread?
        SudokuSolver solver = new SudokuSolver();
        boolean solved = solver.solvePuzzle(this);

        if ( solved )
            System.out.println( "The final solution is shown. the number of iterations was:" + board_.getNumIterations() );
        else
            System.out.println( "This puzzle is not solvable!" ); // guaranteed not to happen
    }


    public void generateNewPuzzle() {
        System.out.println("generating new puzzle.");
        SudokuGenerator generator = new SudokuGenerator(board_.getBaseSize());
        board_ = generator.generatePuzzleBoard(this);
        repaint();
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

        int minEdge = (int) (Math.min(getSize().getHeight(), getSize().getWidth()) - 20 - MARGIN);
        int pieceSize = minEdge / board_.getEdgeLength();
        // erase what's there and redraw.
        g.setColor( BACKGROUND_COLOR );
        g.fillRect( 0, 0, this.getWidth(), this.getHeight() );

        g.setColor( TEXT_COLOR );
        g.drawString( "Number of tries: " + board_.getNumIterations(),
                MARGIN, MARGIN - 24 );

        int len =  board_.getEdgeLength();

        int xpos, ypos;

        for ( int i = 0; i < len; i++ ) {
            for ( int j = 0; j < len; j++ ) {
                Cell c = board_.getCell(i, j);

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
    private static synchronized void drawCell(Graphics g, Cell cell, int xpos, int ypos, int pieceSize) {

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
        List candidates = cell.getCandidates();
        if (candidates != null) {
            g.setColor(CANDIDATE_TEXT_COLOR);
            g.setFont(candidateFont);

            int xOffsetLow =  (int) (0.4 * s);
            int xOffsetHi =  (int) (1.5 * s);
            int yOffsetLow =  (int) (0.9 * s);
            int yOffsetHi =  (int) (2.1 * s);

            int size = candidates.size();
            drawHintNumber(g, 0, size, candidates, xpos + xOffsetLow, ypos + yOffsetLow);
            drawHintNumber(g, 1, size, candidates, xpos + xOffsetHi, ypos + yOffsetLow);
            drawHintNumber(g, 2, size, candidates, xpos + xOffsetLow, ypos + yOffsetHi);
            drawHintNumber(g, 3, size, candidates, xpos + xOffsetHi, ypos + yOffsetHi);
        }
    }

    private static void drawHintNumber(Graphics g, int cellNum, int size, List cands,
                                       int x, int y) {
        if (cellNum < cands.size() && !cands.isEmpty()) {
            g.drawString(cands.get(cellNum).toString(), x, y);
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

