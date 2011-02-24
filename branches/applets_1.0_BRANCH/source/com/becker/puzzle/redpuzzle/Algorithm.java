package com.becker.puzzle.redpuzzle;

import com.becker.puzzle.common.*;

/**
 * Enum for type of solver to employ when solving the puzzle.
 * 
 * @author Barry Becker
 */
public enum Algorithm  implements AlgorithmEnum<PieceList, Piece> {
    
    BRUTE_FORCE_ORIGINAL("Brute force (hand crafted)"),  
    BRUTE_FORCE_SEQUENTIAL("Brute force (sequential)"), 
    BRUTE_FORCE_CONCURRENT("Brute force (concurrent)"), 
    GENETIC_SEARCH("Genetic search");
    
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
    public PuzzleSolver<PieceList, Piece> createSolver(PuzzleController<PieceList, Piece> controller,
                                                                                      Refreshable<PieceList, Piece> ui) {
        PieceList pieces =  PieceList.getInitialPuzzlePieces();
        switch (this) {
            case BRUTE_FORCE_ORIGINAL :
                return new BruteForceSolver<PieceList, Piece>(pieces, ui);
            case BRUTE_FORCE_SEQUENTIAL :
                return new SequentialPuzzleSolver<PieceList, Piece>(controller, ui);
            case BRUTE_FORCE_CONCURRENT :
                return new ConcurrentPuzzleSolver<PieceList, Piece>(controller, 0.2f, ui);
            case GENETIC_SEARCH :
                return new GeneticSearchSolver( pieces, ui);
        }
        return null; //never reached
    }
}
