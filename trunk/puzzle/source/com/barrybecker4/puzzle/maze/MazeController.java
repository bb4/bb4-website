// Copyright by Barry G. Becker, 2013. Licensed under MIT License: http://www.opensource.org/licenses/MIT
package com.barrybecker4.puzzle.maze;


import com.barrybecker4.common.concurrency.Worker;
import com.barrybecker4.puzzle.maze.ui.MazePanel;

import java.awt.Cursor;

/**
 * Controller part of the MVC pattern.
 *
 * @author Barry Becker
 */
public final class MazeController {

    private MazePanel mazePanel;
    private Worker generateWorker;
    private MazeGenerator generator;

    /**
     * Constructor.
     */
    public MazeController(MazePanel panel) {
        mazePanel = panel;
    }


    /**
     * regenerate the maze based on the current UI parameter settings
     * and current size of the panel.
     */
    public void regenerate(final int thickness, final int animationSpeed,
                           final double forwardP, final double leftP, final double rightP) {

        if (generator != null)
        {
            generator.interrupt();
            generateWorker.get(); // blocks until done
        }

        generateWorker = new Worker() {

            @Override
            public Object construct() {
                generator = new MazeGenerator(mazePanel);
                mazePanel.setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));

                double sum = forwardP + leftP + rightP;
                mazePanel.setAnimationSpeed(animationSpeed);
                mazePanel.setThickness(thickness);


                generator.generate(forwardP / sum, leftP / sum, rightP / sum);
                return true;
            }

            @Override
            public void finished() {
                mazePanel.repaint();
                mazePanel.setCursor(Cursor.getDefaultCursor());
            }
        };
        generateWorker.start();
    }


    public void solve(final int animationSpeed) {

        if (generateWorker.isWorking()) return;

        Worker worker = new Worker() {

            @Override
            public Object construct() {

                mazePanel.setAnimationSpeed(animationSpeed);
                MazeSolver solver = new MazeSolver(mazePanel);
                solver.solve();
                return true;
            }

            @Override
            public void finished() {
                mazePanel.repaint();
            }
        };
        worker.start();
    }
}
