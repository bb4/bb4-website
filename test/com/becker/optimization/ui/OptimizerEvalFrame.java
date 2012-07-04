/** Copyright by Barry G. Becker, 2000-2011. Licensed under MIT License: http://www.opensource.org/licenses/MIT  */
package com.becker.optimization.ui;

import com.becker.optimization.Optimizer;

import javax.swing.*;
import java.awt.geom.Point2D;

/**
 * Show iteration steps to 2 d solution.
 *
 * @author Barry Becker
 */
public class OptimizerEvalFrame extends JFrame {

    /**
     * Constructor
     * @param optimizer to show iterations of
     * @param solutionPosition  may be null if unknown.
     */
    public OptimizerEvalFrame(Optimizer optimizer, Point2D.Double solutionPosition) {

        this.setTitle("Optimization Animation of " + optimizer.getOptimizee().getName());
        this.setSize(OptimizerEvalPanel.SIZE);

        OptimizerEvalPanel evalPanel = new OptimizerEvalPanel(solutionPosition);
        optimizer.setListener(evalPanel);

        this.getContentPane().add(evalPanel);
        this.pack();
    }
}
