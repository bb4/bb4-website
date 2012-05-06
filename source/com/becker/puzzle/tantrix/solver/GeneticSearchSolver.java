// Copyright by Barry G. Becker, 2012. Licensed under MIT License: http://www.opensource.org/licenses/MIT
package com.becker.puzzle.tantrix.solver;

import com.becker.optimization.OptimizationListener;
import com.becker.optimization.Optimizee;
import com.becker.optimization.Optimizer;
import com.becker.optimization.parameter.ParameterArray;
import com.becker.optimization.strategy.OptimizationStrategyType;
import com.becker.puzzle.common.Refreshable;
import com.becker.puzzle.tantrix.model.TantrixBoard;
import com.becker.puzzle.tantrix.model.TilePlacement;
import com.becker.puzzle.tantrix.model.TilePlacementList;

import java.util.ArrayList;
import java.util.List;

/**
 * Solve the Tantrix puzzle using a genetic search algorithm.
 *
 * @author Barry Becker
 */
public class GeneticSearchSolver extends TantrixSolver<TantrixBoard, TilePlacement>
                                 implements Optimizee, OptimizationListener {

    /** When reached, the puzzle is solved. */
    public static final double SOLVED_THRESH = 1000;

    /** either genetic or concurrent genetic strategy. */
    private OptimizationStrategyType strategy;
    private int numTries_;

    private PathEvaluator evaluator;


    /** Constructor */
    public GeneticSearchSolver(TantrixBoard board, Refreshable<TantrixBoard, TilePlacement> puzzlePanel,
                               boolean useConcurrency) {
        super(board);
        puzzlePanel_ = puzzlePanel;
        strategy = useConcurrency ? OptimizationStrategyType.CONCURRENT_GENETIC_SEARCH :
                                    OptimizationStrategyType.GENETIC_SEARCH;
        evaluator = new PathEvaluator();
    }

    /**
     * @return list of moves to a solution.
     */
    @Override
    public TilePlacementList solve()  {

        ParameterArray initialGuess = new TantrixPath(board);
        long startTime = System.currentTimeMillis();

        Optimizer optimizer = new Optimizer(this);
        optimizer.setListener(this);

        ParameterArray solution =
            optimizer.doOptimization(strategy, initialGuess, SOLVED_THRESH);

        solution_ =
            new TantrixBoard(((TantrixPath)solution).getTilePlacements(), board.getPrimaryColor());

        TilePlacementList moves;
        if (evaluateFitness(solution) >= SOLVED_THRESH) {
            moves = ((TantrixPath)solution).getTilePlacements();
        } else {
            moves = null;
        }
        long elapsedTime = System.currentTimeMillis() - startTime;
        puzzlePanel_.finalRefresh(moves, solution_, numTries_, elapsedTime);

        return moves;
    }

    public String getName() {
         return "Genetic Search Solver for Tantrix Puzzle";
    }

    /**
     * terminate the solver if we find a solution with this fitness.
     */
    public double getOptimalFitness() {
        return SOLVED_THRESH;
    }

    public boolean evaluateByComparison() {
        return false;
    }

    /**
     * Return a high score if there are a lot of fits among the pieces.
     * For every nub that fits we count 1
     *
     * @param params  parameters
     * @return fitness value. High is good.
     */
    public double evaluateFitness(ParameterArray params) {

        double fitness = evaluator.evaluateFitness((TantrixPath) params);
        params.setFitness(fitness);
        return fitness;
    }

    public double compareFitness(ParameterArray params1, ParameterArray params2) {
        assert false : "compareFitness not used since we evaluate in an absolute way.";
        return 0;
    }

    /**
     * called when the optimizer has made some progress optimizing.
     * We show the current status.
     * @param params
     */
    public void optimizerChanged(ParameterArray params) {
        // update our current best guess at the solution.
        //solution_ = ((PieceParameterArray) params).getPieceList();
        //numTries_ ++;
        //puzzlePanel_.refresh(solution_, getNumIterations());
    }
}
