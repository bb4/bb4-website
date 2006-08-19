package com.becker.puzzle.redpuzzle;

import com.becker.common.*;


/**
 * Abstract base class for puzzle solver strategies (see strategy pattern).
 * Subclasses do the hard work of actually solving the puzzle.
 * Controller in the model-view-controller pattern.
 *
 * @author Barry Becker
 */
public abstract class PuzzleSolver {

    // I don't have the data for other than a 3*3 or 2*2 puzzle.
    protected int dim_ = 3;

    // the unsorted pieces that we draw from and place in the solvedPieces list.
    protected PieceList pieces_;

    // the pieces we have correctly fitted so far.
    protected PieceList solution_;

    // some measure of the number of iterations the solver needs to solve the puzzle.
    protected int numIterations_ = 0;

    public static final int MAX_ANIM_SPEED = 100;
    // slows down the animation.
    private int animationSpeed_ = MAX_ANIM_SPEED;


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
    public abstract boolean solvePuzzle( PuzzlePanel puzzlePanel);


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
     * @param speed higher the faster up to MAX_ANIM_SPEED.
     */
    public void setAnimationSpeed(int speed) {
        assert (speed > 0 && speed <= MAX_ANIM_SPEED);
        animationSpeed_ = speed;
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
        return numIterations_;
    }

    protected void click(PuzzlePanel puzzlePanel) {
        if (puzzlePanel != null)  {
             puzzlePanel.clicked();
        }
    }


    protected void quickRefresh(PuzzlePanel puzzlePanel, Piece p) {

        if ((puzzlePanel == null) && (animationSpeed_ < MAX_ANIM_SPEED-1)) {
            solution_.add( p );
            puzzlePanel.repaint();
            Util.sleep(9*MAX_ANIM_SPEED / animationSpeed_); // give it a chance to repaint.
            solution_.remove( p );
        }
    }

    protected void refresh(PuzzlePanel puzzlePanel) {
        if (puzzlePanel == null)
            return;
        puzzlePanel.repaint();
        if (animationSpeed_ < MAX_ANIM_SPEED-1) {
            Util.sleep(10*MAX_ANIM_SPEED / animationSpeed_);
        }
        else {
            Util.sleep(20);
        }
    }
}
