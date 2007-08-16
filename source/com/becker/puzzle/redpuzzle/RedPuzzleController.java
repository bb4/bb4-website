package com.becker.puzzle.redpuzzle;

import com.becker.common.Util;
import com.becker.puzzle.common.Refreshable;

/**
 *
 * Created on August 11, 2007, 6:12 AM
 * @author becker
 */
public class RedPuzzleController {

    public static final int NUM_PIECES = PieceList.NUM_PIECES;
    
    // the viewer that can show the curent state.
    private final Refreshable ui_;

    private Algorithm algorithm_ = Algorithm.BRUTE_FORCE_SEQUENTIAL;
    
    
    /**
     * Creates a new instance of RedPuzzleController
     */
    public RedPuzzleController(Refreshable ui) {        
        ui_ = ui;
    }
        
    /**
     * There are different approaches we can take to solving the red puzzle.
     *
     * @param alg
     */
    public void setAlgorithm(Algorithm algorithm) {
        algorithm_ = algorithm;
    }
    
    /**
     * solve using the algorithm set in setAlgorithm.
     */
    public void startSolving() {
       long t = System.currentTimeMillis();
        
        PuzzleSolver solver_ = algorithm_.createSolver();
        boolean solved = solver_.solvePuzzle(ui_);

        if ( solved ) {
            double sec = (float)(System.currentTimeMillis() - t) / 1000.0;
            System.out.println("Solved in " + Util.formatNumber(sec) + " seconds.");
            System.out.println( "The final solution is shown. the number of iterations was:" + solver_.getNumIterations() );
        }
        else {
            System.out.println( "This puzzle is not solvable!" ); // guaranteed not to happen
        }        
    }
   
}
