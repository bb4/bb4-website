package com.becker.puzzle.redpuzzle;

/**
 * Works really well in spite of being brute force.
 * @see GeneticSearchSolver
 * for a potentially better alternative.
 *
 * @author Barry Becker Date: Aug 6, 2006
 */
public class BruteForceSolver extends PuzzleSolver {


    public BruteForceSolver(PieceList pieces) {
        super(pieces);
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
     * This implements the main recursive algorithm for solving the red puzzle.
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
                    click(puzzlePanel);

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
}
