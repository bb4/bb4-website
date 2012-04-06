// Copyright by Barry G. Becker, 2012. Licensed under MIT License: http://www.opensource.org/licenses/MIT
package com.becker.puzzle.tantrix;

import com.becker.puzzle.common.AbstractPuzzleController;
import com.becker.puzzle.common.Refreshable;
import com.becker.puzzle.tantrix.model.*;

import java.util.List;

/**
 * The controller allows the solver to do its thing by providing the PuzzleController api.
 *
 * The generic solvers (sequential and concurrent) expect the first class param
 * to represent the state of a board, and the TilePlacement (second param)
 * to represent a move. The way a move is applied is simply to add the piece to the
 * end of the current list.
 *
 * @author Barry Becker
 */
public class TantrixController
       extends AbstractPuzzleController<TantrixBoard, TilePlacement> {

    public static final int MIN_NUM_TILES = 3;
    int numTiles = MIN_NUM_TILES;

    /**
     * Creates a new instance of the Controller
     */
    public TantrixController(Refreshable<TantrixBoard, TilePlacement> ui) {
        super(ui);
        algorithm_ = Algorithm.SEQUENTIAL;
    }

    public void setNumTiles(int numTiles) {
        this.numTiles = numTiles;
    }
 
    public TantrixBoard initialPosition() {
        return new TantrixBoard(new HexTiles().createRandomList(numTiles));
    }

    /**
     * @return true if there is a loop of the primary color and all the
     * secondary color path connections match.
     */
    public boolean isGoal(TantrixBoard position) {
        return position.isSolved();
    }

    public List<TilePlacement> legalMoves(TantrixBoard position) {
        return new MoveGenerator(position).generateMoves();
    }

    public TantrixBoard move(TantrixBoard position, TilePlacement move) {
        return position.placeTile(move);
    }
}
