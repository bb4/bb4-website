package com.becker.optimization;

import javax.swing.*;
import java.awt.geom.Point2D;

/**
 * Show iteration steps to 2 d solution.
 *
 * @author Barry Becker Date: Jun 25, 2006
 */
public class OptimizerEvalFrame extends JFrame {

    /**
     *
     * @param optimizer  to show iterations of
     * @param solutionPosition  may be null if unknown.
     */
    public OptimizerEvalFrame(Optimizer optimizer, Point2D.Double solutionPosition) {

        this.setTitle("Optimization Animation");
        this.setSize(OptimizerEvalPanel.SIZE);

        OptimizerEvalPanel evalPanel = new OptimizerEvalPanel(solutionPosition);
        optimizer.setListener(evalPanel);

        this.getContentPane().add(evalPanel);
        this.pack();

    }
}
