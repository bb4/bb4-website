/** Copyright by Barry G. Becker, 2000-2011. Licensed under MIT License: http://www.opensource.org/licenses/MIT  */
package com.barrybecker4.ui.renderers;

import com.barrybecker4.common.math.Range;
import com.barrybecker4.common.math.function.Function;

import java.awt.*;

/**
 * This class draws a specified function.
 *
 * @author Barry Becker
 */
public class SingleFunctionRenderer extends AbstractFunctionRenderer {

    /** y values for every point on the x axis. */
    private Function function_;

    private static final Color LINE_COLOR = new Color(0, 0, 0);


    /**
     * Constructor that assumes no scaling.
     * @param func the function to plot.
     */
    public SingleFunctionRenderer(Function func) {
        function_ = func;
    }


    /** draw the cartesian function */
    @Override
    public void paint(Graphics g) {

        if (g == null)  return;
        Graphics2D g2 = (Graphics2D) g;

        Range yRange = getRange();
        double maxHeight = yRange.getExtent();
        double scale = (height_ - 2.0 * MARGIN) / maxHeight;

        clearBackground(g2);

        int numPoints = getNumXPoints() ;

        g2.setColor(LINE_COLOR);
        for (int i = 0; i < numPoints;  i++) {
            double x = (double)i/numPoints;
            drawLine(g2, scale, MARGIN + i, function_.getValue(x));
        }
        drawDecoration(g2, yRange);
    }


    @Override
    protected Range getRange() {

        Range range = new Range();
        int numPoints = getNumXPoints() ;
        for (int i = 0; i < numPoints;  i++) {
            double x = (double)i/numPoints;
            range.add(function_.getValue(x));
        }
        return range;
    }
}