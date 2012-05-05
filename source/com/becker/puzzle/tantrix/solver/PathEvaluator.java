// Copyright by Barry G. Becker, 2012. Licensed under MIT License: http://www.opensource.org/licenses/MIT
package com.becker.puzzle.tantrix.solver;


import com.becker.puzzle.tantrix.model.TantrixBoard;
import com.becker.puzzle.tantrix.model.verfication.ConsistencyChecker;
import com.becker.puzzle.tantrix.model.verfication.InnerSpaceDetector;
import com.becker.puzzle.tantrix.model.verfication.LoopDetector;

/**
 * Evaluates the fitness of a tantrix path.
 * It gets the top score if it is a loop and all the path colors match
 *
 * @author Barry Becker
 */
public class PathEvaluator {

    private static final double LOOP_PROXIMITY_WEIGHT = 0.5;
    private static final double LOOP_WEIGHT = 0.7;
    private static final double PATH_MATCH_WEIGHT = 0.4;
    private static final double CONSISTENT_LOOP_BONUS = 0.3;
    private static final double PERFECT_LOOP_BONUS = 2.0;


    private TantrixBoard board;

    public PathEvaluator(TantrixBoard board) {
        this.board = board;
    }

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
            InnerSpaceDetector innerDetector = new InnerSpaceDetector(board);
            perfectLoop = !innerDetector.hasInnerSpaces();
        }

        double fitness =
                LOOP_PROXIMITY_WEIGHT * (numTiles - distance) / numTiles
                + (isLoop ? LOOP_WEIGHT : 0)
                + (double)numFits / numTiles * PATH_MATCH_WEIGHT
                + (consistentLoop ? CONSISTENT_LOOP_BONUS : 0)
                + (perfectLoop ? PERFECT_LOOP_BONUS : 0);
        System.out.println("fitness=" + fitness);

        return fitness;
    }




}
