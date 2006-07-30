package com.becker.misc.eatest.maze;

import java.awt.*;

/**
 *  the state space position, depth, and direction while searching
 *
 *  @author Barry Becker
 */
public class GenState
{

    // 4 if 2d 6 if 3d 12 if 4d
    //public static final int NUM_CELL_FACES = 4;

    Point position;
    Point direction;
    int depth;

    public GenState( Point pos, Point dir, int d )
    {
        position = pos;
        direction = dir;
        depth = d;
    }

}


