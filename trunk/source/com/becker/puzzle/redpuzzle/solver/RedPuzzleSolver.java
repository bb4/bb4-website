/** Copyright by Barry G. Becker, 2000-2011. Licensed under MIT License: http://www.opensource.org/licenses/MIT  */
package com.becker.puzzle.redpuzzle.solver;

import com.becker.puzzle.common.Refreshable;
import com.becker.puzzle.common.solver.PuzzleSolver;
import com.becker.puzzle.redpuzzle.model.Piece;
import com.becker.puzzle.redpuzzle.model.PieceList;

import java.util.List;


/**
 * Abstract base class for puzzle solver strategies (see strategy pattern).
 * Subclasses do the hard work of actually solving the puzzle.
 * Controller in the model-view-controller pattern.
 *
 * @author Barry Becker
 */
public abstract class RedPuzzleSolver<P, K>
                implements PuzzleSolver<PieceList, Piece> {

    /** the unsorted pieces that we draw from and place in the solvedPieces list. */
    protected PieceList pieces_;

    /** the pieces we have correctly fitted so far. */
    protected PieceList solution_;
    
    /** some measure of the number of iterations the solver needs to solve the puzzle. */
    protected int numTries_ = 0;
    
    protected Refreshable<PieceList, Piece> puzzlePanel_;

    /**
     * Constructor
     * @param pieces the unsorted pieces.
     */
    public RedPuzzleSolver(PieceList pieces) {
        pieces_ = pieces;
        solution_ = new PieceList();
    }

    /**
     * Derived classes must provide the implementation for this abstract method.
     * @return true if a solution is found.
     */
    public abstract List<Piece> solve();

    /**
     * @return the list of successfully placed pieces so far.
     */
    public PieceList getSolvedPieces() {
        return solution_;
    }

    /**
     * @return  the number of different ways we have tried to fit pieces together so far.
     */
    public int getNumIterations() {
        return numTries_;
    }

}