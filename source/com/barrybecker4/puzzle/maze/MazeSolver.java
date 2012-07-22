/** Copyright by Barry G. Becker, 2000-2011. Licensed under MIT License: http://www.opensource.org/licenses/MIT  */
package com.barrybecker4.puzzle.maze;

import com.barrybecker4.common.geometry.IntLocation;
import com.barrybecker4.puzzle.maze.model.GenState;
import com.barrybecker4.puzzle.maze.model.MazeCell;
import com.barrybecker4.puzzle.maze.model.MazeModel;
import com.barrybecker4.puzzle.maze.model.StateStack;
import com.barrybecker4.puzzle.maze.ui.MazePanel;

import java.util.LinkedList;
import java.util.List;

/**
 * @author Barry Becker
 */
public class MazeSolver {

    private MazePanel panel_;
    private MazeModel maze;
    private StateStack stack;

    /** Constructor */
    public MazeSolver(MazePanel panel) {
        panel_ = panel;
        maze = panel_.getMaze();
        stack = new StateStack();
    }

    /**
     * do a depth first search (without recursion) of the grid space to determine the solution to the maze.
     * very similar to search (see MazeGenerator), but now we are solving it.
     */
    public void solve() {

        maze.unvisitAll();
        stack.clear();

        // Keep track of our current path. We may need to backtrack along it if we encounter a dead end.
        List<IntLocation> solutionPath = new LinkedList<IntLocation>();

        IntLocation currentPosition = maze.getStartPosition();
        MazeCell currentCell = maze.getCell(currentPosition);

        // push the initial moves
        stack.pushMoves( currentPosition, new IntLocation(0, 1), 1);
        IntLocation dir;
        int depth;
        boolean solved = false;
        panel_.paintAll();

        // while there are still paths to try and we have not yet encountered the finish
        while ( !stack.isEmpty() && !solved ) {

            GenState state = stack.remove(0);  // pop

            currentPosition = state.getPosition();
            solutionPath.add(0, currentPosition);

            if (currentPosition.equals(maze.getStopPosition()))  {
                solved = true;
            }

            dir = state.getDirection();
            depth = state.getDepth();
            if ( depth > currentCell.getDepth() ) {
                currentCell.setDepth(depth);
            }

            currentCell = maze.getCell(currentPosition);
            IntLocation nextPosition = currentCell.getNextPosition(currentPosition,  dir);

            search(solutionPath, currentCell, dir, depth, nextPosition);
        }
        panel_.paintAll();
    }

    private void search(List<IntLocation> solutionPath, MazeCell currentCell,
                        IntLocation dir, int depth, IntLocation nextPosition) {
        MazeCell nextCell = maze.getCell(nextPosition);
        boolean eastBlocked = dir.getX() ==  1 && currentCell.eastWall;
        boolean westBlocked =  dir.getX() == -1 && nextCell.eastWall;
        boolean southBlocked = dir.getY() ==  1 && currentCell.southWall;
        boolean northBlocked = dir.getY() == -1 && nextCell.southWall;

        boolean pathBlocked = eastBlocked || westBlocked || southBlocked || northBlocked;

        if (!pathBlocked)  {
            advanceToNextCell(currentCell, dir, depth, nextPosition, nextCell);
        }
        else {
            backTrack(solutionPath);
        }
    }


    private void advanceToNextCell(MazeCell currentCell, IntLocation dir, int depth,
                                   IntLocation nextPosition, MazeCell nextCell) {
        IntLocation currentPosition;
        if ( dir.getX() == 1 ) {// east
            currentCell.eastPath = true;
            nextCell.westPath = true;
        }
        else if ( dir.getY() == 1 ) { // south
            currentCell.southPath = true;
            nextCell.northPath = true;
        }
        else if ( dir.getX() == -1 ) {  // west
            currentCell.westPath = true;
            nextCell.eastPath = true;
        }
        else if ( dir.getY() == -1 )  { // north
            currentCell.northPath = true;
            nextCell.southPath = true;
        }

        nextCell.visited = true;
        currentPosition = nextPosition;

        // now at a new location
        stack.pushMoves(currentPosition, dir, ++depth);
        panel_.paintCell(currentPosition);
    }


    private void backTrack(List<IntLocation> solutionPath) {
        // need to back up to the next path we will try
        GenState lastState = stack.get(0);

        IntLocation pos;
        do {
            pos =  solutionPath.remove(0);
            MazeCell cell = maze.getCell(pos);
            cell.clearPath();
        } while ( pos != lastState.getPosition());
    }
}
