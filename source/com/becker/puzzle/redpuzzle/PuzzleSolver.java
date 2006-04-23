package com.becker.puzzle.redpuzzle;


/**
 * This does the hard work of actually solving the puzzle.
 * Controller in the model-view-controller pattern.
 *
 * @author Barry Becker
 */
public class PuzzleSolver {

    // I don't have the data for other than a 3*3 puzzle
    public static final int NROWS = 3;
    public static final int NCOLS = 3;

    // the unsorted pieces that we draw from and place in the solvedPieces list.
    PieceList pieces_;

    // the pieces we have correctly fitted so far.
    PieceList solution_;

    // count the number of times we have tried to place a piece.
    private int numIterations_ = 0;


    /**
     * Constructor
     * @param pieces the unsorted pieces.
     */
    public PuzzleSolver(PieceList pieces) {
         pieces_ = pieces;
         solution_ = new PieceList();
    }

    /**
     * @param puzzlePanel will show the pieces as we arrange them.
     * @return true if a solution is found.
     */
    public boolean solvePuzzle( PuzzlePanel puzzlePanel)  {
        return solvePuzzle(puzzlePanel, pieces_);
    }


    /**
     * Solves the puzzle.
     * This implements the main algorithm for solving the red puzzle.
     * @param puzzlePanel
     * @param pieces
     * @return
     */
    protected  boolean solvePuzzle( PuzzlePanel puzzlePanel, PieceList pieces ) {
        boolean solved = false;

        // base case of the recursion. If reached, the puzzle has been solved.
        if (pieces.size() == 0)
            return true;

        int k = 0;
        while (!solved && k < pieces.size() ) {
            Piece p = pieces.get(k);
            int r = 0;
            // try the 4 rotations
            while (!solved && r < 4) {
                numIterations_++;
                if ( fits(p) ) {
                    solution_.add( p );
                    pieces.remove( p );
                    refresh(puzzlePanel);
                    // call solvePuzzle with a simpler case (one less piece to solve)
                    solved = solvePuzzle( puzzlePanel, pieces );
                }
                if (!solved)
                    p.rotate();
                r++;
            }
            k++;
        }

        if (!solved && solution_.size() > 0) {
            // back track.
            Piece p = solution_.removeLast();
            //p.resetOrientation(); // restore to unrotated state
            // put it back where it came from on the original piece list.
            pieces.add(p);
        }

        refresh(puzzlePanel);
        // if we get here and solved is not true, we did not find a puzzlePanel
        return solved;
    }


    /**
     * Try the piece. We rotate it until it fits.
     * if it does not fit after all rotations have been tried, we return false
     */
    public boolean fits( Piece p ) {

        // it needs to match the piece to the left and above (if present)
        boolean fits = true;
        int numSolved = solution_.size();
        int row = numSolved / NROWS;
        int col = numSolved % NCOLS;
        if ( col > 0 ) {
            // if other than a left edge piece, then we need to match to the left side nub.
            Piece leftPiece = solution_.getLast();
            if (!leftPiece.getRightNub().fitsWith(p.getLeftNub()))
                fits = false;
        }
        if ( row > 0 ) {
            // then we need to match with the top one
            Piece topPiece = solution_.get( numSolved - NCOLS );
            if (!topPiece.getBottomNub().fitsWith(p.getTopNub()) )
                fits = false;
        }

        return fits;
    }

    /**
     * Try the piece. We rotate it until it fits.
     * if it does not fit after all rotations have been tried, we return false
     *
    public boolean fits( Piece p ) {
        // assume fits until proven otherwise
        boolean fits = true;

        // it needs to match the piece to the left and above (if present)
        do {
            if ( !fits )
                p.rotate();
            fits = true;

            int numSolved = solution_.size();
            int row = numSolved / NROWS;
            int col = numSolved % NCOLS;
            if ( col > 0 ) {
                // if other than a left edge piece, then we need to match to the left side nub.
                Piece leftPiece = solution_.getLast();
                if (!leftPiece.getRightNub().fitsWith(p.getLeftNub()))
                    fits = false;
            }
            if ( row > 0 ) {
                // then we need to match with the top one
                Piece topPiece = solution_.get( numSolved - NCOLS );
                if (!topPiece.getBottomNub().fitsWith(p.getTopNub()) )
                    fits = false;
            }
        } while ( !fits && !p.isFullyRotated());

        // its been fully rotate, so return to original orientation
        if (!fits) {
            p.rotate();
        }

        return fits;
    }  */

    /**
     * the number of successfully placed pieces so far.
     * @return
     */
    public PieceList getSolvedPieces() {
        return solution_;
    }

    /**
     * @return  the number of pieces we have tried to fit so far.
     */
    public int getNumIterations() {
        return numIterations_;
    }


    private void refresh(PuzzlePanel puzzlePanel) {
        puzzlePanel.repaint();
        try {
           Thread.sleep(20);
        } catch (InterruptedException e) {}
    }
}
