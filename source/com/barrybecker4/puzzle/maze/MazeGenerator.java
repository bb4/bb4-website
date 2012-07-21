/** Copyright by Barry G. Becker, 2000-2011. Licensed under MIT License: http://www.opensource.org/licenses/MIT  */
package com.barrybecker4.puzzle.maze;

import com.barrybecker4.puzzle.maze.model.Direction;
import com.barrybecker4.puzzle.maze.model.GenState;
import com.barrybecker4.puzzle.maze.model.MazeCell;
import com.barrybecker4.puzzle.maze.model.MazeModel;
import com.barrybecker4.puzzle.maze.ui.MazePanel;

import java.awt.*;
import java.util.LinkedList;
import java.util.List;

/**
 *  Program to automatically generate a Maze.
 *  Motivation: Get my son, Brian, to excel at Kumon by trying these mazes with a pencil.
 *  this is the global space containing all the cells, walls, and particles.
 *  Assumes an M*N grid of cells.
 *  X axis increases to the left.
 *  Y axis increases downwards to be consistent with java graphics.
 *
 *  @author Barry Becker
 */
public class MazeGenerator {

    private MazeModel maze_;
    private MazePanel panel_;

    /** put the stop point at the maximum search depth. */
    private int maxDepth_ = 0;


    public MazeGenerator(MazePanel panel) {
        this(panel.getMaze(), panel);
    }

    private MazeGenerator(MazeModel maze, MazePanel panel) {
        maze_ = maze;
        panel_ = panel;
    }

    /**
     * generate the maze
     */
    public void generate() {
        generate(Direction.FORWARD.getProbability(),
                 Direction.LEFT.getProbability(),
                 Direction.RIGHT.getProbability() );
    }

    /**
     * generate the maze.
     */
    public void generate(double forwardProb, double leftProb, double rightProb ) {

        maxDepth_ = 0;
        Direction.FORWARD.setProbability(forwardProb);
        Direction.LEFT.setProbability(leftProb);
        Direction.RIGHT.setProbability(rightProb);

        search();
        panel_.repaint();
    }

    /**
     * Do a depth first search (without recursion) of the grid space to determine the graph.
     * Used to use a recursive algorithm but it was slower and would give stack overflow
     * exceptions even for moderately sized mazes.
     */
    public void search() {
        List<GenState> stack = new LinkedList<GenState>();

        Point currentPosition = maze_.getStartPosition();
        MazeCell currentCell = maze_.getCell(currentPosition);

        // push the initial moves
        MazeModel.pushMoves( currentPosition, new Point( 1, 0 ), 1, stack );
        Point dir;
        int depth;

        while ( !stack.isEmpty() ) {
            boolean moved = false;

            do {
                GenState state = stack.remove(0);  // pop

                currentPosition = state.getPosition();
                dir = state.getDirection();
                depth = state.getDepth();

                if ( depth > maxDepth_ ) {
                    maxDepth_ = depth;
                    maze_.setStopPosition(currentPosition);
                }
                if ( depth > currentCell.depth )
                    currentCell.depth = depth;

                currentCell = maze_.getCell(currentPosition);
                Point nextPosition = currentCell.getNextPosition(currentPosition, dir);

                MazeCell nextCell = maze_.getCell(nextPosition);

                if ( !nextCell.visited ) {
                    moved = true;
                    nextCell.visited = true;
                    currentPosition = nextPosition;
                }
                else {
                    addWall(currentCell, dir, nextCell);
                }
            } while ( !moved && !stack.isEmpty() );

            refresh();
            // now at a new location
            if ( moved )
                MazeModel.pushMoves( currentPosition, dir, ++depth, stack );
        }
    }

    /** this can be really slow if you do a refresh every time */
    private void refresh() {
        if (Math.random() < 4.0/(Math.pow(panel_.getAnimationSpeed(), 2) + 1)) {
            panel_.paintAll();
        }
    }

    private void addWall(MazeCell currentCell, Point dir, MazeCell nextCell) {
        // add a wall
        if ( dir.x == 1 ) // east
            currentCell.eastWall = true;
        else if ( dir.y == 1 ) // south
            currentCell.southWall = true;
        else if ( dir.x == -1 )  // west
            nextCell.eastWall = true;
        else if ( dir.y == -1 )  // north
            nextCell.southWall = true;
    }

}
