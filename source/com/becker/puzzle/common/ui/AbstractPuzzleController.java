/** Copyright by Barry G. Becker, 2000-2011. Licensed under MIT License: http://www.opensource.org/licenses/MIT  */
package com.becker.puzzle.common.ui;

import com.becker.common.concurrency.Worker;
import com.becker.puzzle.common.AlgorithmEnum;
import com.becker.puzzle.common.PuzzleController;
import com.becker.puzzle.common.Refreshable;
import com.becker.puzzle.common.solver.PuzzleSolver;

import java.util.Set;

/**
 * Provides default implementation for a PuzzleController.
 * The puzzle controller updates the ui (refreshable) and determines what algorithm is used to solve it.
 *
 * @author Barry Becker
 */
public abstract class AbstractPuzzleController<P, M> implements PuzzleController<P, M> {

    /** the viewer that can show the current state. */
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
     * There are different approaches we can take to solving the puzzle.
     *
     * @param algorithm strategy to use for solving the puzzle.
     */
    public void setAlgorithm(AlgorithmEnum<P, M> algorithm) {
        algorithm_ = algorithm;
    }

    /**
     * get the solver algorithm..
     */
    public AlgorithmEnum getAlgorithm() {
        return algorithm_;
    }

    /**
     * If this puzzle position was never seen before add it.
     * Must be synchronized because some solvers use concurrency.
     * @return true if this position was already seen while searching.
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
     * Do it in a separate worker thread so the UI is not blocked.
     */
    public void startSolving() {

        // Use either concurrent or sequential solver strategy
        final PuzzleSolver<P, M> solver = algorithm_.createSolver(this, ui_);

        Worker worker = new Worker()  {

            @Override
            public Object construct()  {

                // this does all the heavy work of solving it.
                try {
                    solver.solve();
                } catch (InterruptedException e) {
                    assert false: "Thread interrupted. " + e.getMessage();
                }
                return null;
            }
        };

        worker.start();
    }
}
