package com.becker.puzzle.common;

/**
 * Enum for type of solver to employ when solving the puzzle.
 * 
 * @author Barr Becker
 */
public interface AlgorithmEnum {
    
    String getLabel();
    
    PuzzleSolver createSolver(PuzzleController controller, Refreshable ui);
    
}
