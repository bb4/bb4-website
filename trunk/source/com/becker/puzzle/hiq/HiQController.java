package com.becker.puzzle.hiq;

import com.becker.common.Worker;
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
 *
 * Created on July 28, 2007, 6:28 AM
 * @author becker
 */
public class HiQController implements PuzzleController<PegBoard, PegMove> {
    
    private final Refreshable ui_;
    
    /** if true we will try to take advantage of multiple procesors and run with concurrent threads. */
    private static final boolean USE_CONCURRENT = true;
    
    /**
     * Creates a new instance of RedPuzzleController
     */
    public HiQController(Refreshable ui) {        
        ui_ = ui;
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
        for (int i = 0; i < BoardHashKey.SYMMETRIES; i++) {
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
    
    
    public void startSolving() {             

        // Use either concurrent or sequential solver strategy
        final PuzzleSolver<PegBoard, PegMove> solver = 
                USE_CONCURRENT ? 
                    new ConcurrentPuzzleSolver(this, .4f, ui_) :
                    new SequentialPuzzleSolver(this, ui_);        

        Worker worker = new Worker()  {
     
            public Object construct()  {
                
                long t = System.currentTimeMillis(); 
                 
                // this does all the heavy work of solving it.   
                try {
                    List<PegMove> path = solver.solve();            
                } catch (InterruptedException e) {
                    assert false: "Thread interrupted. " + e.getMessage();
                }

                int time = (int)((System.currentTimeMillis() - t));
                System.out.println("solved in " + time + " milliseconds.");

                return null;
            }

            public void finished() {}
        };

        worker.start();  
    }    

}
