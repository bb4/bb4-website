package com.becker.puzzle.common;

import com.becker.common.Worker;
import java.util.List;
import java.util.Set;

/**
 * Provbides default implementation for a PuzzleController
 *
 * @author Barry Becker
 */
public abstract class AbstractPuzzleController<P, M> implements PuzzleController<P, M> {


    /** the viewer that can show the curent state. */
    protected final Refreshable<P, M> ui_;

    /** default solver. */
    protected AlgorithmEnum<P, M> algorithm_;


    /**
     * Creates a new instance of AbstractPuzzleController
     */
    public AbstractPuzzleController(Refreshable<P, M> ui) {
        ui_ = ui;
    }

    /**
     * There are different approaches we can take to solving thepuzzle.
     *
     * @param alg
     */
    public void setAlgorithm(AlgorithmEnum<P, M> algorithm) {
        algorithm_ = algorithm;
    }

    /**
     * get the solver algoritym..
     */
    public AlgorithmEnum getAlgorithm() {
        return algorithm_;
    }


    /**
     *If it was never seen before add it.
     *Must be synchronized because some solvers use concurrency.
     */
    public synchronized boolean alreadySeen(P position, Set<P> seen) {

        boolean visited = true;
         if (!seen.contains(position)) {
              visited = false;
              seen.add(position);
         }
        return visited;
    }


    /**
     * Begin the process of solving.
     * Do it in a seperate worker thread so the UI is not blocked.
     */
    public void startSolving() {

        // Use either concurrent or sequential solver strategy
        final PuzzleSolver<P, M> solver = algorithm_.createSolver(this, ui_);

        Worker worker = new Worker()  {

            @Override
            public Object construct()  {

                // this does all the heavy work of solving it.
                List<M> path = null;
                try {
                    path = solver.solve();
                } catch (InterruptedException e) {
                    assert false: "Thread interrupted. " + e.getMessage();
                }
                return null;
            }

            @Override
            public void finished() {}
        };

        worker.start();
    }
}
