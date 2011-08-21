package com.becker.ui.renderers;

import com.becker.common.format.DefaultNumberFormatter;
import com.becker.common.format.FormatUtil;
import com.becker.common.format.INumberFormatter;
import com.becker.common.math.Range;

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

    protected INumberFormatter formatter_ = new DefaultNumberFormatter();

    protected static final int DEFAULT_LABEL_WIDTH = 30;
    protected int maxLabelWidth_ = DEFAULT_LABEL_WIDTH;



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
        // left y axis
        g2.drawLine(MARGIN-1, height_ - MARGIN,
                    MARGIN-1, MARGIN);
        // x axis
        g2.drawLine(MARGIN-1,         height_- MARGIN -1,
                    MARGIN-1 + width_, height_ - MARGIN -1);

        g2.drawString("max = " + FormatUtil.formatNumber(yRange.getMax()), MARGIN/3, MARGIN -2);
        g2.drawString("min = " + FormatUtil.formatNumber(yRange.getMin()), MARGIN/3, height_ - MARGIN );
    }

    /**
     * draw a point
     */
    protected void drawLine(Graphics2D g2, double scale,  float xpos, double ypos) {
        double h = (scale * ypos);
        int top = (int)(height_ - h - MARGIN);

        g2.fillOval((int)xpos, top, 3, 3);
    }

    protected void clearBackground(Graphics2D g2) {
        g2.setColor(BACKGROUND_COLOR);
        g2.fillRect( 0, 0, width_, height_ );
    }

    protected abstract Range getRange();
}