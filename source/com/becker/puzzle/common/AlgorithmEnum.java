package com.becker.puzzle.common;

/**
 * Enum for type of solver to employ when solving the puzzle.
 * 
 * @author Barr Becker
 */
public interface AlgorithmEnum<P, M> {
    
    String getLabel();
    
    PuzzleSolver createSolver(PuzzleController<P, M> controller, Refreshable<P, M> ui);
    
}
