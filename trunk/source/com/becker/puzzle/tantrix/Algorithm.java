// Copyright by Barry G. Becker, 2012. Licensed under MIT License: http://www.opensource.org/licenses/MIT
package com.becker.puzzle.tantrix;

import com.becker.puzzle.common.AlgorithmEnum;
import com.becker.puzzle.common.PuzzleController;
import com.becker.puzzle.common.Refreshable;
import com.becker.puzzle.common.solver.PuzzleSolver;
import com.becker.puzzle.tantrix.model.HexTile;
import com.becker.puzzle.tantrix.model.HexTileList;
import com.becker.puzzle.tantrix.model.HexTiles;

/**
 * Enum for type of solver to employ when solving the puzzle.
 * 
 * @author Barry Becker
 */
public enum Algorithm implements AlgorithmEnum<HexTileList, HexTile> {
    
    BRUTE_FORCE_ORIGINAL("Brute force (hand crafted)");

    private String label;
    
    /**
     *Private constructor
     * Creates a new instance of Algorithm
     */
    Algorithm(String label) {
        this.label = label;
    }
    
    public String getLabel() {
        return label;
    }
    
    /**
     * Create an instance of the algorithm given the controller and a refreshable.
     */
    public PuzzleSolver<HexTileList, HexTile> createSolver(PuzzleController<HexTileList, HexTile> controller,
                                                           Refreshable<HexTileList, HexTile> ui) {
        HexTileList tiles = new HexTiles();
        switch (this) {
            case BRUTE_FORCE_ORIGINAL :
                return new BruteForceSolver<HexTileList, HexTile>(tiles, ui);
        }
        return null; //never reached
    }
}
