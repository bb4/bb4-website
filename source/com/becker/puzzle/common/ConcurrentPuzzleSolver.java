package com.becker.puzzle.common;

import java.util.concurrent.atomic.*;

/**
 * PuzzleSolver
 * <p/>
 * Solver that recognizes when no solution exists and stops running it that happens.
 *
 * @author Brian Goetz and Tim Peierls
 */
public class ConcurrentPuzzleSolver <P,M> extends BaseConcurrentPuzzleSolver<P, M> {
    
    public ConcurrentPuzzleSolver(PuzzleController<P, M> puzzle, Refreshable<P, M> ui) {
        super(puzzle, ui);
    }
    
    public ConcurrentPuzzleSolver(PuzzleController<P, M> puzzle, float  depthBreadthFactor, Refreshable<P, M> ui) {
        super(puzzle, ui);
        setDepthBreadthFactor(depthBreadthFactor);
    }

    private final AtomicInteger taskCount = new AtomicInteger(0);

    protected Runnable newTask(P p, M m, PuzzleNode<P, M> n) {
        return new CountingSolverTask(p, m, n);
    }

    class CountingSolverTask extends SolverTask {
        CountingSolverTask(P pos, M move, PuzzleNode<P, M> prev) {
            super(pos, move, prev);
            taskCount.incrementAndGet();
        }

        public void run() {
            try {
                super.run();
            } finally {
                if (taskCount.decrementAndGet() == 0)
                    solution.setValue(null);
            }
        }
    }
}
