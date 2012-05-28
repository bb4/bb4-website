// Copyright by Barry G. Becker, 2012. Licensed under MIT License: http://www.opensource.org/licenses/MIT
package com.becker.puzzle.tantrix.solver.path;


import com.becker.puzzle.tantrix.model.Tantrix;
import com.becker.puzzle.tantrix.model.verfication.ConsistencyChecker;
import com.becker.puzzle.tantrix.model.verfication.InnerSpaceDetector;

/**
 * Evaluates the fitness of a tantrix path.
 * It gets the top score if it is a loop and all the path colors match
 *
 * @author Barry Becker
 */
public class PathEvaluator {

    /** How close are the endpoints of the primary path from forming a loop. */
    private static final double LOOP_PROXIMITY_WEIGHT = 0.5;

    /** Weight to give if we actually have a primary path loop. */
    private static final double LOOP_WEIGHT = 0.7;

    /** Weight to give matching paths (includes secondary paths) */
    private static final double PATH_MATCH_WEIGHT = 0.4;

    /** We have a loop and all paths match */
    private static final double CONSISTENT_LOOP_BONUS = 0.3;

    /** consistent loop and no inner spaces. */
    private static final double PERFECT_LOOP_BONUS = 2.0;


    /**
     * The main criteria for quality of the path is
     *  1) How close the ends of the path are to each other. Perfection achieved when we have a closed loop.
     *  2) Better if more matching secondary path colors
     *  3) Fewer inner spaces and a bbox with less area.
     * @return  the number of different ways we have tried to fit pieces together so far.
     */
    public double evaluateFitness(TantrixPath path) {

        int numTiles = path.size();
        double distance = path.getEndPointDistance();
        boolean isLoop = distance == 0 && path.isLoop();

        ConsistencyChecker checker = new ConsistencyChecker(path.getTilePlacements(), path.getPrimaryPathColor());
        int numFits = checker.numFittingTiles();
        boolean allFit = numFits == numTiles;
        boolean consistentLoop = isLoop && allFit;
        boolean perfectLoop = false;

        if (consistentLoop) {
            Tantrix tantrix = new Tantrix(path.getTilePlacements());
            InnerSpaceDetector innerDetector = new InnerSpaceDetector(tantrix);
            perfectLoop = !innerDetector.hasInnerSpaces();
            //System.out.println("perfect loop");
        }

        double fitness =
                LOOP_PROXIMITY_WEIGHT * (numTiles - distance) / (0.1 + numTiles)
                + (isLoop ? LOOP_WEIGHT : 0)
                + (double)numFits / numTiles * PATH_MATCH_WEIGHT
                + (consistentLoop ? CONSISTENT_LOOP_BONUS : 0)
                + (perfectLoop ? PERFECT_LOOP_BONUS : 0);
        System.out.println("fitness=" + fitness);
        assert !Double.isNaN(fitness) :
                "Invalid fitness  isLoop=" + isLoop + " consistentLoop=" + consistentLoop
                + " numTiles=" + numTiles + " distance=" + distance;
        return fitness;
    }
}
