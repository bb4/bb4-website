// Copyright by Barry G. Becker, 2012. Licensed under MIT License: http://www.opensource.org/licenses/MIT
package com.becker.puzzle.tantrix;

import com.becker.puzzle.common.Refreshable;
import com.becker.puzzle.common.solver.PuzzleSolver;
import com.becker.puzzle.redpuzzle.model.Piece;
import com.becker.puzzle.redpuzzle.model.PieceList;
import com.becker.puzzle.tantrix.model.HexTile;
import com.becker.puzzle.tantrix.model.HexTileList;

import java.util.List;


/**
 * Abstract base class for puzzle solver strategies (see strategy pattern).
 * Subclasses do the hard work of actually solving the puzzle.
 * Controller in the model-view-controller pattern.
 *
 * @author Barry Becker
 */
public abstract class TantricPuzzleSolver implements PuzzleSolver<HexTileList, HexTile> {

    /** the unsorted pieces that we draw from and place in the solvedPieces list. */
    protected HexTileList pieces_;

    /** the pieces we have correctly fitted so far. */
    protected HexTileList solution_;

    /** some measure of the number of iterations the solver needs to solve the puzzle. */
    protected int numTries_ = 0;

    protected Refreshable<HexTileList, HexTile> puzzlePanel_;

    /**
     * Constructor
     * @param pieces the unsorted pieces.
     */
    public TantricPuzzleSolver(HexTileList pieces) {
        pieces_ = pieces;
        solution_ = new HexTileList();
    }

    /**
     * Derived classes must provide the implmentation fo rthis abstract method.
     * @return true if a solution is found.
     */
    public abstract List<HexTile> solve();

    /**
     * the list of successfully placed pieces so far.
     * @return
     */
    public HexTileList getSolvedPieces() {
        return solution_;
    }

    /**
     * @return  the number of different ways we have tried to fit pieces together so far.
     */
    public int getNumIterations() {
        return numTries_;
    }

}
