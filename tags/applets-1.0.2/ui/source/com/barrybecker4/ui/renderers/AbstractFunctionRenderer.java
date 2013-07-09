/** Copyright by Barry G. Becker, 2000-2011. Licensed under MIT License: http://www.opensource.org/licenses/MIT  */
package com.barrybecker4.ui.renderers;

import com.barrybecker4.common.format.DefaultNumberFormatter;
import com.barrybecker4.common.format.FormatUtil;
import com.barrybecker4.common.format.INumberFormatter;
import com.barrybecker4.common.math.Range;

import java.awt.*;

/**
 * This class draws a specified function.
 *
 * @author Barry Becker
 */
public abstract class AbstractFunctionRenderer {

    private static final Color BACKGROUND_COLOR = new Color(255, 255, 255);
    private static final Color LABEL_COLOR = Color.BLACK;

    protected static final int MARGIN = 24;

    protected int width_;
    protected int height_;

    private int xOffset_ = 0;
    private int yOffset_ = 0;

    protected INumberFormatter formatter_ = new DefaultNumberFormatter();

    protected static final int DEFAULT_LABEL_WIDTH = 30;
    protected int maxLabelWidth_ = DEFAULT_LABEL_WIDTH;


    public void setSize(int width, int height) {
        width_ = width;
        height_ = height;
    }

    public void setPosition(int xOffset, int yOffset) {
        xOffset_ = xOffset;
        yOffset_ = yOffset;
    }

    /**
     * Provides customer formatting for the x axis values.
     * @param formatter a way to format the x axis values
     */
    public void setXFormatter(INumberFormatter formatter) {
        formatter_ = formatter;
    }

    /**
     * The larger this is, the fewer equally spaced x labels.
     * @param maxLabelWidth   max width of x labels.
     */
    public void setMaxLabelWidth(int maxLabelWidth) {
        maxLabelWidth_ = maxLabelWidth;
    }

    /** draw the cartesian function */
    public abstract void paint(Graphics g);

    protected int getNumXPoints() {
        return width_ - MARGIN;
    }

    protected void drawDecoration(Graphics2D g2, Range yRange) {

        g2.setColor(LABEL_COLOR);
        g2.drawRect(xOffset_, yOffset_, width_, height_);

        // left y axis
        g2.drawLine(xOffset_ + MARGIN-1, yOffset_ + height_ - MARGIN,
                    xOffset_ + MARGIN-1, yOffset_ + MARGIN);
        // x axis
        g2.drawLine(xOffset_ + MARGIN-1,        yOffset_ + height_- MARGIN -1,
                    xOffset_ + MARGIN-1 + width_, yOffset_ + height_ - MARGIN -1);

        g2.drawString("max = " + FormatUtil.formatNumber(yRange.getMax()), // NON-NLS
                xOffset_ + MARGIN/3, yOffset_ + MARGIN -2);
        g2.drawString("min = " + FormatUtil.formatNumber(yRange.getMin()), // NON-NLS
                xOffset_ + MARGIN/3, yOffset_ + height_ - MARGIN );
    }

    /**
     * draw line composed of points
     */
    protected void drawLine(Graphics2D g2, double scale,  float xpos, double ypos) {
        double h = (scale * ypos);
        int top = (int)(height_ - h - MARGIN);

        g2.fillOval(xOffset_ + (int)xpos, yOffset_ + top, 3, 3);
    }

    /**
     * draw line composed of connected line segments
     */
    protected void drawConnectedLine(Graphics2D g2, double scale,  float xpos, double ypos, double lastX, double lastY) {
        double h = (scale * ypos);
        int top = (int)(height_ - h - MARGIN);

        double lasth = (scale * lastY);
        int lastTop = (int)(height_ - lasth - MARGIN);

        g2.drawLine(xOffset_ + (int)xpos, yOffset_ + top, xOffset_ + (int) lastX, yOffset_ + lastTop);
    }

    protected void clearBackground(Graphics2D g2) {
        g2.setColor(BACKGROUND_COLOR);
        g2.fillRect( xOffset_, yOffset_, width_, height_ );
    }

    protected abstract Range getRange();
}