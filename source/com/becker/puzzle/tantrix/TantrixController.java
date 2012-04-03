// Copyright by Barry G. Becker, 2012. Licensed under MIT License: http://www.opensource.org/licenses/MIT
package com.becker.puzzle.tantrix;

import com.becker.puzzle.common.AbstractPuzzleController;
import com.becker.puzzle.common.Refreshable;
import com.becker.puzzle.tantrix.model.HexTile;
import com.becker.puzzle.tantrix.model.HexTileList;
import com.becker.puzzle.tantrix.model.HexTiles;

import java.util.LinkedList;
import java.util.List;

/**
 * The controller allows the solver to do its thing by providing the PuzzleController api.
 * Originally I had implemented solvers without trying to do concurrency, and those less generic
 * forms still exist, but do not require the PuzzleController api.
 * 
 * The generic solvers (sequential and concurrent) expect the PieceList to represent the state of a board,
 * and the Piece to represent a move. The way a move is applied is simply to add the piece to the 
 * end of the current list.
 *
 * @author Barry Becker
 */
public class TantrixController extends AbstractPuzzleController<HexTileList, HexTile> {

    /**
     * Creates a new instance of RedPuzzleController
     */
    public TantrixController(Refreshable<HexTileList, HexTile> ui) {
        super(ui);
        algorithm_ = Algorithm.BRUTE_FORCE_ORIGINAL;
    }
 
    public HexTileList initialPosition() {
        // empty piece list
        HexTileList masterList = new HexTiles();
        return masterList;
    }

    public boolean isGoal(HexTileList position) {
        return false;
    }

    public List<HexTile> legalMoves(HexTileList position) {
        return null;
    }

    public HexTileList move(HexTileList position, HexTile move) {
        return null;
    }
}
