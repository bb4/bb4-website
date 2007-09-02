package com.becker.puzzle.redpuzzle;

import com.becker.optimization.*;
import com.becker.puzzle.common.Refreshable;
import java.util.List;

/**
 * Solve the red puzzle using a genetic search algorithm.
 * Solves the puzzle in 3.5 seconds on Core2 duo system (6 generations).
 *
 * @author Barry Becker Date: Aug 6, 2006
 */
public class GeneticSearchSolver extends RedPuzzleSolver
                                 implements Optimizee, OptimizationListener {

    public static final int SOLVED_THRESH = 1000;

    /** these boosters are bonuses we give to the scoring algorithm if 3 or four nubs on a side fit. */
    public static final double THREE_FIT_BOOST = 0.1;
    public static final double FOUR_FIT_BOOST = 0.6;

    // the max number of fitting nubs that we can have. The puzzle is solved if this happens.
    public static final double MAX_FITS = 24 + 4 * THREE_FIT_BOOST + FOUR_FIT_BOOST;


    public GeneticSearchSolver(PieceList pieces,  Refreshable puzzlePanel) {
        super(pieces);
        puzzlePanel_ = puzzlePanel;
    }

    /**
     * @param puzzlePanel will show the pieces as we arrange them.
     * @return true if a solution is found.
     */
    public List<Piece> solve()  {
        
        ParameterArray initialGuess = new PieceParameterArray(pieces_);        
        solution_ = pieces_;
        long startTime = System.currentTimeMillis();
        
        Optimizer optimizer = new Optimizer(this);

        optimizer.setListener(this);

        ParameterArray solution =
            optimizer.doOptimization(OptimizationType.GENETIC_SEARCH,
                                     initialGuess, SOLVED_THRESH);

        solution_ = ((PieceParameterArray)solution).getPieceList();
        List<Piece> moves = null;
        if (evaluateFitness(solution) >= SOLVED_THRESH) {
            moves = solution_.getPieces();
        } else {
            moves = null;
        }    
        long elapsedTime = System.currentTimeMillis() - startTime;       
        puzzlePanel_.finalRefresh(moves, solution_, numTries_, elapsedTime);
        
        return moves;
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
     * @param params
     * @return
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
        assert false; // not used since we evaluate in an absolute way.
        return 0;
    }

    public int getNumParameters() {
        return pieces_.size();
    }

    /**
     * @return the number of matches for all the nubs.
     */
    private static double getNumFits(PieceList pieces) {
        double totalFits = 0;
        for (int i=0; i< pieces.size(); i++) {
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
