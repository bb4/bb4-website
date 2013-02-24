/** Copyright by Barry G. Becker, 2000-2011. Licensed under MIT License: http://www.opensource.org/licenses/MIT  */
package com.barrybecker4.ui.renderers;

import com.barrybecker4.common.math.Range;
import com.barrybecker4.common.math.function.Function;

import java.awt.*;
import java.util.List;

/**
 * This class draws a specified function.
 *
 * @author Barry Becker
 */
public class MultipleFunctionRenderer extends AbstractFunctionRenderer {

    /** Functions that provide y values for every point on the x axis. */
    private List<Function> functions_;
    private List<Color> lineColors_;

    /**
     * Constructor that assumes no scaling.
     * @param functions the functions to plot.
     * @param lineColors line colors corresponding to functions
     */
    public MultipleFunctionRenderer(List<Function> functions, List<Color> lineColors) {
        functions_ = functions;
        lineColors_ = lineColors;
        assert functions_.size() == lineColors_.size() :
                "There must be as many line colors as functions";
    }

    /** draw the cartesian functions */
    @Override
    public void paint(Graphics g) {

        if (g == null)  return;
        Graphics2D g2 = (Graphics2D) g;

        Range yRange = getRange();
        double maxHeight = yRange.getMax();

        double scale = (height_ - 2.0 * MARGIN) / maxHeight;

        clearBackground(g2);

        int numPoints = getNumXPoints() ;

        for (int f = 0; f < functions_.size(); f++) {

            g2.setColor(lineColors_.get(f));
            double lastY = 0.0;

            for (int i = 0; i < numPoints;  i++) {
                double x = (double)i/numPoints;
                double y = functions_.get(f).getValue(x);
                drawConnectedLine(g2, scale, MARGIN + i, y, MARGIN + i - 1, lastY);
                lastY = y;

            }
        }
        drawDecoration(g2, yRange);
    }


    @Override
    protected Range getRange() {

        Range range = new Range();
        int numPoints = getNumXPoints() ;

        for (int i = 0; i < numPoints;  i++) {
            double x = (double)i/numPoints;
            for (Function func : functions_) {
                range.add(func.getValue(x));
            }
        }
        return range;
    }
}