/** Copyright by Barry G. Becker, 2000-2011. Licensed under MIT License: http://www.opensource.org/licenses/MIT  */
package com.barrybecker4.puzzle.maze;


import java.awt.*;
import java.util.List;

/**
 * The model part of the model view controller pattern for the maze.
 *
 * @author Barry Becker Date: Jul 29, 2006
 */
public class MazeModel {

    private int width_;
    private int height_;

    // the grid of cells that make up the maze paths
    // in x,y (col, row) order
    private MazeCell[][] grid_;

    // the start and stop positions
    private Point startPosition_;
    private Point stopPosition_;

    /**
     * Constructs a maze with specified width and height.
     */
    public MazeModel(int width, int height)  {
        width_ = width;
        height_ = height;

        grid_ = new MazeCell[width][height];

        for ( int j = 0; j < height_; j++ ) {
            for ( int i = 0; i < width_; i++ ) {
                grid_[i][j] = new MazeCell();
            }
        }

        // a border around the whole maze
        setConstraints();

        // randomize this?
        startPosition_ = new Point( 2, 2 );
    }

    public Point getStartPosition() {
        return startPosition_;
    }

    public void setStopPosition(Point stopPos) {
        stopPosition_ = stopPos;
    }

    public Point getStopPosition() {
        return stopPosition_;
    }

    public MazeCell getCell(int x, int y) {

        return grid_[Math.min(x, width_-1)][Math.min(y, height_-1)];
    }

    public MazeCell getCell(Point p) {
        return grid_[p.x][p.y];
    }

    public int getNumCells() {
        return width_ * height_;
    }

    public int getWidth() {
        return width_;
    }
    public int getHeight() {
        return height_;
    }



    /**
     * From currentPosition try moving in each direction in a random order.
     */
    public static void pushMoves( Point currentPosition, Point currentDir, int depth, List<GenState> stack )
    {
        // assigning different probabilities to the order in which we check these directions
        // can give interesting effects.
        List<Direction> directions = Direction.getShuffledDirections();

        // check all the directions except the one we came from
        for ( int i = 0; i < 3; i++ ) {
            Direction direction = directions.get(i);
            Point dir = direction.apply(currentDir);
            stack.add(0, new GenState( currentPosition, dir, depth ) );
        }
    }

    /**
     * mark all the cells unvisited.
     */
    public void unvisitAll()
    {
        // return everything to unvisited
        for (int j = 0; j < height_; j++ ) {
            for (int i = 0; i < width_; i++ ) {
                //g.drawLine(OFFSET, ypos+OFFSET, rightEdgePos+OFFSET, ypos+OFFSET);
                MazeCell c = grid_[i][j];
                c.clear();
            }
        }
    }

    /**
     *  set OBSTACLEs, walls
     *  mark all the cells around the periphery as visited so there will be walls generated there
     */
    private void setConstraints()
    {
        int i, j;
        MazeCell c;

        // right and left
        for ( j = 0; j < height_; j++ ) {
            // left
            c = grid_[0][j];
            c.visited = true;
            // right
            c = grid_[width_ - 1][j];
            c.visited = true;
        }

        // top and bottom
        for ( i = 0; i < width_; i++ ) {
            // bottom
            c = grid_[i][0];
            c.visited = true;
            // top
            c = grid_[i][height_ - 1];
            c.visited = true;
        }
    }

}
