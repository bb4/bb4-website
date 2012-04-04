/** Copyright by Barry G. Becker, 2000-2011. Licensed under MIT License: http://www.opensource.org/licenses/MIT  */
package com.becker.puzzle.hiq;

import com.becker.puzzle.common.AbstractPuzzleController;
import com.becker.puzzle.common.Refreshable;

import java.util.List;
import java.util.Set;

/**
 * HiQ Puzzle Controller.
 *See puzzle.common for puzzle framework classes.
 *
 * @author Barry Becker
 */
public class HiQController extends AbstractPuzzleController<PegBoard, PegMove> {    

    /**
     * @param ui shows the current state on the screen.
     */
    public HiQController(Refreshable<PegBoard, PegMove> ui) {
        super(ui);
        // set default
        algorithm_ = Algorithm.CONCURRENT_OPTIMUM;
    }

    public PegBoard initialPosition() {
        return PegBoard.INITIAL_BOARD_POSITION;
    }

    public boolean isGoal(PegBoard position) {
        return position.isSolved();
    }

    public List<PegMove> legalMoves(PegBoard position) {   
        return new MoveGenerator(position).generateMoves();
    }

    public PegBoard move(PegBoard position, PegMove move) {
        return position.doMove(move, false);
    }
    
    /**
     * Check all board symmetries to be sure it has or has not been seen.
     * If it was never seen before add it.
     * Must be synchronized because some solvers use concurrency.
     */
    @Override
    public synchronized boolean alreadySeen(PegBoard position, Set<PegBoard> seen) {
       
        boolean visited = false;
        for (int i = 0; i < PegBoard.SYMMETRIES; i++) {
              if (seen.contains(position.symmetry(i))) {
                  visited = true;
                  break;
              }
        }
        if (!visited) {
            seen.add(position);
        }
        return visited;
    }
  
}
