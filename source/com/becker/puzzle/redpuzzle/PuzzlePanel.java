package com.becker.puzzle.redpuzzle;

import com.becker.common.*;
import com.becker.sound.*;

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
    /** size of piece in pixels. */
    private static final int PIECE_SIZE = 90;
    private int numPieces_;

    private static final int MARGIN = 50;
    private static final int ORIENT_ARROW_LEN = PIECE_SIZE >> 2;
    private static final int ARROW_HEAD_RAD = 2;

    private static final Color PIECE_TEXT_COLOR = new Color(200, 0, 0);
    private static final Color PIECE_BACKGROUND_COLOR = new Color(255, 205, 215, 200);
    private static final Color GRID_COLOR = new Color(10, 0, 100);
    private static final Color TEXT_COLOR = new Color(0, 0, 0);
    private static final Color BACKGROUND_COLOR = new Color(220, 220, 240);

    private static final Font NUB_FONT = new Font("SansSerif", Font.PLAIN, 12);
    private static final Font TEXT_FONT = new Font("SansSerif", Font.BOLD, 18);

    // the controller that does the work of finding the solution.
    private PuzzleSolver solver_;

    // play a sound effect when a piece goes into place.
    private MusicMaker musicMaker_ = new MusicMaker();

    public enum Algorithm { BRUTE_FORCE, GENETIC_SEARCH };


    /**
     * Constructor.
     */
    PuzzlePanel(int numPieces) {
        // this does all the heavy work of solving it.
        numPieces_ = numPieces;
        setAlgorithm(Algorithm.BRUTE_FORCE); // default
        this.setPreferredSize( new Dimension( 4 * PIECE_SIZE, 4 * PIECE_SIZE ) );
    }

    /**
     * There are different approaches we can take to solving the red puzzle.
     *
     * @param alg
     */
    public void setAlgorithm(Algorithm alg) {
        switch (alg) {
            case BRUTE_FORCE :
                solver_ = new BruteForceSolver( PieceList.getInitialPuzzlePieces(numPieces_));
                break;
            case GENETIC_SEARCH :
               solver_ = new GeneticSearchSolver( PieceList.getInitialPuzzlePieces(numPieces_));
                break;
        }
        solver_.setAnimationSpeed(1);
    }

    /**
     * solve using the algorithm set in setAlgorithm.
     */
    public void startSolving() {
        // better would be to run in a different thread?
        boolean solved = solver_.solvePuzzle(this);

        if ( solved )
            System.out.println( "The final solution is shown. the number of iterations was:" + solver_.getNumIterations() );
        else
            System.out.println( "This puzzle is not solvable!" ); // guaranteed not to happen
    }


    public void setAnimationSpeed(int speed) {
        solver_.setAnimationSpeed(speed);
    }

    /**
     * make a little click noise when the piece fits into place.
     */
    public void clicked() {
        musicMaker_.playNote(90, 40, 900);
    }

    /**
     *  This renders the current state of the PuzzlePanel to the screen.
     *  This method is part of the component interface.
     */
    protected void paintComponent( Graphics g ) {

        super.paintComponents( g );
        int dim = solver_.getDim();

        // erase what's there and redraw.
        g.setColor( BACKGROUND_COLOR );
        g.fillRect( 0, 0, this.getWidth(), this.getHeight() );

        g.setColor( TEXT_COLOR );
        g.drawString( "Number of tries: " + solver_.getNumIterations(),
                MARGIN, MARGIN - 24 );

        drawPieceBoundaryGrid(g, dim);

        PieceList pieces = solver_.getSolvedPieces();

        int i, xpos, ypos;


        for ( i = 0; i < pieces.size(); i++ ) {
            Piece p = pieces.get( i );
            int row = i / dim;
            int col = i % dim;

            xpos = MARGIN + col * PIECE_SIZE + PIECE_SIZE / 9;
            ypos = MARGIN + row * PIECE_SIZE + 2 * PIECE_SIZE / 9;


            drawPiece(g, p, xpos, ypos);
        }
    }

    /**
     * draw the borders around each piece.
     */
    private static void drawPieceBoundaryGrid(Graphics g, int dim) {

        int xpos, ypos;

        int rightEdgePos = MARGIN + PIECE_SIZE * dim;
        int bottomEdgePos = MARGIN + PIECE_SIZE * dim;

        // draw the hatches which deliniate the cells
        g.setColor( GRID_COLOR );
        for ( int i = 0; i <= dim; i++ )  //   -----
        {
            ypos = MARGIN + i * PIECE_SIZE;
            g.drawLine( MARGIN, ypos, rightEdgePos, ypos );
        }
        for ( int i = 0; i <= dim; i++ )  //   ||||
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
        g.setFont( NUB_FONT );

        // now draw the pieces that we have so far.
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

        drawOrientationMarker(g, p, xpos, ypos);

        // draw the number in the middle
        g.setColor( TEXT_COLOR );
        g.setFont( TEXT_FONT );
        Integer num = p.getNumber();
        g.drawString( Util.formatNumber(num), xpos + oneThirdSize, ypos + oneThirdSize );

    }

    /**
     *  draw a marker line to indicate the orientation.
     */
    private static void drawOrientationMarker(Graphics g, Piece p, int xpos, int ypos) {

        int len2 = ORIENT_ARROW_LEN >> 1;
        int x1 = 0, y1 = 0, x2 = 0, y2 = 0, cx = 0, cy = 0;
        int f = PIECE_SIZE / 7;

        switch (p.getOrientation()) {
            case TOP :
                x1 = xpos - len2 + 3*f;
                y1 = ypos + f;
                cx =x2 = xpos + len2 + 3*f;
                cy = y2 = ypos + f;
                break;
            case RIGHT :
                x1 = xpos + 4*f;
                y1 = ypos - len2 + 2*f;
                cx = x2 = xpos + 4*f;
                cy = y2 = ypos + len2 + 2*f;
                break;
            case BOTTOM :
                cx = x1 = xpos - len2 + 3*f;
                cy = y1 = ypos + 3*f;
                x2 = xpos + len2 + 3*f;
                y2 = ypos + 3*f;
                break;
            case LEFT :
                cx = x1 = xpos + 2*f;
                cy = y1 = ypos - len2 + 2*f;
                x2 = xpos + 2*f;
                y2 = ypos + len2 + 2*f;
                break;
        }
        g.drawLine(x1, y1, x2, y2);
        int ahd2 = ARROW_HEAD_RAD >> 1;
        g.drawOval(cx - ahd2, cy - ahd2, ARROW_HEAD_RAD, ARROW_HEAD_RAD);

    }

}

