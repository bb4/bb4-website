/** Copyright by Barry G. Becker, 2000-2011. Licensed under MIT License: http://www.opensource.org/licenses/MIT  */
package com.barrybecker4.puzzle.maze.ui;

import com.barrybecker4.common.concurrency.ThreadUtil;
import com.barrybecker4.common.geometry.Location;
import com.barrybecker4.common.math.MathUtil;
import com.barrybecker4.puzzle.maze.MazeGenerator;
import com.barrybecker4.puzzle.maze.MazeSolver;
import com.barrybecker4.puzzle.maze.model.MazeModel;

import javax.swing.JComponent;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;

/**
 * This panel is responsible for drawing the Maze (see MazeModel).
 * @author Barry Becker
 */
public class MazePanel extends JComponent {

    /** represents the maze that we need to render. */
    private MazeModel maze_;
    private int animationSpeed_;
    private int cellSize;
    private MazeRenderer renderer;

    public MazePanel() {
        renderer = new MazeRenderer();
    }

    public MazeModel getMaze() {
        return maze_;
    }

    public void setAnimationSpeed(int animSpeed) {
        animationSpeed_ = animSpeed;
    }

    public int getAnimationSpeed() {
        return animationSpeed_;
    }

    public void setThickness(int thickness) {

        Dimension dim = getSize();
        if (dim.width <= 0 || dim.height < 0)
            return;

        cellSize = thickness;
        renderer.setCellSize(cellSize);
        int w = dim.width / thickness;
        int h = dim.height / thickness;

        maze_ = new MazeModel(w, h);
    }

    /**
     * Generate the maze.
     */
    public void generate(double forwardProb, double leftProb, double rightProb) {
        MazeGenerator generator = new MazeGenerator(this);
        generator.generate(forwardProb, leftProb, rightProb);
    }

    /**
     * solve the maze.
     */
    public void solve() {
        MazeSolver solver = new MazeSolver(this);
        solver.solve();
    }

    /**
     * paint the whole window right now!
     */
    public void paintAll() {
        Dimension d = this.getSize();
        this.paintImmediately( 0, 0, (int) d.getWidth(), (int) d.getHeight() );
    }

    /**
     * paint just the region around a single cell for performance.
     * @param pt
     */
    public void paintCell(Location pt) {
        int csized2 = (cellSize/2)+2;
        int xpos = (pt.getX() * cellSize);
        int ypos = (pt.getY() * cellSize);
        if (animationSpeed_ <= 10)  {
            // this paints just the cell immediately (sorta slow)
            this.paintImmediately( xpos-csized2, ypos-csized2, (2*cellSize), (2*cellSize));
            if (animationSpeed_ < 9) {
                ThreadUtil.sleep(200 / animationSpeed_ - 15);
            }
        }
        else  {
            if (MathUtil.RANDOM.nextDouble() < (8.0/(double)(animationSpeed_ * animationSpeed_)))  {
                paintAll();
            }
            else {
                this.repaint(xpos-csized2, ypos-csized2, (2*cellSize), (2*cellSize));
            }
        }
    }

    /**
     * Render the Environment on the screen.
     */
    @Override
    public void paintComponent( Graphics g ) {

        super.paintComponent( g );
        renderer.render((Graphics2D)g, maze_);
    }
}