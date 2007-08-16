package com.becker.puzzle.redpuzzle;

import net.jcip.examples.ThisEscape;

/**
 *
 * Created on August 16, 2007, 5:54 AM
 * @author becker
 */
public enum Algorithm {
    
    BRUTE_FORCE_SEQUENTIAL("Brute force (sequential)"), 
    //BRUTE_FORCE_CONCURRENT("Brute force (concurrent)"), 
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
    
    public PuzzleSolver createSolver() {
        switch (this) {
            case BRUTE_FORCE_SEQUENTIAL :
                return new BruteForceSolver( PieceList.getInitialPuzzlePieces(), false);
            //case BRUTE_FORCE_CONCURRENT :
            //    return new BruteForceSolver( PieceList.getInitialPuzzlePieces(), true);
            case GENETIC_SEARCH :
                return new GeneticSearchSolver( PieceList.getInitialPuzzlePieces());
        }
        return null; //never reached
    }
    
}
