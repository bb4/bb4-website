// Copyright by Barry G. Becker, 2012. Licensed under MIT License: http://www.opensource.org/licenses/MIT
package com.becker.puzzle.tantrix;

import com.becker.puzzle.common.AbstractPuzzleController;
import com.becker.puzzle.common.Refreshable;
import com.becker.puzzle.tantrix.model.*;

import java.util.List;

/**
 * The controller allows the solver to do its thing by providing the PuzzleController api.
 * Originally I had implemented solvers without trying to do concurrency, and those less generic
 * forms still exist, but do not require the PuzzleController api.
 * 
 * The generic solvers (sequential and concurrent) expect the first class param
 * to represent the state of a board, and the HexTile (second param)
 * to represent a move. The way a move is applied is simply to add the piece to the
 * end of the current list.
 *
 * @author Barry Becker
 */
public class TantrixController extends AbstractPuzzleController<TantrixBoard, TilePlacement> {

    /**
     * Creates a new instance of \ the Controller
     */
    public TantrixController(Refreshable<TantrixBoard, TilePlacement> ui) {
        super(ui);
        algorithm_ = Algorithm.SEQUENTIAL;
    }
 
    public TantrixBoard initialPosition() {
        return new TantrixBoard(new HexTileList());
    }

    public boolean isGoal(TantrixBoard position) {
        return false;
    }

    public List<TilePlacement> legalMoves(TantrixBoard position) {
        return null;
    }

    public TantrixBoard move(TantrixBoard position, TilePlacement move) {
        return null;
    }
}
