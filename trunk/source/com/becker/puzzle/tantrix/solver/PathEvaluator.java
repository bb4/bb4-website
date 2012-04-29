// Copyright by Barry G. Becker, 2012. Licensed under MIT License: http://www.opensource.org/licenses/MIT
package com.becker.puzzle.tantrix.solver;


/**
 * Evaluates the fitness of a tantrix path.
 * It gets the top score if it is a loop and all the path colors match
 *
 * @author Barry Becker
 */
public class PathEvaluator {

    private static final double LOOP_PROXIMITY_WEIGHT = 0.5;
    private static final double SECONDARY_PATH_MATCH_WEIGHT = 0.4;
    private static final double FEW_INNER_SPACE_WEIGHT = 0.1;


    /**
     * Constructor
     */
    public PathEvaluator() {
    }

    /**
     * The main criteria for quality of the path is
     *  1) How close the ends of the path are to each other. Perfection achieved when we have a closed loop.
     *  2) Better if more matching secondary path colors
     *  3) Fewer inner spaces and a bbox with less area.
     * @return  the number of different ways we have tried to fit pieces together so far.
     */
    public double evaluateFitness(TantrixPath path) {
        return 9;
    }



}
