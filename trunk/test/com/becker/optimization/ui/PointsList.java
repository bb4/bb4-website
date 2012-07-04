/** Copyright by Barry G. Becker, 2000-2011. Licensed under MIT License: http://www.opensource.org/licenses/MIT  */
package com.becker.optimization.ui;

import com.becker.optimization.parameter.ParameterArray;
import com.becker.optimization.parameter.types.Parameter;

import javax.swing.*;
import javax.vecmath.Point2d;
import java.awt.*;
import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.util.List;

/**
 * Panel for showing the optimization visually.
 * @@ add pan and zoom capability based on right click menu.
 * @author Barry Becker
 */
public class PointsList  {

    private List<Point2d> rawPoints_;
    private List<Point> scaledPoints_;

    private Point2D.Double rawSolutionPosition_;
    private Point solutionPosition_ = null;
    private int edgeSize;

    /**
     * Constructor
     * @param solutionPosition where we hope to wind up at.
     */
    public PointsList(Point2D.Double solutionPosition, int edgeSize) {
        rawPoints_ = new ArrayList<Point2d>();
        scaledPoints_ = new ArrayList<Point>(100);
        rawSolutionPosition_ = solutionPosition;
        this.edgeSize = edgeSize;
    }

    public Point getSolutionPosition() {
        return solutionPosition_;
    }

    public Point2d getRawPoint(int i) {
        return rawPoints_.get(i);
    }

    public Point getScaledPoint(int i) {
        return scaledPoints_.get(i);
    }

    public int size() {
        return rawPoints_.size();
    }

    /**
     * Called whenever the optimizer strategy moves incrementally toward the solution.
     * @param params we assume there is only two.
     */
    public void addPoint(ParameterArray params) {

        Parameter xParam = params.get(0);
        Parameter yParam = params.get(1);

        rawPoints_.add(new Point2d(xParam.getValue(), yParam.getValue()));
        scaledPoints_.add(new Point(getScaledValue(xParam), getScaledValue(yParam)));

        if (solutionPosition_ == null) {
            solutionPosition_ = new Point(getScaledValue(xParam, rawSolutionPosition_.getX()),
                    getScaledValue(xParam, rawSolutionPosition_.getY()));
        }
    }

    private int getScaledValue(Parameter p) {
        return getScaledValue(p, p.getValue());
    }

    private  int getScaledValue(Parameter p, double value) {
        return (int) (3.0* edgeSize * (value - p.getMinValue()) / p.getRange()) - edgeSize;
    }
}