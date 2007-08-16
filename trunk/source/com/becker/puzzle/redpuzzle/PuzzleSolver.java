package com.becker.puzzle.redpuzzle;

import com.becker.common.*;
import com.becker.puzzle.common.Refreshable;


/**
 * Abstract base class for puzzle solver strategies (see strategy pattern).
 * Subclasses do the hard work of actually solving the puzzle.
 * Controller in the model-view-controller pattern.
 *
 * @author Barry Becker
 */
public abstract class PuzzleSolver {

    // the unsorted pieces that we draw from and place in the solvedPieces list.
    protected PieceList pieces_;

    // the pieces we have correctly fitted so far.
    protected PieceList solution_;

    // I don't have the data for other than a 3*3 or 2*2 puzzle.
    protected int dim_;
    
    // some measure of the number of iterations the solver needs to solve the puzzle.
    protected int numTries_ = 0;

    /**
     * Constructor
     * @param pieces the unsorted pieces.
     */
    public PuzzleSolver(PieceList pieces) {
        pieces_ = pieces;
        dim_ = (int) Math.sqrt(pieces.size());
        solution_ = new PieceList();
    }

    /**
     * Derived classes must provide the implmentation fo rthis abstract method.
     * @param puzzlePanel will show the pieces as we arrange them.
     * @return true if a solution is found.
     */
    public abstract boolean solvePuzzle(Refreshable puzzlePanel);


    /**
     * Try the piece.
     * @return true if it fits.
     */
    public boolean fits( Piece p ) {

        // it needs to match the piece to the left and above (if present)
        boolean fits = true;
        int numSolved = solution_.size();
        int row = numSolved / dim_;
        int col = numSolved % dim_;
        if ( col > 0 ) {
            // if other than a left edge piece, then we need to match to the left side nub.
            Piece leftPiece = solution_.getLast();
            if (!leftPiece.getRightNub().fitsWith(p.getLeftNub()))
                fits = false;
        }
        if ( row > 0 ) {
            // then we need to match with the top one
            Piece topPiece = solution_.get( numSolved - dim_ );
            if (!topPiece.getBottomNub().fitsWith(p.getTopNub()) )
                fits = false;
        }

        return fits;
    }

    /**
     * @return the number of squares on a side.
     */
    public int getDim() {
        return dim_;
    }

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
        return numTries_;
    }

}
