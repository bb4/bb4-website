package com.becker.puzzle.maze;

import java.util.*;
import java.util.List;
import java.awt.*;

/**
 * @author Barry Becker Date: Jul 29, 2006
 */
public class MazeSolver {


    private MazePanel panel_;

    public MazeSolver(MazePanel panel) {
        panel_ = panel;
    }

    /**
     * do a depth first search (without recursion) of the grid space to determine the solution to the maze.
     * very similar to search (see MazeGenerator), but now we are solving it.
     */
    public void solve()
    {
        MazeModel maze =  panel_.getMaze();
        maze.unvisitAll();
        List stack = new LinkedList();

        Point currentPosition = maze.getStartPosition();
        MazeCell currentCell = maze.getCell(currentPosition);

        // push the initial moves
        MazeModel.pushMoves( currentPosition, new Point( 1, 0 ), 1, stack );
        Point dir;
        int depth;
        boolean solved = false;
        panel_.paintAll();


        while ( !stack.isEmpty() && !solved ) {

            GenState state = (GenState) stack.remove(0);  // pop

            currentPosition = state.getPosition();
            if (currentPosition.equals(maze.getStopPosition()))
              solved = true;

            dir = state.getDirection();
            depth = state.getDepth();
            if ( depth > currentCell.depth )
                currentCell.depth = depth;

            currentCell = maze.getCell(currentPosition);
            Point nextPosition = currentCell.getNextPosition(currentPosition,  dir);

            MazeCell nextCell = maze.getCell(nextPosition);
            boolean eastBlocked = dir.x ==  1 && currentCell.eastWall;
            boolean westBlocked =  dir.x == -1 && nextCell.eastWall;
            boolean southBlocked = dir.y ==  1 && currentCell.southWall;
            boolean northBlocked = dir.y == -1 && nextCell.southWall;

            boolean pathBlocked = eastBlocked || westBlocked || southBlocked || northBlocked;

            if (!pathBlocked)  {
                if ( dir.x == 1 ) {// east
                    currentCell.eastPath = true;
                    nextCell.westPath = true;
                }
                else if ( dir.y == 1 ) { // south
                    currentCell.southPath = true;
                    nextCell.northPath = true;
                }
                else if ( dir.x == -1 ) {  // west
                    currentCell.westPath = true;
                    nextCell.eastPath = true;
                }
                else if ( dir.y == -1 )  { // north
                    currentCell.northPath = true;
                    nextCell.southPath = true;
                }

                nextCell.visited = true;
                currentPosition = nextPosition;

                // now at a new location
                MazeModel.pushMoves( currentPosition, dir, ++depth, stack );
                panel_.paintCell(currentPosition);
            }
        }
        panel_.paintAll();
    }
}
