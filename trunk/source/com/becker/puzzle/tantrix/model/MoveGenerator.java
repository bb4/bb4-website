// Copyright by Barry G. Becker, 2012. Licensed under MIT License: http://www.opensource.org/licenses/MIT
package com.becker.puzzle.tantrix.model;

import com.becker.common.geometry.Location;
import com.becker.puzzle.hiq.PegBoard;
import com.becker.puzzle.hiq.PegMove;

import java.util.LinkedList;
import java.util.List;

/**
 * Tantrix puzzle move generator. Generates valid next moves.
 *
 * @author Barry Becker
 */
public class MoveGenerator {

    TantrixBoard board;

    /**
     * Constructor
     */
    public MoveGenerator(TantrixBoard board) {
        this.board = board;
    }

    /**
     * @return List of all valid jumps for the current board state
     */
    public List<TilePlacement> generateMoves() {
        List<TilePlacement> moves = new LinkedList<TilePlacement>();
        /*
        List<Location> emptyLocations = board.getLocations(false);
        if (emptyLocations.isEmpty()) {
            moves.add(board.getFirstMove());
        } else {
            for (Location pos : emptyLocations) {
                moves.addAll(findMovesForLocation(pos, false));
            }
        }
        */
        return moves;
    }

}
