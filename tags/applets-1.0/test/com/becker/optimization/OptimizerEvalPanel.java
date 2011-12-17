/** Copyright by Barry G. Becker, 2000-2011. Licensed under MIT License: http://www.opensource.org/licenses/MIT  */
package com.becker.optimization;

import com.becker.common.format.FormatUtil;
import com.becker.optimization.parameter.Parameter;
import com.becker.optimization.parameter.ParameterArray;

import javax.swing.*;
import javax.vecmath.Point2d;
import java.awt.*;
import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.util.List;

/**
 * Panel for showing the optimization visually.
 * @@ add zoom in and zoom out capability based on right click menu.
 * @author Barry Becker
 */
public class OptimizerEvalPanel extends JPanel implements OptimizationListener {

    private static final int POINT_RADIUS = 7;
    private static final int EDGE_SIZE = 1000;
    public  static final Dimension SIZE = new Dimension(EDGE_SIZE, EDGE_SIZE);
    public static final Color VECTOR_COLOR = new Color(10, 40, 255);
    private static final Color BG_COLOR = new Color(240, 241, 242);
    private List<Point2d> rawPoints_;
    private List<Point> scaledPoints_;

    private Point2D.Double rawSolutionPosition_;
    private Point solutionPosition_ = null;

    /**
     * Constructor
     * @param solutionPosition where we hope to wind up at.
     */
    public OptimizerEvalPanel(Point2D.Double solutionPosition) {
        rawPoints_ = new ArrayList<Point2d>();
        scaledPoints_ = new ArrayList<Point>(100);
        rawSolutionPosition_ = solutionPosition;

        this.setPreferredSize( SIZE );
    }

    /**
     * Called whenever the optimizer strategy moves incrementally toward the solution.
     * @param params we assume there is only two.
     */
    public void optimizerChanged(ParameterArray params) {

        Parameter xParam = params.get(0);
        Parameter yParam = params.get(1);

        rawPoints_.add(new Point2d(xParam.getValue(), yParam.getValue()));
        scaledPoints_.add(new Point(getScaledValue(xParam), getScaledValue(yParam)));

        if (solutionPosition_ == null) {
            solutionPosition_ = new Point(getScaledValue(xParam, rawSolutionPosition_.getX()),
                                          getScaledValue(xParam, rawSolutionPosition_.getY()));
        }
    }

    private static int getScaledValue(Parameter p) {
        return getScaledValue(p, p.getValue());
    }

    private static int getScaledValue(Parameter p, double value) {
        return (int) (3.0* EDGE_SIZE * (value - p.getMinValue()) / p.getRange()) - EDGE_SIZE;
    }


    @Override
    public void paintComponent(Graphics  g) {

        super.paintComponents( g );

        Graphics2D g2 = (Graphics2D) g;
        Dimension dim = this.getSize();
        g2.setColor(BG_COLOR);
        g2.fillRect(0, 0, (int) dim.getWidth(), (int) dim.getHeight());

        if (solutionPosition_ != null)  {
            drawSolution(g2, solutionPosition_);
        }

        for (int i=1; i< scaledPoints_.size(); i++) {
            drawVector(g2, scaledPoints_.get(i-1), scaledPoints_.get(i), rawPoints_.get(i));
        }
    }

    private void drawVector(Graphics2D g2, Point lastPoint, Point currentPoint, Point2d rawPoint) {

        g2.setColor(VECTOR_COLOR);
        g2.drawLine(currentPoint.x, currentPoint.y, lastPoint.x, lastPoint.y);
        g2.setColor(Color.BLACK);
        g2.drawOval(currentPoint.x,  currentPoint.y, POINT_RADIUS, POINT_RADIUS);

        String label = "(" + FormatUtil.formatNumber(rawPoint.x) + ", " + FormatUtil.formatNumber(rawPoint.y) + ")";
        g2.drawString(label, currentPoint.x - 20, currentPoint.y + 6);
    }

    private void drawSolution(Graphics2D g2, Point position) {
        g2.setColor(Color.RED);
        drawOval(position, POINT_RADIUS - 2, g2);
        drawOval(position, POINT_RADIUS,     g2);
        drawOval(position, POINT_RADIUS + 4, g2);
        drawOval(position, POINT_RADIUS + 10, g2);
    }

    private void drawOval(Point position, int rad, Graphics2D g2) {
         g2.drawOval((int)(position.x - rad / 2.0),  (int)(position.y - rad /2.0),
                           rad, rad);
    }
}
