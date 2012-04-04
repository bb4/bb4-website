/** Copyright by Barry G. Becker, 2000-2011. Licensed under MIT License: http://www.opensource.org/licenses/MIT  */
package com.becker.puzzle.redpuzzle.solver;

import com.becker.optimization.OptimizationListener;
import com.becker.optimization.Optimizee;
import com.becker.optimization.Optimizer;
import com.becker.optimization.parameter.ParameterArray;
import com.becker.optimization.strategy.OptimizationStrategyType;
import com.becker.puzzle.common.Refreshable;
import com.becker.puzzle.redpuzzle.model.Piece;
import com.becker.puzzle.redpuzzle.model.PieceList;
import com.becker.puzzle.redpuzzle.model.PieceParameterArray;

import java.util.List;

/**
 * Solve the red puzzle using a genetic search algorithm.
 * Solves the puzzle in 3.5 seconds on Core2 duo system (6 generations).
 *
 * @author Barry Becker
 */
public class GeneticSearchSolver extends RedPuzzleSolver
                                 implements Optimizee, OptimizationListener {

    public static final int SOLVED_THRESH = 1000;

    /** bonuses given to the scoring algorithm if 3 nubs fit on a side piece. */
    public static final double THREE_FIT_BOOST = 0.1;
    /** bonuses given to the scoring algorithm if 4 nubs on the center piece fit. */
    public static final double FOUR_FIT_BOOST = 0.6;

    // the max number of fitting nubs that we can have. The puzzle is solved if this happens.
    public static final double MAX_FITS = 24 + 4 * THREE_FIT_BOOST + FOUR_FIT_BOOST;

    /** either genetic or concurrent genetic strategy. */
    private OptimizationStrategyType strategy;


    /** Constructor */
    public GeneticSearchSolver(PieceList pieces, Refreshable<PieceList, Piece> puzzlePanel,
                               boolean useConcurrency) {
        super(pieces);
        puzzlePanel_ = puzzlePanel;
        strategy = useConcurrency ? OptimizationStrategyType.CONCURRENT_GENETIC_SEARCH :
                                    OptimizationStrategyType.GENETIC_SEARCH;
    }

    /**
     * @return list of moves to a solution.
     */
    @Override
    public List<Piece> solve()  {
        
        ParameterArray initialGuess = new PieceParameterArray(pieces_);
        solution_ = pieces_;
        long startTime = System.currentTimeMillis();
        
        Optimizer optimizer = new Optimizer(this);

        optimizer.setListener(this);

        ParameterArray solution =
            optimizer.doOptimization(strategy, initialGuess, SOLVED_THRESH);

        solution_ = ((PieceParameterArray)solution).getPieceList();
        List<Piece> moves;
        if (evaluateFitness(solution) >= SOLVED_THRESH) {
            moves = solution_.getPieces();
        } else {
            moves = null;
        }    
        long elapsedTime = System.currentTimeMillis() - startTime;       
        puzzlePanel_.finalRefresh(moves, solution_, numTries_, elapsedTime);
        
        return moves;
    }

    public String getName() {
         return "Genetic Search Solver for Red Puzzle";
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
        PieceList pieces = ((PieceParameterArray) params).getPieceList();
        double fitness = getNumFits(pieces);
        params.setFitness(fitness);
        // there are 24 fits when the puzzle is solved.
        if (fitness >= MAX_FITS) {
            return SOLVED_THRESH;
        }
        return fitness;
    }

    public double compareFitness(ParameterArray params1, ParameterArray params2) {
        assert false : "compareFitness not used since we evaluate in an absolute way.";
        return 0;
    }

    /**
     * @return the number of matches for all the nubs.
     */
    private static double getNumFits(PieceList pieces) {
        double totalFits = 0;
        for (int i=0; i < pieces.size(); i++) {
            double nFits = pieces.getNumFits(i);
            totalFits += nFits;
            // give a boost if a give piece has 3 or 4 fits.
            if (nFits == 3) {
                totalFits += THREE_FIT_BOOST;
            } else if (nFits == 4) {
                // center piece
                totalFits += FOUR_FIT_BOOST;
            }
        }
        assert(totalFits <= MAX_FITS) :
                "fits exceeded " + MAX_FITS +". Fits="+totalFits +" pieces="+pieces;
        return totalFits;
    }

    /**
     * called when the optimizer has made some progress optimizing.
     * We show the current status.
     * @param params
     */
    public void optimizerChanged(ParameterArray params) {
        // update our current best guess at the solution.
        solution_ = ((PieceParameterArray) params).getPieceList();
        numTries_ ++;
        puzzlePanel_.refresh(solution_, numTries_);
    }
}
