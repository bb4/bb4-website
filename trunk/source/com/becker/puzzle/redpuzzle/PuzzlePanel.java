package com.becker.puzzle.redpuzzle;

import com.becker.common.*;

import javax.swing.*;
import java.awt.*;

/**
 * Draws the current best solution to the puzzle in a panel.
 * The view in the model-view-controller pattern.
 *
 *  @author Barry Becker
 */
final class PuzzlePanel extends JPanel
{
    private static final int PIECE_SIZE = 60;

    private static final int MARGIN = 50;

    private static final Color PIECE_TEXT_COLOR = Color.RED;
    private static final Color PIECE_BACKGROUND_COLOR = new Color(255, 195, 205);
    private static final Color GRID_COLOR = new Color(10, 0, 100);
    private static final Color TEXT_COLOR = new Color(0, 10, 10);
    private static final Color BACKGROUND_COLOR = new Color(220, 220, 240);

    // the contorller that does the work of finding the solution.
    private PuzzleSolver solver_;


    /**
     * Constructor.
     */
    PuzzlePanel() {
        // this does all the heavy work of solving it.
        solver_ = new PuzzleSolver( PieceList.getInitialPuzlePieces() );

        this.setPreferredSize( new Dimension( 4 * PIECE_SIZE, 4 * PIECE_SIZE ) );
    }

    public void startSolving() {
        // better would be to run in a different thread?
        boolean solved = solver_.solvePuzzle(this);

        if ( solved )
            System.out.println( "The final solution is shown. the number of iterations was:" + solver_.getNumIterations() );
        else
            System.out.println( "This puzzle is not solvable!" ); // guaranteed not to happen
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

        drawPieceBoundaryGrid(g);

        PieceList pieces = solver_.getSolvedPieces();

        int i, xpos, ypos;

        for ( i = 0; i < pieces.size(); i++ ) {
            Piece p = pieces.get( i );
            int row = i / PuzzleSolver.NROWS;
            int col = i % PuzzleSolver.NCOLS;

            xpos = MARGIN + col * PIECE_SIZE + PIECE_SIZE / 9;
            ypos = MARGIN + row * PIECE_SIZE + 2 * PIECE_SIZE / 9;


            drawPiece(g, p, xpos, ypos);
        }
    }

    /**
     * draw the borders around each piece.
     */
    private static void drawPieceBoundaryGrid(Graphics g) {

        int xpos, ypos;

        int rightEdgePos = MARGIN + PIECE_SIZE * PuzzleSolver.NCOLS;
        int bottomEdgePos = MARGIN + PIECE_SIZE * PuzzleSolver.NROWS;

        // draw the hatches which deliniate the cells
        g.setColor( GRID_COLOR );
        for ( int i = 0; i <= PuzzleSolver.NROWS; i++ )  //   -----
        {
            ypos = MARGIN + i * PIECE_SIZE;
            g.drawLine( MARGIN, ypos, rightEdgePos, ypos );
        }
        for ( int i = 0; i <= PuzzleSolver.NCOLS; i++ )  //   ||||
        {
            xpos = MARGIN + i * PIECE_SIZE;
            g.drawLine( xpos, MARGIN, xpos, bottomEdgePos );
        }
    }

    /**
     * Draw a puzzle piece at the specified location.
     */
    private static void drawPiece(Graphics g, Piece p, int xpos, int ypos) {

        g.setColor( PIECE_BACKGROUND_COLOR );
        g.fillRect( xpos - PIECE_SIZE / 9 + 2, ypos - 2 * PIECE_SIZE / 9 + 1, PIECE_SIZE - 3, PIECE_SIZE - 2 );

        g.setColor( PIECE_TEXT_COLOR );

        // now draw the pieces that we have so far
        Nub nub;
        char[] symb = new char[1];
        int oneThirdSize = PIECE_SIZE / 3;

        // draw the topSuit
        nub = p.getTopNub();
        symb[0] = nub.getSuitSymbol();
        if ( nub.isOuty() )
            g.drawChars( symb, 0, 1, xpos + oneThirdSize, ypos - oneThirdSize );
        else
            g.drawChars( symb, 0, 1, xpos + oneThirdSize, ypos );

        // draw the rightSuit
        nub = p.getRightNub();
        symb[0] = nub.getSuitSymbol();
        if ( nub.isOuty() )
            g.drawChars( symb, 0, 1, xpos + PIECE_SIZE, ypos + oneThirdSize );
        else
            g.drawChars( symb, 0, 1, xpos + 2 * oneThirdSize, ypos + oneThirdSize );

        // draw the bottomSuit
        nub = p.getBottomNub();
        symb[0] = nub.getSuitSymbol();
        if ( nub.isOuty() )
            g.drawChars( symb, 0, 1, xpos + oneThirdSize, ypos + PIECE_SIZE );
        else
            g.drawChars( symb, 0, 1, xpos + oneThirdSize, ypos + 2 * oneThirdSize );

        // draw the leftSuit
        nub = p.getLeftNub();
        symb[0] = nub.getSuitSymbol();
        if ( nub.isOuty() )
            g.drawChars( symb, 0, 1, xpos - oneThirdSize, ypos + oneThirdSize );
        else
            g.drawChars( symb, 0, 1, xpos, ypos + oneThirdSize );

        // draw the number in the middle
        g.setColor( TEXT_COLOR );
        Integer num = p.getNumber();
        g.drawString( Util.formatNumber(num), xpos + oneThirdSize, ypos + oneThirdSize );
    }

}

