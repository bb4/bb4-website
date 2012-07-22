/** Copyright by Barry G. Becker, 2000-2011. Licensed under MIT License: http://www.opensource.org/licenses/MIT  */
package com.barrybecker4.puzzle.maze.model;

import com.barrybecker4.common.geometry.IntLocation;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;

/**
 * Possible directions that we can go.
 * Vary the probability that each direction occurs for interesting effects.
 * the sum of these probabilities must sum to 1.
 *
 * @author Barry Becker
 */
public enum Direction {

    FORWARD(0.5) {
        @Override
        public IntLocation apply(IntLocation p) { return p; }
    },
    LEFT(0.28) {
        @Override
        public IntLocation apply(IntLocation p) { return leftOf(p); }
    },
    RIGHT(0.22) {
        @Override
        public IntLocation apply(IntLocation p) { return rightOf(p); }
    };

    private double probability_;

    private static Random RANDOM = new Random(1);

    Direction(double probability) {
        probability_ = probability;
    }

    public double getProbability() {
        return probability_;
    }

    public void setProbability(double probability) {
        probability_ = probability;
    }

    public abstract IntLocation apply(IntLocation dir);

    /**
     * return a shuffled list of directions
     * they are ordered given the potentially skewed probabilities at the top.
     */
    public static List<Direction> getShuffledDirections() {
        double rnd = RANDOM.nextDouble();
        List<Direction> directions = new ArrayList<Direction>();
        List<Direction> originalDirections = new ArrayList<Direction>();
        originalDirections.addAll(Arrays.asList(values()));

        double fwdProb = FORWARD.getProbability();
        double leftProb = LEFT.getProbability();
        double rightProb = RIGHT.getProbability();
        double sum = fwdProb + leftProb + rightProb;
        fwdProb /= sum;
        leftProb /= sum;
        rightProb /= sum;

        if (rnd < fwdProb) {
            directions.add( originalDirections.remove( 0 ) );
            directions.add( getSecondDir( originalDirections,  leftProb));
        }
        else if ( rnd >= fwdProb && rnd < ( fwdProb + leftProb) ) {
            directions.add( originalDirections.remove( 1 ) );
            directions.add( getSecondDir( originalDirections,  fwdProb));
        }
        else {
            directions.add( originalDirections.remove( 2 ) );
            directions.add( getSecondDir( originalDirections,  fwdProb));
        }
        // the third direction is whatever remains
        directions.add( originalDirections.remove( 0 ) );
        return directions;
    }

    /**
     * Determine the second direction in the list given a probability
     * @return  the second direction.
     */
    private static Direction getSecondDir( List twoDirections, double p1) {
        double rnd = RANDOM.nextDouble();
        if ( rnd < p1 )
            return (Direction) twoDirections.remove( 0 );
        else
            return (Direction) twoDirections.remove( 1 );
    }


    /**
     *  find the direction which is counterclockwise 90 (to the left) of the specified dir.
     */
    private static IntLocation leftOf( IntLocation dir ) {
        IntLocation newDir;
        if ( dir.getX() == 0 ) {
            newDir = new IntLocation(0, (dir.getY() > 0)? -1 : 1 );
        }
        else {  // assumed dir.y == 0
            newDir = new IntLocation(( dir.getX() > 0)? 1 : -1, 0);
        }
        return newDir;
    }

    /**
     * find the direction which is clockwise 90 (to the right) of the specified dir.
     */
    private static IntLocation rightOf( IntLocation dir ) {
        IntLocation newDir ;
        if ( dir.getX() == 0 ) {
            newDir = new IntLocation(0, (dir.getY() > 0)? 1 : -1);
        }
        else {  // assumed dir.y == 0
            newDir = new IntLocation((dir.getX() > 0)? -1 : 1, 0);
        }
        return newDir;
    }
}
