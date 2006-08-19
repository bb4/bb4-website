package com.becker.puzzle.redpuzzle;

import com.becker.optimization.*;

/**
 * Solve the red puzzle using a genetic search algorithm.
 *
 * @author Barry Becker Date: Aug 6, 2006
 */
public class GeneticSearchSolver extends PuzzleSolver
                                 implements Optimizee, OptimizationListener {

    public static final int SOLVED_THRESH = 1000;

    // the max number of fitting nubs that we can have. The puzzle is solved if this happens.
    public static final int MAX_FITS = 24;

    private PuzzlePanel puzzlePanel_;

    public GeneticSearchSolver(PieceList pieces) {
        super(pieces);
    }

    /**
     * @param puzzlePanel will show the pieces as we arrange them.
     * @return true if a solution is found.
     */
    public boolean solvePuzzle( PuzzlePanel puzzlePanel)  {

        ParameterArray initialGuess = new PieceParameterArray(pieces_);
        puzzlePanel_ = puzzlePanel;
        solution_ = pieces_;

        Optimizer optimizer = new Optimizer(this);
        optimizer.setListener(this);

        ParameterArray solution =
            optimizer.doOptimization(OptimizationType.GENETIC_SEARCH,
                                     initialGuess, SOLVED_THRESH);

        solution_ = ((PieceParameterArray)solution).getPieceList();
        refresh(puzzlePanel);

        return (evaluateFitness(solution) >= SOLVED_THRESH);
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
        int fitness = getNumFits(pieces);
        if (fitness > 10)
          System.out.println("nf="+fitness);
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
     * @return the number of matches for all the nubs
     */
    private static int getNumFits(PieceList pieces) {
        int totalFits = 0;
        for (int i=0; i< pieces.size(); i++) {
            totalFits += pieces.getNumFits(i);
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
        refresh(puzzlePanel_);
    }
}
