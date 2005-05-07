package com.becker.puzzle.hiq;


import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;

/**
 *  Describes the current best solution to the puzzle
 *
 */
final class Solution extends JPanel
{

    private int numSolved_ = 0;

    private static final int INC = 20;

    private static final int LEFT_MARGIN = 40;
    private static final int TOP_MARGIN = 50;

    // I don't have the data for other than a 3*3 puzzle
    private static final int NROWS = 3;
    private static final int NCOLS = 3;

    // these are the pieces that fit together so far
    // when there are 9 of them we are done and the puzzle is solved
    private final ArrayList pieces_ = new ArrayList();

    // Constructor.
    public Solution()
    {
        this.setPreferredSize( new Dimension( 14 * INC, 14 * INC ) );
    }

    // try the piece
    // we rotate it until it fits.
    // if it does not fit after all rotations have been tried we return false
    public final boolean fits( Peg p )
    {
        // assume fits until proven otherwise
        boolean fits = true;

        // it needs to match the piece to the left and above (if present)
        do {
            if ( !fits )
                p.rotate();
            fits = true;

            int row = numSolved_ / NROWS;
            int col = numSolved_ % NCOLS;
            if ( col > 0 ) {
                // then we need to match to the left
                Peg leftPiece = (Peg) pieces_.get( numSolved_ - 1 );
                if ( !(leftPiece.rightSuit() == p.leftSuit()
                        && leftPiece.rightOut() != p.leftOut())
                )
                    fits = false;
            }
            if ( row > 0 ) {
                // then we need to match with the top one
                Peg topPiece = (Peg) pieces_.get( numSolved_ - NCOLS );
                if ( !(topPiece.bottomSuit() == p.topSuit()
                        && topPiece.bottomOut() != p.topOut())
                )
                    fits = false;
            }
        } while ( !fits && p.getRotation() < 4 );

        // its been fully rotate, so return to original orientation
        if ( p.getRotation() == 4 ) {
            p.reset();
        }

        return fits;
    }

    public final void push( Peg p )
    {
        if ( !fits( p ) )
            System.out.println( "Error: the piece must fit for it to be pushed" );
        pieces_.add( p );
        numSolved_++;
    }

    public final Peg pop()
    {
        Peg p = (Peg) pieces_.get( numSolved_ - 1 );
        pieces_.remove( p );
        numSolved_--;
        return p;
    }

    // This renders the current state of the Solution to the screen
    protected final void paintComponent( Graphics g )
    {
        int i, xpos, ypos;

        super.paintComponents( g );
        // erase what's there and redraw.

        g.clearRect( 0, 0, this.getWidth(), this.getHeight() );
        g.setColor( new Color( 235, 235, 230 ) );
        g.fillRect( 0, 0, this.getWidth(), this.getHeight() );

        int rightEdgePos = LEFT_MARGIN + 3 * INC * NCOLS;
        int bottomEdgePos = TOP_MARGIN + 3 * INC * NROWS;

        g.setColor( Color.black );
        g.drawString( "Number of tries: " + Integer.toString( HiQPuzzle.getNumIterations() ),
                LEFT_MARGIN, TOP_MARGIN - 24 );

        // draw the hatches which deliniate the cells
        g.setColor( Color.darkGray );
        for ( i = 0; i <= NROWS; i++ )  //   -----
        {
            ypos = TOP_MARGIN + i * 3 * INC;
            g.drawLine( LEFT_MARGIN, ypos, rightEdgePos, ypos );
        }
        for ( i = 0; i <= NCOLS; i++ )  //   ||||
        {
            xpos = LEFT_MARGIN + i * 3 * INC;
            g.drawLine( xpos, TOP_MARGIN, xpos, bottomEdgePos );
        }

        // now draw the pieces that we have so far
        char[] symb = new char[1];
        //System.out.println("numSolved = "+numSolved_);
        for ( i = 0; i < numSolved_; i++ ) {
            Peg p = (Peg) pieces_.get( i );
            int row = i / 3;
            int col = i % 3;

            xpos = LEFT_MARGIN + col * 3 * INC + INC / 3;
            ypos = TOP_MARGIN + row * 3 * INC + 2 * INC / 3;

            g.setColor( Color.red );
            // draw the topSuit
            symb[0] = p.topSuit();
            if ( p.topOut() )
                g.drawChars( symb, 0, 1, xpos + INC, ypos - INC );
            else
                g.drawChars( symb, 0, 1, xpos + INC, ypos );

            // draw the rightSuit
            symb[0] = p.rightSuit();
            if ( p.rightOut() )
                g.drawChars( symb, 0, 1, xpos + 3 * INC, ypos + INC );
            else
                g.drawChars( symb, 0, 1, xpos + 2 * INC, ypos + INC );

            // draw the bottomSuit
            symb[0] = p.bottomSuit();
            if ( p.bottomOut() )
                g.drawChars( symb, 0, 1, xpos + INC, ypos + 3 * INC );
            else
                g.drawChars( symb, 0, 1, xpos + INC, ypos + 2 * INC );

            // draw the leftSuit
            symb[0] = p.leftSuit();
            if ( p.leftOut() )
                g.drawChars( symb, 0, 1, xpos - INC, ypos + INC );
            else
                g.drawChars( symb, 0, 1, xpos, ypos + INC );

            // draw the number in the middle
            g.setColor( Color.gray );
            Integer num = new Integer( p.getNumber() );
            g.drawString( num.toString(), xpos + INC, ypos + INC );
        }
    }
}

