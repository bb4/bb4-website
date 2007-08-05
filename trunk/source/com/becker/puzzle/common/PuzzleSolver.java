package com.becker.puzzle.common;

import java.util.List;
import java.util.concurrent.atomic.*;

/**
 * PuzzleSolver interface
 * <p/>
 *
 * @author Barry Becker
 */
public interface PuzzleSolver<P, M>  {
  
    /**
     *Solve the puzzle and return a list of moves that lead to the solution. 
     */
    List<M> solve()  throws InterruptedException;
    
}
