package com.becker.maze;

/**
 *  A region of space bounded by walls in the maze.
 *
 *  @author Barry Becker
 */
public class MazeCell
{

    // 4 if 2d 6 if 3d 12 if 4d
    public static final int NUM_CELL_FACES = 4;

    public boolean visited = false;

    // walls in the positive x, y directions
    public boolean eastWall = false;
    public boolean southWall = false;

    public int depth = 0;
}


