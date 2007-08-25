package com.becker.puzzle.redpuzzle;

import com.becker.puzzle.common.ConcurrentPuzzleSolver;
import com.becker.puzzle.common.PuzzleSolver;
import com.becker.puzzle.common.Refreshable;
import com.becker.puzzle.common.SequentialPuzzleSolver;

/**
 *
 * Created on August 16, 2007, 5:54 AM
 * @author becker
 */
public enum Algorithm {
    
    BRUTE_FORCE_ORIGINAL("Brute force (hand crafted)"),  
    BRUTE_FORCE_SEQUENTIAL("Brute force (sequential)"), 
    BRUTE_FORCE_CONCURRENT("Brute force (concurrent)"), 
    GENETIC_SEARCH("Genetic search");
    
    private String label;
    
    /**
     *Private constructor
     * Creates a new instance of Algorithm
     */
    private Algorithm(String label) {
        this.label = label;
    }
    
    public String getLabel() {
        return label;
    }
    
    /**
     * Create an instance of the algorithm given the controller and a refreshable.
     */
    public PuzzleSolver createSolver(RedPuzzleController controller, Refreshable ui) {
        PieceList pieces =  PieceList.getInitialPuzzlePieces();
        switch (this) {
            case BRUTE_FORCE_ORIGINAL :
                return new BruteForceSolver(pieces, ui);
            case BRUTE_FORCE_SEQUENTIAL :
                return new SequentialPuzzleSolver(controller, ui);
            case BRUTE_FORCE_CONCURRENT :
                return new ConcurrentPuzzleSolver(controller, 0.2f, ui);
            case GENETIC_SEARCH :
                return new GeneticSearchSolver( pieces, ui);
        }
        return null; //never reached
    }
}
