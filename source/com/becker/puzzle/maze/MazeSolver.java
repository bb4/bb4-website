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
        // stack of paths we did not try yet.
        List<GenState> stack = new LinkedList<GenState>();
        
        // Keep track of our current path. We may need to backtrack along it if we encounter a dead end.
        List<Point> solutionPath = new LinkedList<Point>();

        Point currentPosition = maze.getStartPosition();
        MazeCell currentCell = maze.getCell(currentPosition);

        // push the initial moves
        MazeModel.pushMoves( currentPosition, new Point( 1, 0 ), 1, stack );
        Point dir;
        int depth;
        boolean solved = false;
        panel_.paintAll();

        // while there are still paths to try and we have not yet encountered the finish
        while ( !stack.isEmpty() && !solved ) {

            GenState state = (GenState) stack.remove(0);  // pop

            currentPosition = state.getPosition();
            solutionPath.add(0, currentPosition);
            
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
            else {
                // need to back up to the next path we will try
                GenState lastState = (GenState) stack.get(0); 
                
                Point pos;
                do {
                   pos =  solutionPath.remove(0);
                   MazeCell cell = maze.getCell(pos);
                   cell.clearPath();
                } while ( pos != lastState.getPosition());
            }
        }
        panel_.paintAll();
    }
}
