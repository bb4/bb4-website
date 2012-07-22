/** Copyright by Barry G. Becker, 2000-2011. Licensed under MIT License: http://www.opensource.org/licenses/MIT  */
package com.barrybecker4.puzzle.maze.model;

import com.barrybecker4.common.geometry.IntLocation;

/**
 *  The state space position, depth, and direction while searching.
 *  Immutable.
 *
 *  @author Barry Becker
 */
public class GenState {

    private IntLocation position_;
    private IntLocation direction_;
    private int depth_;

    public GenState( IntLocation pos, IntLocation dir, int d ) {
        position_ = pos;
        direction_ = dir;
        depth_ = d;
    }

    public IntLocation getPosition() {
        return position_;
    }

    public IntLocation getDirection() {
        return direction_;
    }

    public int getDepth() {
        return depth_;
    }

    public String toString() {
        return "[pos=" + position_ + " dir="+ direction_ + " depth="+ depth_ + "]";
    }
}


