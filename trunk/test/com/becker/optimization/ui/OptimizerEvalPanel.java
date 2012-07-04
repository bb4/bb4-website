/** Copyright by Barry G. Becker, 2000-2011. Licensed under MIT License: http://www.opensource.org/licenses/MIT  */
package com.becker.optimization.ui;

import com.becker.optimization.OptimizationListener;
import com.becker.optimization.parameter.ParameterArray;

import javax.swing.*;
import java.awt.*;
import java.awt.geom.Point2D;

/**
 * Panel for showing the optimization visually.
 * @@ add pan and zoom capability based on right click menu.
 * @author Barry Becker
 */
public class OptimizerEvalPanel extends JPanel implements OptimizationListener {

    private static final int EDGE_SIZE = 1000;
    static final Dimension SIZE = new Dimension(EDGE_SIZE, EDGE_SIZE);
    private static final Color BG_COLOR = new Color(240, 241, 242);
    private PointsList pointsList;
    private PointsListRenderer renderer;

    /**
     * Constructor
     * @param solutionPosition where we hope to wind up at.
     */
    public OptimizerEvalPanel(Point2D.Double solutionPosition) {
        pointsList = new PointsList(solutionPosition, EDGE_SIZE);
        this.setPreferredSize(  SIZE );
        renderer = new PointsListRenderer();
    }

    /**
     * Called whenever the optimizer strategy moves incrementally toward the solution.
     * @param params we assume there is only two.
     */
    public void optimizerChanged(ParameterArray params) {
        pointsList.addPoint(params);
    }

    @Override
    public void paintComponent(Graphics  g) {

        super.paintComponents( g );

        Graphics2D g2 = (Graphics2D) g;
        Dimension dim = this.getSize();
        g2.setColor(BG_COLOR);
        g2.fillRect(0, 0, (int) dim.getWidth(), (int) dim.getHeight());

        renderer.render(pointsList, g2);
    }
}