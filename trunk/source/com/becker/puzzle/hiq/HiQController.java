package com.becker.puzzle.hiq;

import com.becker.common.Worker;
import com.becker.puzzle.common.AbstractPuzzleController;
import com.becker.puzzle.common.AlgorithmEnum;
import com.becker.puzzle.common.BaseConcurrentPuzzleSolver;
import com.becker.puzzle.common.ConcurrentPuzzleSolver;
import com.becker.puzzle.common.PuzzleController;
import com.becker.puzzle.common.PuzzleSolver;
import com.becker.puzzle.common.Refreshable;
import com.becker.puzzle.common.SequentialPuzzleSolver;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * HiQ Puzzle Controller.
 *See puzzle.common for puzzle framework classes.
 *
 * Created on July 28, 2007
 * @author becker
 */
public class HiQController extends AbstractPuzzleController<PegBoard, PegMove> {    

    public HiQController(Refreshable ui) {        
        super(ui);
        algorithm_ = Algorithm.SEQUENTIAL; 
    }

    public PegBoard initialPosition() {
        return PegBoard.INITIAL_BOARD_POSITION;
    }

    public boolean isGoal(PegBoard position) {
        return position.isSolved();
    }

    public List<PegMove> legalMoves(PegBoard position) {   
        return position.generateMoves();
    }

    public PegBoard move(PegBoard position, PegMove move) {
        return position.doMove(move, false);
    }
    
    /**
     *Check all board symmetries to be sure it has or has not been seen.
     *If it was never seen before add it.
     *Must be synchronized because some solvers use concurrency.
     */
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
