package com.becker.puzzle.redpuzzle;

import com.becker.common.*;


/**
 * This does the hard work of actually solving the puzzle.
 * Controller in the model-view-controller pattern.
 * This could easily be made into a strategy patter with a base class and different
 * approaches to solving.
 *
 * @author Barry Becker
 */
public class PuzzleSolver {

    // I don't have the data for other than a 3*3 or 2*2 puzzle
    private int dim_ = 3;

    // the unsorted pieces that we draw from and place in the solvedPieces list.
    private PieceList pieces_;

    // the pieces we have correctly fitted so far.
    private PieceList solution_;

    // count the number of times we have tried to place a piece.
    private int numIterations_ = 0;

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
     * @param puzzlePanel will show the pieces as we arrange them.
     * @return true if a solution is found.
     */
    public boolean solvePuzzle( PuzzlePanel puzzlePanel)  {
        refresh(puzzlePanel);
        return solvePuzzle(puzzlePanel, pieces_, 0);
    }


    /**
     * Solves the puzzle.
     * This implements the main algorithm for solving the red puzzle.
     * @param puzzlePanel
     * @param pieces the pieces that have yet to be fitted.
     * @param i insdex of last placed piece. If we have to backtrack, we put it back where we got it.
     * @return true if successfully solved, false if no solution.
     */
    protected  boolean solvePuzzle( PuzzlePanel puzzlePanel, PieceList pieces, int i ) {
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
                quickRefresh(puzzlePanel, p);

                if ( fits(p) ) {
                    solution_.add( p );
                    pieces.remove( p );
                    if (puzzlePanel != null)
                        puzzlePanel.clicked();

                    // call solvePuzzle with a simpler case (one less piece to solve)
                    solved = solvePuzzle( puzzlePanel, pieces, k);
                }
                if (!solved) {
                    p.rotate();
                }
                r++;
            }
            k++;
        }

        if (!solved && solution_.size() > 0) {
            // backtrack.
            Piece p = solution_.removeLast();
            // put it back where we took it from,
            // so our list of unplaced pieces does not get out of order.
            pieces.add(i, p);
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


    private void quickRefresh(PuzzlePanel puzzlePanel, Piece p) {

        if ((puzzlePanel == null) && (animationSpeed_ < MAX_ANIM_SPEED-1)) {
            solution_.add( p );
            puzzlePanel.repaint();
            Util.sleep(9*MAX_ANIM_SPEED / animationSpeed_); // give it a chance to repaint.
            solution_.remove( p );
        }
    }

    private void refresh(PuzzlePanel puzzlePanel) {
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
