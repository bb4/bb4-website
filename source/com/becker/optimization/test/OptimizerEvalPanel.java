package com.becker.optimization.test;

import com.becker.optimization.parameter.ParameterArray;
import com.becker.optimization.parameter.Parameter;
import com.becker.optimization.*;

import javax.swing.*;
import java.awt.*;
import java.awt.geom.*;
import java.util.*;
import java.util.List;

/**
 * @@ add zoom in and zoom out capability based on right click menu.
 * @author Barry Becker Date: Jun 25, 2006
 */
public class OptimizerEvalPanel extends JPanel implements OptimizationListener {

    private static final int POINT_RADIUS = 7;
    private static final int EDGE_SIZE = 900;
    public  static final Dimension SIZE = new Dimension(EDGE_SIZE, EDGE_SIZE);
    private static final Color BG_COLOR = new Color(240, 241, 242);
    private List points_;


    private Point2D.Double rawSolutionPosition_;
    private Point solutionPosition_ = null;


    public OptimizerEvalPanel(Point2D.Double solutionPosition) {
        points_ = new ArrayList(100);
        rawSolutionPosition_ = solutionPosition;

        this.setPreferredSize( SIZE );
    }

    public void optimizerChanged(ParameterArray params) {
        // To change body of implemented methods use File | Settings | File Templates.
        // assume there are 2 parameters.

        Parameter xParam = params.get(0);
        Parameter yParam = params.get(1);

        points_.add(new Point(getScaledValue(xParam), getScaledValue(yParam)));

        if (solutionPosition_ == null) {
            solutionPosition_ = new Point(getScaledValue(xParam, rawSolutionPosition_.getX()),
                                          getScaledValue(xParam, rawSolutionPosition_.getY()));
        }
    }

    private static int getScaledValue(Parameter p) {
        return getScaledValue(p, p.getValue());
    }

    private static int getScaledValue(Parameter p, double v) {
            return (int) (1.4* EDGE_SIZE * (v - p.getMinValue()) / p.getRange());
        }


    public void paintComponent(Graphics  g) {

        super.paintComponents( g );

        Graphics2D g2 = (Graphics2D) g;
        Dimension dim = this.getSize();
        g2.setColor(BG_COLOR);
        g2.fillRect(0, 0, (int) dim.getWidth(), (int) dim.getHeight());


        g2.setColor(Color.RED);
        if (solutionPosition_ != null)  {
            drawOval(POINT_RADIUS - 2, g2);
            drawOval(POINT_RADIUS,     g2);
            drawOval(POINT_RADIUS + 4, g2);
            drawOval(POINT_RADIUS + 10, g2);
        }

        for (int i=1; i<points_.size(); i++) {
            Point currPoint = (Point) points_.get(i);
            Point lastPoint = (Point) points_.get(i-1);
            g2.setColor(Color.BLUE);
            g2.drawLine(currPoint.x,  currPoint.y, lastPoint.x, lastPoint.y);
            g2.setColor(Color.BLACK);
            g2.drawOval(currPoint.x,  currPoint.y, POINT_RADIUS, POINT_RADIUS);
        }
    }

    private void drawOval(int rad, Graphics2D g2) {
         g2.drawOval((int)(solutionPosition_.x - rad / 2.0),  (int)(solutionPosition_.y - rad /2.0),
                           rad, rad);
    }
}
