// Copyright by Barry G. Becker, 2012. Licensed under MIT License: http://www.opensource.org/licenses/MIT
package com.barrybecker4.puzzle.maze.model;

import com.barrybecker4.common.geometry.IntLocation;

import java.util.LinkedList;
import java.util.List;

/**
 *  Stack of GenStates to try during the search.
 *
 *  @author Barry Becker
 */
public class StateStack extends LinkedList<GenState> {

    /**
     * From currentPosition try moving in each direction in a random order.
     * Assigning different probabilities to the order in which we check these directions
     * can give interesting effects.
     */
    public  void pushMoves(IntLocation currentPosition, IntLocation currentDir, int depth) {

        List<Direction> directions = Direction.getShuffledDirections();

        // check all the directions except the one we came from
        for ( int i = 0; i < 3; i++ ) {
            Direction direction = directions.get(i);
            IntLocation dir = direction.apply(currentDir);
            add(0, new GenState( currentPosition, dir, depth ) );
        }
    }

}
