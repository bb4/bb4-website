package com.becker.puzzle.maze;

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

    // walls in the positive x, y directions.
    // when these are true, we render walls
    public boolean eastWall = false;
    public boolean southWall = false;

    // the 4 possible paths (e,w,n,s)
    // we show 0 or 2 of them at any given time in a cell when solving the maze
    public boolean eastPath = false;
    public boolean westPath = false;
    public boolean northPath = false;
    public boolean southPath = false;

    public int depth = 0;
}


