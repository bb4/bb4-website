package com.becker.ui.renderers;

import com.becker.common.format.DefaultNumberFormatter;
import com.becker.common.format.INumberFormatter;
import com.becker.common.math.Range;
import com.becker.common.math.function.Function;
import com.becker.common.util.Util;

import java.awt.*;

/**
 * This class draws a specified function.
 *
 * @author Barry Becker
 */
public class FunctionRenderer {

    /** y values for every point on the x axis. */
    private Function function_;

    private static final Color BACKGROUND_COLOR = new Color(255, 255, 255);
    private static final Color LINE_COLOR = new Color(0, 0, 0);

    private static final int MARGIN = 24;
    private static final int TICK_LENGTH = 4;

    private int width_;
    private int height_;

    private INumberFormatter formatter_ = new DefaultNumberFormatter();

    private static final int DEFAULT_LABEL_WIDTH = 30;
    private int maxLabelWidth_ = DEFAULT_LABEL_WIDTH;


    /**
     * Constructor that assumes no scaling.
     * @param func the function to plot.
     */
    public FunctionRenderer(Function func)
    {
        function_ = func;
    }


    public void setSize(int width, int height) {
        width_ = width;
        height_ = height;
        int maxNumLabels_ = width_ / maxLabelWidth_;
    }

    /**
     * Provides customer formatting for the x axis values.
     * @param formatter a way to format the x axis values
     */
    public void setXFormatter(INumberFormatter formatter) {
        formatter_ = formatter;
    }

    /**
     * The larger this is, the fewere equally spaced x labels.
     * @param maxLabelWidth   max width of x labels.
     */
    public void setMaxLabelWidth(int maxLabelWidth) {
        maxLabelWidth_ = maxLabelWidth;
    }

    /** draw the cartesian function */
    public void paint(Graphics g) {

        if (g == null)  return;
        Graphics2D g2 = (Graphics2D) g;

        Range yRange = getRange();
        double maxHeight = getRange().getExtent();
        double scale = (height_ - 2.0 * MARGIN) / maxHeight;

        clearBackground(g2);

        float xpos = MARGIN;
        int numPoints = getNumXPoints() ;

        g2.setColor(LINE_COLOR);
        for (int i = 0; i < numPoints;  i++) {
            double x = (double)i/numPoints;
            drawLine(g2, scale, MARGIN + i, function_.getValue(x));
        }
        drawDecoration(g2, yRange);
    }

    private int getNumXPoints() {
        return width_ - MARGIN;
    }

    private void drawDecoration(Graphics2D g2, Range yRange) {

        // left y axis
        g2.drawLine(MARGIN-1, height_ - MARGIN,
                    MARGIN-1, MARGIN);
        // x axis
        g2.drawLine(MARGIN-1,         height_- MARGIN -1,
                    MARGIN-1 + width_, height_ - MARGIN -1);

        g2.drawString("max = " + Util.formatNumber(yRange.getMax()), MARGIN/3, MARGIN -2);
        g2.drawString("min = " + Util.formatNumber(yRange.getMin()), MARGIN/3, height_ - MARGIN );
    }

    /**
     * draw a point
     */
    private void drawLine(Graphics2D g2, double scale,  float xpos, double ypos) {
        double h = (scale * ypos);
        int top = (int)(height_ - h - MARGIN);

        g2.fillOval((int)xpos, top, 3, 3);
    }

    private void clearBackground(Graphics2D g2) {
        g2.setColor(BACKGROUND_COLOR);
        g2.fillRect( 0, 0, width_, height_ );
    }

    private Range getRange() {

        Range range = new Range();
        int numPoints = getNumXPoints() ;
        for (int i = 0; i < numPoints;  i++) {
            double x = (double)i/numPoints;
            range.add(function_.getValue(x));
        }
        return range;
    }
}