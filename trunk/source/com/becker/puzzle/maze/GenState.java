package com.becker.puzzle.maze;

import java.awt.*;

/**
 *  The state space position, depth, and direction while searching.
 *  Immutable.
 *
 *  @author Barry Becker
 */
public class GenState
{

    private Point position_;
    private Point direction_;
    private int depth_;

    public GenState( Point pos, Point dir, int d )
    {
        position_ = pos;
        direction_ = dir;
        depth_ = d;
    }


    public Point getPosition() {
        return position_;
    }

    public Point getDirection() {
        return direction_;
    }

    public int getDepth() {
        return depth_;
    }

}

