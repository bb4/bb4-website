/** Copyright by Barry G. Becker, 2000-2011. Licensed under MIT License: http://www.opensource.org/licenses/MIT  */
package com.becker.puzzle.maze;

import java.awt.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;

/**
 * Possible directions that we can go.
 * Vary the probability that each direction occurs for interesting effects.
 * the sum of these probabilities must sum to 1.
 *
 * @author Barry Becker Date: Nov 27, 2005
 */
public enum Direction {

    FORWARD(0.5) {
        @Override
        public Point apply(Point p) { return p; }
    },
    LEFT(0.28) {
        @Override
        public Point apply(Point p) { return leftOf(p); }
    },
    RIGHT(0.22) {
        @Override
        public Point apply(Point p) { return rightOf(p); }
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

    public abstract Point apply(Point dir);

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
     *  find the direction which is counterclockwise 90 to the left of the specified dir.
     */
    private static Point leftOf( Point dir ) {
        Point newDir;
        if ( dir.x == 0 ) {
            newDir = new Point((dir.y > 0)? -1 : 1, 0 );
        }
        else {  // assumed dir.y == 0
            newDir = new Point( 0, ( dir.x > 0)? 1 : -1);
        }
        return newDir;
    }

    /**
     * find the direction which is clockwise 90 to the right of the specified dir.
     */
    private static Point rightOf( Point dir ) {
        Point newDir ;
        if ( dir.x == 0 ) {
            newDir = new Point( (dir.y > 0)? 1 : -1, 0 );
        }
        else {  // assumed dir.y == 0
            newDir = new Point( 0, (dir.x > 0)? -1 : 1);
        }
        return newDir;
    }
}
