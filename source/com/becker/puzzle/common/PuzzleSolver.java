package com.becker.puzzle.common;

import java.util.List;

/**
 * PuzzleSolver interface
 * <p/>
 * P is the Puzzle type
 * M is the move type
 *
 * @author Barry Becker
 */
public interface PuzzleSolver<P, M>  {
  
    /**
     *Solve the puzzle and return a list of moves that lead to the solution. 
     */
    List<M> solve()  throws InterruptedException;
    
}
