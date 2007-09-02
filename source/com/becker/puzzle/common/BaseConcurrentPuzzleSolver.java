package com.becker.puzzle.common;

import java.util.*;
import java.util.concurrent.*;

/**
 * ConcurrentPuzzleSolver
 * <p/>
 * Concurrent version of puzzle solver.
 * Does not recognize when there is no solution (use ConcurrentPuzzle Solver instead)
 *
 * @author Brian Goetz and Tim Peierls
 */
public class BaseConcurrentPuzzleSolver <P, M>  implements PuzzleSolver<P, M> {
    private final PuzzleController<P, M> puzzle;
    private final ExecutorService exec;
    
    private final Set<P> seen;
    protected final ValueLatch<PuzzleNode<P, M>> solution = new ValueLatch<PuzzleNode<P, M>>();
    private final Refreshable<P, M> ui;
    private volatile int numTries;
    /** default is a mixture between depth (0) (sequential) and breadth (1.0) (concurrent) first search. */
    private float depthBreadthFactor = 0.4f;
    private static final Random RANDOM = new Random(1);

    public BaseConcurrentPuzzleSolver(PuzzleController<P, M> puzzle, Refreshable<P, M> ui) {
        this.ui = ui;
        this.puzzle = puzzle;
        this.exec = initThreadPool();
        this.seen = new HashSet<P>();
        if (exec instanceof ThreadPoolExecutor) {
            ThreadPoolExecutor tpe = (ThreadPoolExecutor) exec;
            tpe.setRejectedExecutionHandler(new ThreadPoolExecutor.DiscardPolicy());
        }
    }
    
    /**
     *The amount that you want the search to use depth first or breadth first search.
     *If factor is 0, then all depth first traversal and not concurrent, if 1 then all breadth first search and not sequential.
     *If the search is large, it is easier to run out of memory at the extremes.
     *Must be greater than 0 to have some amount of concurrency used.
     *@param factor a number between 0 and 1. One being all breadth frist search and not sequential. 
     */
    protected void setDepthBreadthFactor(float factor) {
        depthBreadthFactor = factor;
    }

    private ExecutorService initThreadPool() {
        //return Executors.newCachedThreadPool();
        return Executors.newFixedThreadPool(1000);
    }

    public List<M> solve() throws InterruptedException {
        try {
            P p = puzzle.initialPosition();
            long startTime = System.currentTimeMillis();          
            exec.execute(newTask(p, null, null));
            // block until solution found
            PuzzleNode<P, M> solnPuzzleNode = solution.getValue();
            List<M> path = (solnPuzzleNode == null) ? null: solnPuzzleNode.asMoveList();
            if (ui != null) {      
                long elapsedTime = System.currentTimeMillis() - startTime;
                ui.finalRefresh(path, solnPuzzleNode.position, numTries, elapsedTime);
            } 
            return path;
        } finally {
            exec.shutdown();
        }
    }

    protected Runnable newTask(P p, M m, PuzzleNode<P, M> n) {
        return new SolverTask(p, m, n);
    }

    protected class SolverTask extends PuzzleNode<P, M> implements Runnable {
        SolverTask(P pos, M move, PuzzleNode<P, M> prev) {
            super(pos, move, prev);
        }

        public void run() {
            numTries++;             
            if (solution.isSet() || puzzle.alreadySeen(position, seen)) {                         
                return; // already solved or seen this position
            }
            if (ui!=null && !solution.isSet()) {     
          
                ui.refresh(position, numTries);                    
            }     
            if (puzzle.isGoal(position)) {                
                solution.setValue(this);
            }
            else {
                for (M m : puzzle.legalMoves(position)) {
                    Runnable task = newTask(puzzle.move(position, m), m, this);
                    // either process the children sequentially or concurrently based on  depthBreadthFactor               
                    if (RANDOM.nextFloat() > depthBreadthFactor) 
                        task.run();
                    else
                        exec.execute(task);
                }
            }
        }
    }
}

